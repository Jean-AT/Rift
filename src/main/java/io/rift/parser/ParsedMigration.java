package io.rift.parser;

import java.util.List;

import io.rift.model.SqlStatement;

public record ParsedMigration(String sourceName, List<SqlStatement> statements) {
}

