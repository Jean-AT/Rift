package io.rift.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class SqlTextInspector {

    private SqlTextInspector() {
    }

    static boolean containsTopLevelKeyword(String sql, String keyword) {
        if (sql == null || sql.isBlank() || keyword == null || keyword.isBlank()) {
            return false;
        }

        List<String> tokens = topLevelTokens(sql);
        String normalizedKeyword = keyword.toUpperCase(Locale.ROOT);
        return tokens.contains(normalizedKeyword);
    }

    static boolean containsTopLevelPhrase(String sql, String... phrase) {
        if (sql == null || sql.isBlank() || phrase == null || phrase.length == 0) {
            return false;
        }

        String[] normalizedPhrase = new String[phrase.length];
        for (int i = 0; i < phrase.length; i++) {
            if (phrase[i] == null || phrase[i].isBlank()) {
                return false;
            }
            normalizedPhrase[i] = phrase[i].toUpperCase(Locale.ROOT);
        }

        List<String> tokens = topLevelTokens(sql);
        if (tokens.size() < normalizedPhrase.length) {
            return false;
        }

        for (int i = 0; i <= tokens.size() - normalizedPhrase.length; i++) {
            boolean matches = true;
            for (int j = 0; j < normalizedPhrase.length; j++) {
                if (!tokens.get(i + j).equals(normalizedPhrase[j])) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return true;
            }
        }

        return false;
    }

    private static List<String> topLevelTokens(String sql) {
        String normalizedSql = sql.replace("\r\n", "\n").replace('\r', '\n');

        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inBracketQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;
        int depth = 0;

        for (int i = 0; i < normalizedSql.length(); i++) {
            char c = normalizedSql.charAt(i);
            char next = i + 1 < normalizedSql.length() ? normalizedSql.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
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
                    flushToken(token, tokens, depth);
                    inLineComment = true;
                    i++;
                    continue;
                }
                if (c == '/' && next == '*') {
                    flushToken(token, tokens, depth);
                    inBlockComment = true;
                    i++;
                    continue;
                }
            }

            if (!inDoubleQuote && !inBracketQuote && c == '\'') {
                flushToken(token, tokens, depth);
                inSingleQuote = !inSingleQuote;
                continue;
            }

            if (!inSingleQuote && !inBracketQuote && c == '"') {
                flushToken(token, tokens, depth);
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote) {
                if (c == '[') {
                    flushToken(token, tokens, depth);
                    inBracketQuote = true;
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
                flushToken(token, tokens, depth);
                depth++;
                continue;
            }

            if (c == ')') {
                flushToken(token, tokens, depth);
                if (depth > 0) {
                    depth--;
                }
                continue;
            }

            if (depth == 0 && (Character.isLetterOrDigit(c) || c == '_' || c == '$')) {
                token.append(Character.toUpperCase(c));
                continue;
            }

            flushToken(token, tokens, depth);
        }

        flushToken(token, tokens, depth);
        return tokens;
    }

    private static void flushToken(StringBuilder token, List<String> tokens, int depth) {
        if (depth == 0 && !token.isEmpty()) {
            tokens.add(token.toString());
        }
        token.setLength(0);
    }
}

