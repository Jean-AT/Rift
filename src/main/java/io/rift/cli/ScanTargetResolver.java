package io.rift.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

final class ScanTargetResolver {

    List<Path> resolve(Path input) throws IOException {
        if (!Files.exists(input)) {
            throw new NoSuchFileException(input.toString());
        }

        if (Files.isRegularFile(input)) {
            return List.of(input);
        }

        if (Files.isDirectory(input)) {
            try (Stream<Path> stream = Files.walk(input)) {
                return stream
                        .filter(Files::isRegularFile)
                        .filter(this::isMigrationFile)
                        .sorted(Comparator.naturalOrder())
                        .toList();
            }
        }

        throw new IOException("Path is neither a regular file nor a directory: " + input);
    }

    private boolean isMigrationFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".sql");
    }
}

