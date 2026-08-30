package io.rift.parser;

public final class JSqlParserAdapter implements SqlParser {

    @Override
    public ParsedMigration parse(String sql, String sourceName) {
        throw new UnsupportedOperationException("SQL parsing is not implemented yet.");
    }
}

