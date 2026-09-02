package io.rift.reporter;

import io.rift.analyzer.AnalysisResult;
import picocli.CommandLine;

public final class ConsoleReporter implements Reporter {

    @Override
    public void report(AnalysisResult result) {
        System.out.printf("Risk score: %d%nRisk level: %s%n", result.riskScore(), result.riskLevel());
    }

    public void printUsage(CommandLine commandLine) {
        commandLine.usage(System.out);
    }
}

