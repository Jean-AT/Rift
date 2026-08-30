package io.rift.rules;

import java.util.List;

import io.rift.analyzer.AnalysisContext;
import io.rift.model.Finding;
import io.rift.model.SqlStatement;

public interface MigrationRule {

    boolean supports(SqlStatement statement);

    List<Finding> analyze(SqlStatement statement, AnalysisContext context);
}

