package it.frank.conformita.gef;

import it.frank.conformita.core.unifilare.schema.editor.SchemaEditorCliArgs;
import it.frank.conformita.gef.editor.SchemaEditorShell;
import org.eclipse.swt.widgets.Display;

public final class GefSchemaEditorMain {

    private GefSchemaEditorMain() {}

    public static void main(String[] args) {
        int exitCode = 1;
        Display display = new Display();
        try {
            SchemaEditorCliArgs cli = SchemaEditorCliArgs.parse(args);
            SchemaEditorShell shell = new SchemaEditorShell(display, cli);
            shell.openBlocking();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
            exitCode = shell.exitCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            display.dispose();
        }
        System.exit(exitCode);
    }
}
