package io.rift.model;

public record Finding(
        String id,
        String name,
        Severity severity,
        int line,
        String rawSql,
        String message,
        String impact,
        String recommendation
) {
}

