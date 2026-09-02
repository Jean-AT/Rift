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

class DeleteWithoutWhereRuleTest {

    private final DeleteWithoutWhereRule rule = new DeleteWithoutWhereRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsCriticalFindingWhenDeleteHasNoWhereClause() {
        SqlStatement statement = new SqlStatement(
                StatementType.DELETE,
                "DELETE FROM Orders",
                8,
                "Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT002");
        assertThat(finding.name()).isEqualTo("DELETE_WITHOUT_WHERE");
        assertThat(finding.severity()).isEqualTo(Severity.CRITICAL);
        assertThat(finding.line()).isEqualTo(8);
        assertThat(finding.message()).contains("does not contain a WHERE clause");
    }

    @Test
    void doesNotReportWhenDeleteHasWhereClause() {
        SqlStatement statement = new SqlStatement(
                StatementType.DELETE,
                "DELETE FROM Orders WHERE id = 1",
                5,
                "Orders");

        assertThat(rule.analyze(statement, context)).isEmpty();
    }

    @Test
    void ignoresNonDeleteStatements() {
        SqlStatement statement = new SqlStatement(
                StatementType.UPDATE,
                "UPDATE Orders SET status = 'CANCELLED'",
                1,
                "Orders");

        assertThat(rule.supports(statement)).isFalse();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

