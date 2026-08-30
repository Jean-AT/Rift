package io.rift.model;

public record SqlStatement(
        StatementType type,
        String rawSql,
        int startLine,
        String tableName
) {
}

