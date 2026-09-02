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

class DropColumnRuleTest {

    private final DropColumnRule rule = new DropColumnRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsHighFindingForDropColumn() {
        SqlStatement statement = new SqlStatement(
                StatementType.DROP_COLUMN,
                "ALTER TABLE dbo.Orders DROP COLUMN legacy_code",
                32,
                "dbo.Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT004");
        assertThat(finding.name()).isEqualTo("DROP_COLUMN");
        assertThat(finding.severity()).isEqualTo(Severity.HIGH);
        assertThat(finding.line()).isEqualTo(32);
        assertThat(finding.message()).contains("permanently removes the column");
    }

    @Test
    void ignoresNonDropColumnStatements() {
        SqlStatement statement = new SqlStatement(
                StatementType.UPDATE,
                "UPDATE dbo.Orders SET status = 'CANCELLED'",
                7,
                "dbo.Orders");

        assertThat(rule.supports(statement)).isFalse();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

