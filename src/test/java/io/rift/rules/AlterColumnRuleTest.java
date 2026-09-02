package io.rift.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.dialect.sqlserver.SqlServerDialect;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;
import org.junit.jupiter.api.Test;

class AlterColumnRuleTest {

    private final AlterColumnRule rule = new AlterColumnRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsMediumFindingForAlterColumn() {
        SqlStatement statement = new SqlStatement(
                StatementType.ALTER_COLUMN,
                "ALTER TABLE dbo.Orders ALTER COLUMN amount DECIMAL(10,2)",
                47,
                "dbo.Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT005");
        assertThat(finding.name()).isEqualTo("ALTER_COLUMN");
        assertThat(finding.severity()).isEqualTo(Severity.MEDIUM);
        assertThat(finding.line()).isEqualTo(47);
        assertThat(finding.message()).contains("changes the column definition");
    }

    @Test
    void ignoresNonAlterColumnStatements() {
        SqlStatement statement = new SqlStatement(
                StatementType.DROP_COLUMN,
                "ALTER TABLE dbo.Orders DROP COLUMN legacy_code",
                32,
                "dbo.Orders");

        assertThat(rule.supports(statement)).isFalse();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

