package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class DropTableRule implements MigrationRule {

    @Override
    public boolean supports(SqlStatement statement) {
        return statement != null && statement.type() == StatementType.DROP_TABLE;
    }

    @Override
    public List<Finding> analyze(SqlStatement statement, AnalysisContext context) {
        if (!supports(statement)) {
            return List.of();
        }

        return List.of(new Finding(
                "RIFT003",
                "DROP_TABLE",
                Severity.CRITICAL,
                statement.startLine(),
                statement.rawSql(),
                "DROP TABLE removes the table definition and its data.",
                "The target table will no longer exist after deployment.",
                "Confirm that the table is no longer required before deploying."));
    }
}

