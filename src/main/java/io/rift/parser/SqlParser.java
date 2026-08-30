package io.rift.parser;

public interface SqlParser {

    ParsedMigration parse(String sql, String sourceName);
}

