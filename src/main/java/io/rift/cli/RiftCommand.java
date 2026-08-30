package io.rift.cli;

import io.rift.reporter.ConsoleReporter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
        name = "rift",
        mixinStandardHelpOptions = true,
        version = "Rift 0.1.0",
        subcommands = {
                ScanCommand.class,
                VersionCommand.class
        },
        description = "Static analysis for risky database migrations."
)
public final class RiftCommand implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        new ConsoleReporter().printUsage(spec.commandLine());
    }
}

