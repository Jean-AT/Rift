package io.rift.cli;

import java.nio.file.Path;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = "scan",
        description = "Scan a SQL migration file or directory."
)
public final class ScanCommand implements Runnable {

    @Parameters(index = "0", description = "Migration file or directory to analyze.")
    private Path path;

    @Option(names = "--dialect", required = true, description = "SQL dialect, for example sqlserver.")
    private String dialect;

    @Override
    public void run() {
        System.out.printf("Rift scan scaffold only.%nPath: %s%nDialect: %s%n", path, dialect);
    }
}

