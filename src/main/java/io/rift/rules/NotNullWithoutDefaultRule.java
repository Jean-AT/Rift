package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class NotNullWithoutDefaultRule implements MigrationRule {

    @Override
    public boolean supports(SqlStatement statement) {
        if (statement == null) {
            return false;
        }

        return statement.type() == StatementType.ADD_COLUMN || statement.type() == StatementType.ALTER_COLUMN;
    }

    @Override
    public List<Finding> analyze(SqlStatement statement, AnalysisContext context) {
        if (!supports(statement)) {
            return List.of();
        }

        boolean hasNotNull = SqlTextInspector.containsTopLevelPhrase(statement.rawSql(), "NOT", "NULL");
        boolean hasDefault = SqlTextInspector.containsTopLevelKeyword(statement.rawSql(), "DEFAULT");

        if (!hasNotNull || hasDefault) {
            return List.of();
        }

        return List.of(new Finding(
                "RIFT006",
                "NOT_NULL_WITHOUT_DEFAULT",
                Severity.HIGH,
                statement.startLine(),
                statement.rawSql(),
                "Column is marked NOT NULL without a default value.",
                "Existing rows may fail to backfill or the migration may fail on non-empty tables.",
                "Provide a safe default or backfill existing rows before enforcing NOT NULL."));
    }
}

