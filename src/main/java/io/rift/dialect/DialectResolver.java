package io.rift.dialect;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import io.rift.dialect.postgresql.PostgreSqlDialect;
import io.rift.dialect.sqlserver.SqlServerDialect;

public final class DialectResolver {

    private static final List<String> SUPPORTED_DIALECTS = List.of("sqlserver", "postgresql");

    public Optional<SqlDialect> resolve(String rawDialect) {
        if (rawDialect == null) {
            return Optional.empty();
        }

        String dialect = rawDialect.trim().toLowerCase(Locale.ROOT);
        return switch (dialect) {
            case "sqlserver", "sql-server", "mssql" -> Optional.of(new SqlServerDialect());
            case "postgresql", "postgres", "postgresql12", "postgresql13" -> Optional.of(new PostgreSqlDialect());
            default -> Optional.empty();
        };
    }

    public List<String> supportedDialects() {
        return SUPPORTED_DIALECTS;
    }
}

