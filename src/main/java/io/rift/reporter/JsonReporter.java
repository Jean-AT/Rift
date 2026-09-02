package io.rift.reporter;

import io.rift.analyzer.AnalysisResult;

public final class JsonReporter implements Reporter {

    @Override
    public void report(AnalysisResult result) {
        throw new UnsupportedOperationException("JSON reporting is not implemented yet.");
    }
}

