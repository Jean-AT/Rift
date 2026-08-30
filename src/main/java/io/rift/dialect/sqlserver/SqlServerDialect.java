package io.rift.dialect.sqlserver;

import io.rift.dialect.SqlDialect;

public final class SqlServerDialect implements SqlDialect {

    @Override
    public String name() {
        return "sqlserver";
    }
}

