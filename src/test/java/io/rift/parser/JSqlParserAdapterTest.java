package io.rift.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.rift.model.StatementType;
import org.junit.jupiter.api.Test;

class JSqlParserAdapterTest {

    private final JSqlParserAdapter parser = new JSqlParserAdapter();

    @Test
    void parsesSqlServerBatchSeparatorsAndCapturesLineNumbers() {
        String sql = """
                GO
                UPDATE Orders SET status = 'CANCELLED';
                GO
                ALTER TABLE dbo.Orders
                DROP COLUMN legacy_code;
                """;

        ParsedMigration migration = parser.parse(sql, "migration.sql");

        assertThat(migration.sourceName()).isEqualTo("migration.sql");
        assertThat(migration.statements()).hasSize(2);
        assertThat(migration.statements().get(0).type()).isEqualTo(StatementType.UPDATE);
        assertThat(migration.statements().get(0).tableName()).isEqualTo("Orders");
        assertThat(migration.statements().get(0).startLine()).isEqualTo(2);
        assertThat(migration.statements().get(1).type()).isEqualTo(StatementType.DROP_COLUMN);
        assertThat(migration.statements().get(1).tableName()).isEqualTo("dbo.Orders");
        assertThat(migration.statements().get(1).startLine()).isEqualTo(4);
    }

    @Test
    void parsesAlterColumnStatements() {
        String sql = """
                ALTER TABLE dbo.Orders
                ALTER COLUMN amount DECIMAL(10,2);
                """;

        ParsedMigration migration = parser.parse(sql, "alter.sql");

        assertThat(migration.statements()).hasSize(1);
        assertThat(migration.statements().get(0).type()).isEqualTo(StatementType.ALTER_COLUMN);
        assertThat(migration.statements().get(0).tableName()).isEqualTo("dbo.Orders");
        assertThat(migration.statements().get(0).rawSql()).contains("ALTER TABLE");
    }

    @Test
    void parsesCreateTableAndDropIndexStatements() {
        String sql = """
                CREATE TABLE dbo.Customers (
                    id INT NOT NULL
                );
                DROP INDEX IX_Customers_Name ON dbo.Customers;
                """;

        ParsedMigration migration = parser.parse(sql, "ddl.sql");

        List<StatementType> types = migration.statements().stream().map(statement -> statement.type()).toList();
        assertThat(types).containsExactly(StatementType.CREATE_TABLE, StatementType.DROP_INDEX);
        assertThat(migration.statements().get(0).tableName()).isEqualTo("dbo.Customers");
        assertThat(migration.statements().get(1).tableName()).isEqualTo("dbo.Customers");
    }
}

