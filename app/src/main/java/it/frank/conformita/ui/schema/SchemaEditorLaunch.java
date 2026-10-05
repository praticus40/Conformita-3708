package it.frank.conformita.ui.schema;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record SchemaEditorLaunch(Path jar, Path javafxModulePath, boolean useJavaFxModules) {

    public List<String> prefixJvmArgs() {
        List<String> args = new ArrayList<>();
        if (useJavaFxModules && javafxModulePath != null) {
            args.add("--module-path");
            args.add(javafxModulePath.toString());
            args.add("--add-modules");
            args.add("javafx.controls,javafx.graphics");
        }
        return args;
    }
}
