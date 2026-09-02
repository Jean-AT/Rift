package io.rift.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ScanTargetResolverTest {

    @TempDir
    Path tempDir;

    private final ScanTargetResolver resolver = new ScanTargetResolver();

    @Test
    void resolvesSingleFile() throws IOException {
        Path sqlFile = Files.writeString(tempDir.resolve("migration.sql"), "SELECT 1;");

        List<Path> targets = resolver.resolve(sqlFile);

        assertThat(targets).containsExactly(sqlFile);
    }

    @Test
    void resolvesSqlFilesInDirectory() throws IOException {
        Path nestedDir = Files.createDirectories(tempDir.resolve("nested"));
        Path first = Files.writeString(tempDir.resolve("a.sql"), "SELECT 1;");
        Path second = Files.writeString(nestedDir.resolve("b.sql"), "SELECT 2;");
        Files.writeString(tempDir.resolve("ignore.txt"), "noop");

        List<Path> targets = resolver.resolve(tempDir);

        assertThat(targets).containsExactly(first, second);
    }
}

