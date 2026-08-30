package io.rift.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class SqlScriptSplitter {

    private SqlScriptSplitter() {
    }

    static List<SqlChunk> split(String sql) {
        List<SqlChunk> chunks = new ArrayList<>();
        if (sql == null || sql.isBlank()) {
            return chunks;
        }

        String normalized = sql.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalized.split("\n", -1);

        StringBuilder batch = new StringBuilder();
        int batchStartLine = -1;
        int currentLine = 1;
        for (String line : lines) {
            if (isGoLine(line)) {
                emitBatch(chunks, batch, batchStartLine);
                batch.setLength(0);
                batchStartLine = -1;
                currentLine++;
                continue;
            }

            if (batchStartLine == -1 && !line.isBlank()) {
                batchStartLine = currentLine;
            }

            batch.append(line).append('\n');
            currentLine++;
        }

        emitBatch(chunks, batch, batchStartLine);
        return chunks;
    }

    private static boolean isGoLine(String line) {
        return line.trim().toUpperCase(Locale.ROOT).equals("GO");
    }

    private static void emitBatch(List<SqlChunk> chunks, StringBuilder batch, int batchStartLine) {
        String batchSql = batch.toString().trim();
        if (batchSql.isEmpty()) {
            return;
        }

        int line = batchStartLine == -1 ? 1 : batchStartLine;
        StringBuilder current = new StringBuilder();
        int currentStartLine = line;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < batchSql.length(); i++) {
            char c = batchSql.charAt(i);
            char next = i + 1 < batchSql.length() ? batchSql.charAt(i + 1) : '\0';

            if (c == '\n') {
                if (inLineComment) {
                    inLineComment = false;
                }
                current.append(c);
                line++;
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote) {
                if (!inBlockComment && c == '-' && next == '-') {
                    inLineComment = true;
                } else if (!inLineComment && c == '/' && next == '*') {
                    inBlockComment = true;
                } else if (inBlockComment && c == '*' && next == '/') {
                    current.append(c).append(next);
                    i++;
                    inBlockComment = false;
                    continue;
                }
            }

            if (!inLineComment && !inBlockComment) {
                if (c == '\'' && !inDoubleQuote) {
                    inSingleQuote = !inSingleQuote;
                } else if (c == '"' && !inSingleQuote) {
                    inDoubleQuote = !inDoubleQuote;
                }
            }

            if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment && c == ';') {
                emitStatement(chunks, current, currentStartLine);
                current.setLength(0);
                currentStartLine = line;
                continue;
            }

            if (current.length() == 0 && !Character.isWhitespace(c)) {
                currentStartLine = line;
            }

            if (!inLineComment) {
                current.append(c);
            }
        }

        emitStatement(chunks, current, currentStartLine);
    }

    private static void emitStatement(List<SqlChunk> chunks, StringBuilder current, int startLine) {
        String statement = current.toString().trim();
        if (statement.isEmpty()) {
            return;
        }
        if (statement.endsWith(";")) {
            statement = statement.substring(0, statement.length() - 1).trim();
        }
        if (!statement.isEmpty()) {
            chunks.add(new SqlChunk(statement, startLine));
        }
    }
}

