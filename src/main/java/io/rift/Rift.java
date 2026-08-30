package io.rift;

import io.rift.cli.RiftCommand;
import picocli.CommandLine;

public final class Rift {

    private Rift() {
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new RiftCommand()).execute(args);
        System.exit(exitCode);
    }
}

