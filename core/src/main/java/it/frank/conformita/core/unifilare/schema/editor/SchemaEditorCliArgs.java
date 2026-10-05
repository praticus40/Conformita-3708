package it.frank.conformita.core.unifilare.schema.editor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class SchemaEditorCliArgs {

    private final Path dataDir;
    private final long schemaId;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean exitOnClose;

    private SchemaEditorCliArgs(
            Path dataDir, long schemaId, int x, int y, int width, int height, boolean exitOnClose) {
        this.dataDir = dataDir;
        this.schemaId = schemaId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.exitOnClose = exitOnClose;
    }

    public static SchemaEditorCliArgs parse(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                map.put(args[i], args[++i]);
            } else if ("--exit-on-close".equals(args[i])) {
                map.put("--exit-on-close", "true");
            }
        }
        String dataDir = map.get("--data-dir");
        String schemaId = map.get("--schema-id");
        if (dataDir == null || dataDir.isBlank()) {
            throw new IllegalArgumentException("--data-dir obbligatorio");
        }
        if (schemaId == null || schemaId.isBlank()) {
            throw new IllegalArgumentException("--schema-id obbligatorio");
        }
        int x = parseInt(map.get("--x"), 100);
        int y = parseInt(map.get("--y"), 100);
        int width = parseInt(map.get("--width"), 1200);
        int height = parseInt(map.get("--height"), 800);
        boolean exitOnClose = !"false".equalsIgnoreCase(map.get("--exit-on-close"));
        Path dataDirPath;
        try {
            dataDirPath = Path.of(dataDir).toAbsolutePath().normalize();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "--data-dir non valido: \""
                            + dataDir
                            + "\" (usare un percorso reale, non il placeholder <data> della documentazione)",
                    e);
        }
        return new SchemaEditorCliArgs(
                dataDirPath,
                Long.parseLong(schemaId),
                x,
                y,
                width,
                height,
                exitOnClose);
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public Path dataDir() {
        return dataDir;
    }

    public long schemaId() {
        return schemaId;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean exitOnClose() {
        return exitOnClose;
    }
}
