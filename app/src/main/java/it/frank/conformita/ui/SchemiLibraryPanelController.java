package it.frank.conformita.ui;

import it.frank.conformita.core.dto.UnifilareSchemaSummary;
import it.frank.conformita.ui.schema.SchemaEditorProcessLauncher;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class SchemiLibraryPanelController implements Initializable {

    private static final DateTimeFormatter UPDATED =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final UiContext context;

    @FXML
    private TableView<UnifilareSchemaSummary> schemaTable;

    @FXML
    private TableColumn<UnifilareSchemaSummary, String> colName;

    @FXML
    private TableColumn<UnifilareSchemaSummary, String> colUpdated;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnDuplicate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnOpenEditor;

    public SchemiLibraryPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        colName.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().name()));
        colUpdated.setCellValueFactory(row -> new SimpleStringProperty(UPDATED.format(row.getValue().updatedAt())));

        btnNew.setOnAction(e -> createSchema());
        btnDuplicate.setOnAction(e -> duplicateSelected());
        btnDelete.setOnAction(e -> deleteSelected());
        btnOpenEditor.setOnAction(e -> openEditor());
        schemaTable.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> updateButtons());
        updateButtons();
    }

    public void refresh() {
        List<UnifilareSchemaSummary> schemas = context.facade().listSchemaLibrary();
        schemaTable.setItems(FXCollections.observableArrayList(schemas));
        updateButtons();
    }

    private void updateButtons() {
        boolean selected = schemaTable.getSelectionModel().getSelectedItem() != null;
        btnDuplicate.setDisable(!selected);
        btnDelete.setDisable(!selected);
        btnOpenEditor.setDisable(!selected);
    }

    private void createSchema() {
        TextInputDialog dialog = new TextInputDialog("Nuovo schema");
        dialog.setTitle("Nuovo schema");
        dialog.setHeaderText(null);
        dialog.setContentText("Nome:");
        dialog.showAndWait().ifPresent(name -> FxTasks.runAsync(
                () -> context.facade().createSchemaLibraryEntry(name),
                created -> refresh(),
                ex -> showError(ex)));
    }

    private void duplicateSelected() {
        UnifilareSchemaSummary selected = schemaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.name() + " (copia)");
        dialog.setTitle("Duplica schema");
        dialog.setContentText("Nome:");
        dialog.showAndWait().ifPresent(name -> FxTasks.runAsync(
                () -> context.facade().duplicateSchemaLibraryEntry(selected.id(), name),
                dup -> refresh(),
                ex -> showError(ex)));
    }

    private void deleteSelected() {
        UnifilareSchemaSummary selected = schemaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare lo schema \"" + selected.name() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> FxTasks.runAsync(
                () -> {
                    context.facade().deleteSchemaLibraryEntry(selected.id());
                    return null;
                },
                ignored -> refresh(),
                ex -> showError(ex)));
    }

    private void openEditor() {
        UnifilareSchemaSummary selected = schemaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Stage stage = context.callbacks().getStage();
        FxTasks.runAsync(
                () -> SchemaEditorProcessLauncher.openEditorAndWait(context.facade(), stage, selected.id()),
                exitCode -> {
                    refresh();
                    if (exitCode == 0) {
                        context.callbacks().updateStatusMessage("Schema salvato");
                    }
                },
                ex -> showError(ex));
    }

    private void showError(Throwable ex) {
        new Alert(Alert.AlertType.ERROR, ex.getMessage() != null ? ex.getMessage() : ex.toString()).showAndWait();
    }
}
