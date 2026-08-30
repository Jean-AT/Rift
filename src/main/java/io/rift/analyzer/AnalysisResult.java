package io.rift.analyzer;

import java.util.List;

import io.rift.model.Finding;
import io.rift.risk.RiskLevel;

public record AnalysisResult(List<Finding> findings, int riskScore, RiskLevel riskLevel) {
}

