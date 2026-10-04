package it.frank.conformita.ui.schema;

import it.frank.conformita.core.ApplicationFacade;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javafx.stage.Stage;

public final class SchemaEditorProcessLauncher {

    private SchemaEditorProcessLauncher() {}

    public static int openEditorAndWait(ApplicationFacade facade, Stage ownerStage, long schemaId) throws IOException, InterruptedException {
        if (facade.isSchemaLibraryLocked(schemaId)) {
            throw new IllegalStateException("Lo schema è già aperto in un altro editor.");
        }

        Path jar = resolveEditorJar();
        Path javaBin = Path.of(System.getProperty("java.home"), "bin", "java.exe");

        double x = ownerStage.getX();
        double y = ownerStage.getY();
        double width = ownerStage.getWidth();
        double height = ownerStage.getHeight();

        List<String> command = new ArrayList<>();
        command.add(javaBin.toString());
        command.add("-jar");
        command.add(jar.toString());
        command.add("--data-dir");
        command.add(facade.getDataDirectory().toString());
        command.add("--schema-id");
        command.add(Long.toString(schemaId));
        command.add("--x");
        command.add(Integer.toString((int) Math.max(0, x)));
        command.add("--y");
        command.add(Integer.toString((int) Math.max(0, y)));
        command.add("--width");
        command.add(Integer.toString((int) Math.max(640, width)));
        command.add("--height");
        command.add(Integer.toString((int) Math.max(480, height)));

        var root = ownerStage.getScene().getRoot();
        root.setDisable(true);
        try {
            Process process = new ProcessBuilder(command).start();
            return process.waitFor();
        } finally {
            root.setDisable(false);
        }
    }

    static Path resolveEditorJar() throws IOException {
        String appHome = System.getProperty("app.home");
        if (appHome != null && !appHome.isBlank()) {
            Path packaged = Path.of(appHome, "lib", "schema-editor-gef.jar");
            if (Files.isRegularFile(packaged)) {
                return packaged;
            }
        }
        Path dev = Path.of("schema-editor-gef", "build", "libs", "schema-editor-gef.jar");
        if (Files.isRegularFile(dev)) {
            return dev.toAbsolutePath().normalize();
        }
        Path root = Path.of(".").toAbsolutePath().normalize();
        try (Stream<Path> walk = Files.walk(root, 6)) {
            return walk.filter(p -> p.getFileName().toString().equals("schema-editor-gef.jar"))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "JAR schema-editor-gef non trovato. Eseguire :schema-editor-gef:shadowJar o installDist."));
        }
    }
}
