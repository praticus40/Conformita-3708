package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.schema.SchemaLibraryFileStorage;
import it.frank.conformita.core.unifilare.schema.SchemaLibraryPaths;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;
import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import it.frank.conformita.core.unifilare.schema.editor.SchemaEditorCliArgs;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class SchemaEditorApplication extends Application {

    private static int exitCode = 1;
    private static SchemaEditorCliArgs cliArgs;

    public static int launchAndWait(String[] args) {
        cliArgs = SchemaEditorCliArgs.parse(args);
        launch(args);
        return exitCode;
    }

    @Override
    public void start(Stage stage) {
        SchemaEditorCliArgs cli = cliArgs;
        SchemaLibraryFileStorage storage = new SchemaLibraryFileStorage(cli.dataDir());
        try {
            storage.acquireLock(cli.schemaId(), "schema-editor-javafx");
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile acquisire lock schema", e);
        }

        UnifilareSchemaDocumentV2 document = loadDocument(storage, cli);
        SchemaDiagramRoot root = new SchemaDiagramRoot(document);

        SchemaCanvasView canvas = new SchemaCanvasView(root);
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setPannable(true);

        SchemaOutlinePane outline = new SchemaOutlinePane();
        outline.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(it.frank.conformita.core.unifilare.schema.GefDiagramNode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String label = item.getLabel();
                    setText((label != null && !label.isBlank() ? label : item.getType()) + " (" + item.getType() + ")");
                }
            }
        });
        outline.setRootModel(root);

        SchemaPropertiesPane properties = new SchemaPropertiesPane();

        boolean[] savedOnExit = {false};
        boolean[] syncingSelection = {false};
        @SuppressWarnings("unchecked")
        Consumer<String>[] addSymbolRef = new Consumer[1];
        addSymbolRef[0] = type -> {};

        MenuItem saveItem = new MenuItem("Salva");
        saveItem.setOnAction(e -> {
            if (saveAndRender(storage, cli, root, savedOnExit)) {
                outline.refreshTree();
            }
        });
        MenuItem closeItem = new MenuItem("Chiudi");
        closeItem.setOnAction(e -> {
            if (confirmClose(stage, savedOnExit, () -> saveAndRender(storage, cli, root, savedOnExit))) {
                try {
                    storage.releaseLock(cli.schemaId());
                } catch (IOException ignored) {
                }
                exitCode = savedOnExit[0] ? 0 : 1;
                stage.close();
            }
        });

        Menu fileMenu = new Menu("File", null, saveItem, new SeparatorMenuItem(), closeItem);
        MenuBar menuBar = new MenuBar(fileMenu);

        ToolBar symbolsBar = buildSymbolToolbar(type -> addSymbolRef[0].accept(type));

        SplitPane right = new SplitPane(outline, properties);
        right.setOrientation(javafx.geometry.Orientation.VERTICAL);
        right.setDividerPositions(0.65);

        SplitPane main = new SplitPane(scroll, right);
        main.setDividerPositions(0.78);

        BorderPane layout = new BorderPane();
        layout.setTop(new VBox(menuBar, symbolsBar));
        layout.setCenter(main);

        canvas.setSelectionListener(nodes -> {
            if (syncingSelection[0]) {
                return;
            }
            syncingSelection[0] = true;
            try {
                outline.selectNode(nodes.isEmpty() ? null : nodes.get(0));
                properties.bindSelection(nodes);
            } finally {
                syncingSelection[0] = false;
            }
        });
        outline.setSelectionListener(node -> {
            if (syncingSelection[0]) {
                return;
            }
            syncingSelection[0] = true;
            try {
                canvas.selectNodeFromOutline(node);
            } finally {
                syncingSelection[0] = false;
            }
        });
        properties.setChangeListener(node -> {
            canvas.refreshNode(node);
            outline.refreshTree();
        });

        addSymbolRef[0] = type -> {
            if (!canvas.addNode(type)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Simbolo");
                alert.setHeaderText(null);
                alert.setContentText(canvas.addBlockedReason(type));
                alert.showAndWait();
                return;
            }
            outline.refreshTree();
        };

        properties.bindSelection(canvas.selection());

        stage.setTitle("Editor schema (JavaFX) — " + cli.schemaId());
        stage.setX(cli.x());
        stage.setY(cli.y());
        stage.setWidth(cli.width());
        stage.setHeight(cli.height());
        stage.setScene(new Scene(layout, cli.width(), cli.height()));
        stage.setOnCloseRequest(e -> {
            if (!confirmClose(stage, savedOnExit, () -> saveAndRender(storage, cli, root, savedOnExit))) {
                e.consume();
                return;
            }
            try {
                storage.releaseLock(cli.schemaId());
            } catch (IOException ignored) {
            }
            exitCode = savedOnExit[0] ? 0 : 1;
        });
        stage.show();
    }

    private static ToolBar buildSymbolToolbar(Consumer<String> onAdd) {
        String[][] entries = {
            {"Generale", "general"},
            {"Linea", "circuit"},
            {"Sbarra collettore", "busbar"},
        };
        ToolBar bar = new ToolBar();
        Label heading = new Label("Simboli");
        HBox box = new HBox(8, heading);
        HBox.setHgrow(box, Priority.ALWAYS);
        bar.getItems().add(box);
        for (String[] entry : entries) {
            Button btn = new Button();
            btn.setGraphic(new javafx.scene.image.ImageView(PaletteIconFactory.iconFor(entry[1], PaletteIconFactory.SIZE_MEDIUM)));
            btn.setTooltip(new Tooltip(entry[0]));
            btn.setOnAction(e -> onAdd.accept(entry[1]));
            bar.getItems().add(btn);
        }
        return bar;
    }

    private static UnifilareSchemaDocumentV2 loadDocument(SchemaLibraryFileStorage storage, SchemaEditorCliArgs cli) {
        try {
            return storage.loadDocument(SchemaLibraryPaths.documentRelativePath(cli.schemaId()));
        } catch (IOException e) {
            throw new IllegalStateException("Caricamento schema " + cli.schemaId(), e);
        }
    }

    private static boolean saveAndRender(
            SchemaLibraryFileStorage storage, SchemaEditorCliArgs cli, SchemaDiagramRoot root, boolean[] savedOnExit) {
        try {
            storage.saveDocument(SchemaLibraryPaths.documentRelativePath(cli.schemaId()), root.document());
            storage.renderPdf(cli.schemaId(), root.document());
            savedOnExit[0] = true;
            exitCode = 0;
            return true;
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Salvataggio");
            alert.setHeaderText(null);
            alert.setContentText("Salvataggio fallito: " + e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    private static boolean confirmClose(Stage stage, boolean[] savedOnExit, Runnable onSave) {
        if (savedOnExit[0]) {
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Chiudi editor");
        alert.setHeaderText(null);
        alert.setContentText("Salvare le modifiche prima di chiudere?");
        alert.getButtonTypes().setAll(
                new javafx.scene.control.ButtonType("Sì", javafx.scene.control.ButtonBar.ButtonData.YES),
                new javafx.scene.control.ButtonType("No", javafx.scene.control.ButtonBar.ButtonData.NO),
                new javafx.scene.control.ButtonType("Annulla", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE));
        var result = alert.showAndWait();
        if (result.isEmpty() || result.get().getButtonData() == javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE) {
            return false;
        }
        if (result.get().getButtonData() == javafx.scene.control.ButtonBar.ButtonData.YES) {
            onSave.run();
        }
        return true;
    }
}
