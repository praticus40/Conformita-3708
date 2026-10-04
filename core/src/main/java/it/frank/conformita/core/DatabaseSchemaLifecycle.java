package it.frank.conformita.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.OptionalInt;

/**
 * Tracks logical schema version for file-based H2. When {@link #SCHEMA_VERSION} increases, either
 * applies an in-place Hibernate schema update or removes database files (breaking upgrades).
 */
public final class DatabaseSchemaLifecycle {

    /** Libreria schemi + {@code unifilareSchemaId} su pratica; upgrade da v3 in-place. */
    public static final int SCHEMA_VERSION = 4;

    private static final String VERSION_FILE = "schema-version";
    private static final String DB_BASENAME = "conformita";

    private boolean resetPerformed;
    private int previousVersion;

    public boolean wasResetPerformed() {
        return resetPerformed;
    }

    public int getPreviousVersion() {
        return previousVersion;
    }

    /**
     * If stored version is older than {@link #SCHEMA_VERSION}, either wipes H2 files (breaking
     * upgrades) or leaves files for Hibernate {@code update}. Does not write the version file yet.
     */
    public void prepareFileDatabase(Path dataDir) {
        resetPerformed = false;
        previousVersion = readStoredVersion(dataDir).orElse(0);
        if (previousVersion >= SCHEMA_VERSION) {
            return;
        }
        if (requiresFullReset(previousVersion, SCHEMA_VERSION)) {
            deleteH2Files(dataDir);
            resetPerformed = true;
            System.out.printf(
                    "conformita-3708: database reset for schema version %d -> %d%n",
                    previousVersion, SCHEMA_VERSION);
        } else {
            System.out.printf(
                    "conformita-3708: schema upgrade %d -> %d (in-place, dati conservati)%n",
                    previousVersion, SCHEMA_VERSION);
        }
    }

    static boolean requiresFullReset(int fromVersion, int toVersion) {
        if (fromVersion >= toVersion) {
            return false;
        }
        for (int version = fromVersion; version < toVersion; version++) {
            if (requiresFullResetStep(version, version + 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean requiresFullResetStep(int fromVersion, int toVersion) {
        if (fromVersion == 2 && toVersion == 3) {
            return false;
        }
        if (fromVersion == 3 && toVersion == 4) {
            return false;
        }
        return true;
    }

    public void markSchemaApplied(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(dataDir.resolve(VERSION_FILE), Integer.toString(SCHEMA_VERSION), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write schema version to " + dataDir, e);
        }
    }

    static OptionalInt readStoredVersion(Path dataDir) {
        Path file = dataDir.resolve(VERSION_FILE);
        if (!Files.isRegularFile(file)) {
            return OptionalInt.empty();
        }
        try {
            String text = Files.readString(file, StandardCharsets.UTF_8).trim();
            if (text.isEmpty()) {
                return OptionalInt.empty();
            }
            return OptionalInt.of(Integer.parseInt(text));
        } catch (IOException | NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    static void deleteH2Files(Path dataDir) {
        if (!Files.isDirectory(dataDir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir, DB_BASENAME + "*")) {
            for (Path path : stream) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot delete database file: " + path, e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot list database files in " + dataDir, e);
        }
    }
}
