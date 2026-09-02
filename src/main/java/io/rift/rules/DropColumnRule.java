package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class DropColumnRule implements MigrationRule {

    @Override
    public boolean supports(SqlStatement statement) {
        return statement != null && statement.type() == StatementType.DROP_COLUMN;
    }

    @Override
    public List<Finding> analyze(SqlStatement statement, AnalysisContext context) {
        if (!supports(statement)) {
            return List.of();
        }

        return List.of(new Finding(
                "RIFT004",
                "DROP_COLUMN",
                Severity.HIGH,
                statement.startLine(),
                statement.rawSql(),
                "DROP COLUMN permanently removes the column and its data.",
                "Any existing data in the column will be lost.",
                "Confirm that no application or report still depends on the column."));
    }
}

