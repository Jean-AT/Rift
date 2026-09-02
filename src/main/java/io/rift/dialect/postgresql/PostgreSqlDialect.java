package io.rift.dialect.postgresql;

import io.rift.dialect.SqlDialect;

public final class PostgreSqlDialect implements SqlDialect {

    @Override
    public String name() {
        return "postgresql";
    }
}

