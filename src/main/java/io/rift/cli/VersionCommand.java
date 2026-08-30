package io.rift.cli;

import picocli.CommandLine.Command;

@Command(
        name = "version",
        description = "Print the Rift version."
)
public final class VersionCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Rift 0.1.0");
    }
}

