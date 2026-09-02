package io.rift.cli;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.rift.dialect.DialectResolver;
import io.rift.dialect.SqlDialect;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
        name = "scan",
        description = "Scan a SQL migration file or directory."
)
public final class ScanCommand implements java.util.concurrent.Callable<Integer> {

    private static final int IO_ERROR_EXIT_CODE = 74;

    @Parameters(index = "0", description = "Migration file or directory to analyze.")
    private Path path;

    @Option(names = "--dialect", required = true, description = "SQL dialect, for example sqlserver.")
    private String dialect;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        DialectResolver dialectResolver = new DialectResolver();
        Optional<SqlDialect> resolvedDialect = dialectResolver.resolve(dialect);
        if (resolvedDialect.isEmpty()) {
            spec.commandLine().getErr().printf(
                    "Unsupported dialect '%s'. Supported dialects: %s%n",
                    dialect,
                    String.join(", ", dialectResolver.supportedDialects()));
            return CommandLine.ExitCode.USAGE;
        }

        ScanTargetResolver targetResolver = new ScanTargetResolver();
        List<Path> targets;
        try {
            targets = targetResolver.resolve(path);
        } catch (NoSuchFileException e) {
            spec.commandLine().getErr().printf("Input path does not exist: %s%n", path);
            return CommandLine.ExitCode.USAGE;
        } catch (IOException e) {
            spec.commandLine().getErr().printf("Unable to read input path '%s': %s%n", path, e.getMessage());
            return IO_ERROR_EXIT_CODE;
        }

        if (targets.isEmpty()) {
            spec.commandLine().getErr().printf(
                    "No SQL migration files found in '%s'. Only *.sql files are scanned.%n",
                    path);
            return CommandLine.ExitCode.USAGE;
        }

        spec.commandLine().getOut().println("RIFT 0.1.0");
        spec.commandLine().getOut().printf("Path: %s%n", path);
        spec.commandLine().getOut().printf("Dialect: %s%n", resolvedDialect.get().name());
        spec.commandLine().getOut().printf("Files discovered: %d%n", targets.size());
        for (Path target : targets) {
            spec.commandLine().getOut().printf("- %s%n", target);
        }

        return CommandLine.ExitCode.OK;
    }
}
