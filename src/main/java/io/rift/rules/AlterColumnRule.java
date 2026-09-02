package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class AlterColumnRule implements MigrationRule {

    @Override
    public boolean supports(SqlStatement statement) {
        return statement != null && statement.type() == StatementType.ALTER_COLUMN;
    }

    @Override
    public List<Finding> analyze(SqlStatement statement, AnalysisContext context) {
        if (!supports(statement)) {
            return List.of();
        }

        return List.of(new Finding(
                "RIFT005",
                "ALTER_COLUMN",
                Severity.MEDIUM,
                statement.startLine(),
                statement.rawSql(),
                "ALTER COLUMN changes the column definition.",
                "The change may cause truncation, precision loss, or application incompatibilities.",
                "Verify the new definition against existing data and application usage."));
    }
}

