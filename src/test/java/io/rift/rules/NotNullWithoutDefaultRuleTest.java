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

class NotNullWithoutDefaultRuleTest {

    private final NotNullWithoutDefaultRule rule = new NotNullWithoutDefaultRule();
    private final AnalysisContext context = new AnalysisContext(new SqlServerDialect(), "migration.sql");

    @Test
    void reportsHighFindingForAlterColumnWithoutDefault() {
        SqlStatement statement = new SqlStatement(
                StatementType.ALTER_COLUMN,
                "ALTER TABLE dbo.Orders ALTER COLUMN status NVARCHAR(20) NOT NULL",
                47,
                "dbo.Orders");

        List<Finding> findings = rule.analyze(statement, context);

        assertThat(findings).hasSize(1);
        Finding finding = findings.getFirst();
        assertThat(finding.id()).isEqualTo("RIFT006");
        assertThat(finding.name()).isEqualTo("NOT_NULL_WITHOUT_DEFAULT");
        assertThat(finding.severity()).isEqualTo(Severity.HIGH);
        assertThat(finding.line()).isEqualTo(47);
        assertThat(finding.message()).contains("without a default value");
    }

    @Test
    void doesNotReportWhenDefaultIsPresent() {
        SqlStatement statement = new SqlStatement(
                StatementType.ADD_COLUMN,
                "ALTER TABLE dbo.Orders ADD status NVARCHAR(20) NOT NULL DEFAULT 'PENDING'",
                18,
                "dbo.Orders");

        assertThat(rule.analyze(statement, context)).isEmpty();
    }

    @Test
    void ignoresStatementsWithoutNotNull() {
        SqlStatement statement = new SqlStatement(
                StatementType.ALTER_COLUMN,
                "ALTER TABLE dbo.Orders ALTER COLUMN status NVARCHAR(20) NULL",
                9,
                "dbo.Orders");

        assertThat(rule.supports(statement)).isTrue();
        assertThat(rule.analyze(statement, context)).isEmpty();
    }
}

