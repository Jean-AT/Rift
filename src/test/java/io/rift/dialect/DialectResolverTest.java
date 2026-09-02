package io.rift.dialect;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DialectResolverTest {

    private final DialectResolver resolver = new DialectResolver();

    @Test
    void resolvesSqlServerAliases() {
        assertThat(resolver.resolve("sqlserver")).isPresent();
        assertThat(resolver.resolve("SQL-SERVER")).isPresent();
        assertThat(resolver.resolve("mssql")).isPresent();
    }

    @Test
    void resolvesPostgreSqlAliases() {
        assertThat(resolver.resolve("postgresql")).isPresent();
        assertThat(resolver.resolve("postgres")).isPresent();
    }

    @Test
    void rejectsUnsupportedDialect() {
        assertThat(resolver.resolve("oracle")).isEmpty();
    }
}

