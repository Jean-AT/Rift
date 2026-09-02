package io.rift.rules;

import java.util.Locale;

final class SqlTextInspector {

    private SqlTextInspector() {
    }

    static boolean containsTopLevelKeyword(String sql, String keyword) {
        if (sql == null || sql.isBlank() || keyword == null || keyword.isBlank()) {
            return false;
        }

        String normalizedKeyword = keyword.toUpperCase(Locale.ROOT);
        String normalizedSql = sql.replace("\r\n", "\n").replace('\r', '\n');

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inBracketQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;
        int depth = 0;
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < normalizedSql.length(); i++) {
            char c = normalizedSql.charAt(i);
            char next = i + 1 < normalizedSql.length() ? normalizedSql.charAt(i + 1) : '\0';

            if (c == '\n') {
                if (inLineComment) {
                    inLineComment = false;
                }
                flushToken(token, normalizedKeyword, depth);
                continue;
            }

            if (inLineComment) {
                continue;
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote && !inBracketQuote) {
                if (c == '-' && next == '-') {
                    inLineComment = true;
                    flushToken(token, normalizedKeyword, depth);
                    i++;
                    continue;
                }
                if (c == '/' && next == '*') {
                    inBlockComment = true;
                    flushToken(token, normalizedKeyword, depth);
                    i++;
                    continue;
                }
            }

            if (!inDoubleQuote && !inBracketQuote && c == '\'') {
                inSingleQuote = !inSingleQuote;
                flushToken(token, normalizedKeyword, depth);
                continue;
            }

            if (!inSingleQuote && !inBracketQuote && c == '"') {
                inDoubleQuote = !inDoubleQuote;
                flushToken(token, normalizedKeyword, depth);
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote) {
                if (c == '[') {
                    inBracketQuote = true;
                    flushToken(token, normalizedKeyword, depth);
                    continue;
                }
                if (c == ']' && inBracketQuote) {
                    inBracketQuote = false;
                    continue;
                }
            }

            if (inSingleQuote || inDoubleQuote || inBracketQuote) {
                continue;
            }

            if (c == '(') {
                flushToken(token, normalizedKeyword, depth);
                depth++;
                continue;
            }

            if (c == ')') {
                flushToken(token, normalizedKeyword, depth);
                if (depth > 0) {
                    depth--;
                }
                continue;
            }

            if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
                token.append(Character.toUpperCase(c));
            } else {
                if (flushToken(token, normalizedKeyword, depth)) {
                    return true;
                }
            }
        }

        return flushToken(token, normalizedKeyword, depth);
    }

    private static boolean flushToken(StringBuilder token, String keyword, int depth) {
        if (token.isEmpty()) {
            return false;
        }

        String current = token.toString();
        token.setLength(0);
        return depth == 0 && current.equals(keyword);
    }
}

