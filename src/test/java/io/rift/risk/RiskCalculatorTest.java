package io.rift.risk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.rift.model.Finding;
import io.rift.model.Severity;
import org.junit.jupiter.api.Test;

class RiskCalculatorTest {

    private final RiskCalculator calculator = new RiskCalculator();

    @Test
    void calculatesScoreFromFindings() {
        int score = calculator.calculateScore(List.of(
                finding(Severity.CRITICAL),
                finding(Severity.HIGH),
                finding(Severity.MEDIUM)));

        assertThat(score).isEqualTo(100);
    }

    @Test
    void mapsScoreToRiskLevel() {
        assertThat(calculator.calculateRiskLevel(10)).isEqualTo(RiskLevel.LOW);
        assertThat(calculator.calculateRiskLevel(30)).isEqualTo(RiskLevel.MODERATE);
        assertThat(calculator.calculateRiskLevel(60)).isEqualTo(RiskLevel.HIGH);
        assertThat(calculator.calculateRiskLevel(80)).isEqualTo(RiskLevel.CRITICAL);
    }

    private static Finding finding(Severity severity) {
        return new Finding(
                "TEST",
                "TEST",
                severity,
                1,
                "SELECT 1",
                "message",
                "impact",
                "recommendation");
    }
}
