package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.Severity;
import io.rift.model.SqlStatement;
import io.rift.model.StatementType;

public final class DeleteWithoutWhereRule implements MigrationRule {

    @Override
    public boolean supports(SqlStatement statement) {
        return statement != null && statement.type() == StatementType.DELETE;
    }

    @Override
    public List<Finding> analyze(SqlStatement statement, AnalysisContext context) {
        if (!supports(statement)) {
            return List.of();
        }

        if (SqlTextInspector.containsTopLevelKeyword(statement.rawSql(), "WHERE")) {
            return List.of();
        }

        return List.of(new Finding(
                "RIFT002",
                "DELETE_WITHOUT_WHERE",
                Severity.CRITICAL,
                statement.startLine(),
                statement.rawSql(),
                "DELETE statement does not contain a WHERE clause.",
                "This operation may remove every row in the target table.",
                "Verify that the statement intentionally targets all rows."));
    }
}

