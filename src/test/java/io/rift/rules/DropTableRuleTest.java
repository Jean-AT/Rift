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

class DropTableRuleTest {

    private final DropTableRule rule = new DropTableRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsCriticalFindingForDropTable() {
        SqlStatement statement = new SqlStatement(
                StatementType.DROP_TABLE,
                "DROP TABLE dbo.Orders",
                21,
                "dbo.Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT003");
        assertThat(finding.name()).isEqualTo("DROP_TABLE");
        assertThat(finding.severity()).isEqualTo(Severity.CRITICAL);
        assertThat(finding.line()).isEqualTo(21);
        assertThat(finding.message()).contains("removes the table definition");
    }

    @Test
    void ignoresNonDropTableStatements() {
        SqlStatement statement = new SqlStatement(
                StatementType.DROP_COLUMN,
                "ALTER TABLE dbo.Orders DROP COLUMN legacy_code",
                10,
                "dbo.Orders");

        assertThat(rule.supports(statement)).isFalse();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

