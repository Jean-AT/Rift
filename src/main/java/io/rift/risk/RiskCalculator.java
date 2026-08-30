package io.rift.risk;

import java.util.List;

import io.rift.model.Finding;
import io.rift.model.Severity;

public final class RiskCalculator {

    public int calculateScore(List<Finding> findings) {
        int score = findings.stream()
                .mapToInt(finding -> switch (finding.severity()) {
                    case INFO -> 0;
                    case LOW -> 10;
                    case MEDIUM -> 35;
                    case HIGH -> 65;
                    case CRITICAL -> 90;
                })
                .sum();
        return Math.min(score, 100);
    }

    public RiskLevel calculateRiskLevel(int score) {
        if (score >= 80) {
            return RiskLevel.CRITICAL;
        }
        if (score >= 60) {
            return RiskLevel.HIGH;
        }
        if (score >= 30) {
            return RiskLevel.MODERATE;
        }
        return RiskLevel.LOW;
    }
}
