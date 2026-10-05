package it.frank.conformita.ui.schema;

import it.frank.conformita.core.ApplicationFacade;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javafx.stage.Stage;

public final class SchemaEditorProcessLauncher {

    public static final String EDITOR_PROPERTY = "schema.editor";

    private SchemaEditorProcessLauncher() {}

    public static int openEditorAndWait(ApplicationFacade facade, Stage ownerStage, long schemaId)
            throws IOException, InterruptedException {
        if (facade.isSchemaLibraryLocked(schemaId)) {
            throw new IllegalStateException("Lo schema è già aperto in un altro editor.");
        }

        SchemaEditorLaunch launch = resolveEditorLaunch();
        Path javaBin = Path.of(System.getProperty("java.home"), "bin", "java.exe");

        double x = ownerStage.getX();
        double y = ownerStage.getY();
        double width = ownerStage.getWidth();
        double height = ownerStage.getHeight();

        List<String> command = new ArrayList<>();
        command.add(javaBin.toString());
        command.addAll(launch.prefixJvmArgs());
        command.add("-jar");
        command.add(launch.jar().toString());
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

    static SchemaEditorLaunch resolveEditorLaunch() throws IOException {
        if (useJavaFxEditor()) {
            Path jar = resolveJar("schema-editor-javafx.jar", "schema-editor-javafx");
            Path modulePath = resolveJavafxModulePath();
            if (modulePath == null) {
                throw new IOException(
                        "Moduli JavaFX (win) non trovati. Da sviluppo: gradlew :app:run "
                                + "(copia automatica in app/build/javafx-modules) oppure gradlew :app:installDist.");
            }
            return new SchemaEditorLaunch(jar, modulePath, true);
        }
        Path jar = resolveJar("schema-editor-gef.jar", "schema-editor-gef");
        return new SchemaEditorLaunch(jar, null, false);
    }

    static boolean useJavaFxEditor() {
        String env = System.getenv("SCHEMA_EDITOR");
        if (env != null && !env.isBlank()) {
            return "javafx".equalsIgnoreCase(env.trim());
        }
        String value = System.getProperty(EDITOR_PROPERTY, "gef");
        return "javafx".equalsIgnoreCase(value.trim());
    }

    static Path resolveJar(String jarFileName, String moduleDirName) throws IOException {
        String appHome = System.getProperty("app.home");
        if (appHome != null && !appHome.isBlank()) {
            Path packaged = Path.of(appHome, "lib", jarFileName);
            if (Files.isRegularFile(packaged)) {
                return packaged;
            }
        }
        Path dev = Path.of(moduleDirName, "build", "libs", jarFileName);
        if (Files.isRegularFile(dev)) {
            return dev.toAbsolutePath().normalize();
        }
        Path devFromApp = Path.of("..", moduleDirName, "build", "libs", jarFileName);
        if (Files.isRegularFile(devFromApp)) {
            return devFromApp.toAbsolutePath().normalize();
        }
        Path root = Path.of(".").toAbsolutePath().normalize();
        try (Stream<Path> walk = Files.walk(root, 6)) {
            return walk.filter(p -> p.getFileName().toString().equals(jarFileName))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "JAR " + jarFileName + " non trovato. Eseguire shadowJar del modulo " + moduleDirName + "."));
        }
    }

    static Path resolveJavafxModulePath() throws IOException {
        String appHome = System.getProperty("app.home");
        if (appHome != null && !appHome.isBlank()) {
            Path packaged = Path.of(appHome, "lib", "javafx");
            if (Files.isDirectory(packaged) && containsJavafxJars(packaged)) {
                return packaged.toAbsolutePath().normalize();
            }
        }
        Path dev = Path.of("app", "build", "install", "conformita-3708", "lib", "javafx");
        if (Files.isDirectory(dev) && containsJavafxJars(dev)) {
            return dev.toAbsolutePath().normalize();
        }
        Path devFromRoot = Path.of("build", "install", "conformita-3708", "lib", "javafx");
        if (Files.isDirectory(devFromRoot) && containsJavafxJars(devFromRoot)) {
            return devFromRoot.toAbsolutePath().normalize();
        }
        Path devSync = Path.of("app", "build", "javafx-modules");
        if (Files.isDirectory(devSync) && containsJavafxJars(devSync)) {
            return devSync.toAbsolutePath().normalize();
        }
        Path devSyncFromApp = Path.of("build", "javafx-modules");
        if (Files.isDirectory(devSyncFromApp) && containsJavafxJars(devSyncFromApp)) {
            return devSyncFromApp.toAbsolutePath().normalize();
        }
        return null;
    }

    private static boolean containsJavafxJars(Path dir) throws IOException {
        try (Stream<Path> walk = Files.list(dir)) {
            return walk.anyMatch(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).startsWith("javafx-"));
        }
    }
}
