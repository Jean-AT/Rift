package io.rift.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.dialect.sqlserver.SqlServerDialect;
import io.rift.model.Finding;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;
import org.junit.jupiter.api.Test;

class UpdateWithoutWhereRuleTest {

    private final UpdateWithoutWhereRule rule = new UpdateWithoutWhereRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsCriticalFindingWhenUpdateHasNoWhereClause() {
        SqlStatement statement = new SqlStatement(
                StatementType.UPDATE,
                "UPDATE Orders SET status = 'CANCELLED'",
                12,
                "Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT001");
        assertThat(finding.name()).isEqualTo("UPDATE_WITHOUT_WHERE");
        assertThat(finding.severity()).isEqualTo(io.rift.model.Severity.CRITICAL);
        assertThat(finding.line()).isEqualTo(12);
        assertThat(finding.message()).contains("does not contain a WHERE clause");
    }

    @Test
    void doesNotReportWhenUpdateHasWhereClause() {
        SqlStatement statement = new SqlStatement(
                StatementType.UPDATE,
                "UPDATE Orders SET status = 'CANCELLED' WHERE id = 1",
                4,
                "Orders");

        assertThat(rule.analyze(statement, context)).isEmpty();
    }

    @Test
    void ignoresNonUpdateStatements() {
        SqlStatement statement = new SqlStatement(
                StatementType.DELETE,
                "DELETE FROM Orders",
                1,
                "Orders");

        assertThat(rule.supports(statement)).isFalse();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

