package io.rift.reporter;

import io.rift.analyzer.AnalysisResult;

public interface Reporter {

    void report(AnalysisResult result);
}

