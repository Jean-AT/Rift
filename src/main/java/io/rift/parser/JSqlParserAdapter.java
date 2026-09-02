package io.rift.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.schema.Table;

import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class JSqlParserAdapter implements SqlParser {

    private static final Pattern SQL_SERVER_DROP_INDEX_PATTERN = Pattern.compile(
            "(?is)^drop\\s+index\\s+.+?\\s+on\\s+(.+)$");

    @Override
    public ParsedMigration parse(String sql, String sourceName) {
        List<SqlStatement> statements = new ArrayList<>();
        for (SqlChunk chunk : SqlScriptSplitter.split(sql)) {
            statements.add(parseChunk(chunk));
        }
        return new ParsedMigration(sourceName, List.copyOf(statements));
    }

    private SqlStatement parseChunk(SqlChunk chunk) {
        String normalizedSql = chunk.sql().trim();
        try {
            Statement statement = CCJSqlParserUtil.parse(normalizedSql);
            return mapStatement(statement, normalizedSql, chunk.startLine());
        } catch (JSQLParserException ex) {
            return parseSqlServerFallback(normalizedSql, chunk.startLine());
        }
    }

    private SqlStatement parseSqlServerFallback(String rawSql, int startLine) {
        Matcher dropIndexMatcher = SQL_SERVER_DROP_INDEX_PATTERN.matcher(rawSql);
        if (dropIndexMatcher.matches()) {
            return new SqlStatement(
                    StatementType.DROP_INDEX,
                    rawSql,
                    startLine,
                    normalizeTableName(dropIndexMatcher.group(1)));
        }

        return new SqlStatement(StatementType.UNKNOWN, rawSql, startLine, null);
    }

    private SqlStatement mapStatement(Statement statement, String rawSql, int startLine) {
        if (statement instanceof Update update) {
            return new SqlStatement(StatementType.UPDATE, rawSql, startLine, tableName(update.getTable()));
        }
        if (statement instanceof Delete delete) {
            return new SqlStatement(StatementType.DELETE, rawSql, startLine, tableName(delete.getTable()));
        }
        if (statement instanceof CreateTable createTable) {
            return new SqlStatement(StatementType.CREATE_TABLE, rawSql, startLine, tableName(createTable.getTable()));
        }
        if (statement instanceof CreateIndex createIndex) {
            return new SqlStatement(StatementType.CREATE_INDEX, rawSql, startLine, tableName(createIndex.getTable()));
        }
        if (statement instanceof Drop drop) {
            return new SqlStatement(dropType(drop), rawSql, startLine, tableName(drop.getName()));
        }
        if (statement instanceof Alter alter) {
            return new SqlStatement(alterType(alter), rawSql, startLine, tableName(alter.getTable()));
        }
        if (statement instanceof Insert insert) {
            return new SqlStatement(StatementType.INSERT, rawSql, startLine, tableName(insert.getTable()));
        }
        if (statement instanceof Select) {
            return new SqlStatement(StatementType.SELECT, rawSql, startLine, null);
        }
        return new SqlStatement(StatementType.UNKNOWN, rawSql, startLine, null);
    }

    private StatementType alterType(Alter alter) {
        StatementType fallback = StatementType.ALTER_TABLE;
        for (AlterExpression expression : alter.getAlterExpressions()) {
            AlterOperation operation = expression.getOperation();
            if (operation == null) {
                continue;
            }
            switch (operation) {
                case ADD:
                    return StatementType.ADD_COLUMN;
                case DROP:
                    return StatementType.DROP_COLUMN;
                case ALTER:
                case MODIFY:
                case CHANGE:
                case CONVERT:
                case COLLATE:
                    return StatementType.ALTER_COLUMN;
                case RENAME:
                case RENAME_TABLE:
                case RENAME_INDEX:
                case RENAME_KEY:
                case RENAME_CONSTRAINT:
                case SET_TABLE_OPTION:
                case ENGINE:
                case FORCE:
                case KEY_BLOCK_SIZE:
                case LOCK:
                case DISABLE_KEYS:
                case ENABLE_KEYS:
                    fallback = StatementType.ALTER_TABLE;
                    break;
                default:
                    break;
            }
        }
        return fallback;
    }

    private StatementType dropType(Drop drop) {
        String type = drop.getType();
        if (type == null) {
            return StatementType.UNKNOWN;
        }
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("INDEX")) {
            return StatementType.DROP_INDEX;
        }
        if (normalized.contains("TABLE")) {
            return StatementType.DROP_TABLE;
        }
        return StatementType.UNKNOWN;
    }

    private String tableName(Table table) {
        if (table == null) {
            return null;
        }
        return normalizeTableName(table.getFullyQualifiedName());
    }

    private String normalizeTableName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }
}
