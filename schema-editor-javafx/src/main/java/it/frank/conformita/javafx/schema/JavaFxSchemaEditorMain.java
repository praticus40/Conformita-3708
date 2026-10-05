package it.frank.conformita.javafx.schema;

public final class JavaFxSchemaEditorMain {

    private JavaFxSchemaEditorMain() {}

    public static void main(String[] args) {
        int exitCode = 1;
        try {
            exitCode = SchemaEditorApplication.launchAndWait(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(exitCode);
    }
}
