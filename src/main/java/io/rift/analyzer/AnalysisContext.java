package io.rift.analyzer;

import io.rift.dialect.SqlDialect;

public record AnalysisContext(SqlDialect dialect, String sourceName) {
}

