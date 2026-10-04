package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.dto.UnifilareSchemaSummary;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class WizardLibrettoPanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));
    private PraticaDto current;

    @FXML
    private TextField telefonoAssistenzaField;

    @FXML
    private TextField schemaPathField;

    @FXML
    private Button btnScegliSchema;

    @FXML
    private ComboBox<UnifilareSchemaSummary> schemaLibraryCombo;

    @FXML
    private Label schemaLibraryStatusLabel;

    @FXML
    private Label schemaPdfStatusLabel;

    public WizardLibrettoPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autosave.setOnFinished(e -> requestAutosave());
        telefonoAssistenzaField.textProperty().addListener((o, a, b) -> autosave.playFromStart());
        btnScegliSchema.setOnAction(e -> chooseSchema());
        schemaLibraryCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(UnifilareSchemaSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });
        schemaLibraryCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UnifilareSchemaSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Nessuno —" : item.name());
            }
        });
        schemaLibraryCombo.valueProperty().addListener((o, a, b) -> {
            autosave.playFromStart();
            refreshSchemaStatus(current);
        });
        reloadSchemaLibraryItems();
    }

    private void reloadSchemaLibraryItems() {
        FxTasks.runAsync(
                context.facade()::listSchemaLibrary,
                list -> {
                    schemaLibraryCombo.setItems(FXCollections.observableArrayList(list));
                    selectCurrentSchemaInCombo();
                },
                ex -> schemaLibraryStatusLabel.setText("Errore libreria schemi"));
    }

    private void selectCurrentSchemaInCombo() {
        if (current == null || current.unifilareSchemaId() == null) {
            schemaLibraryCombo.getSelectionModel().clearSelection();
            return;
        }
        schemaLibraryCombo.getItems().stream()
                .filter(s -> s.id().equals(current.unifilareSchemaId()))
                .findFirst()
                .ifPresentOrElse(
                        s -> schemaLibraryCombo.getSelectionModel().select(s),
                        () -> schemaLibraryCombo.getSelectionModel().clearSelection());
    }

    private void chooseSchema() {
        if (current == null || current.id() == null) {
            new Alert(Alert.AlertType.WARNING, "Salvare la pratica prima di importare uno schema PDF.").showAndWait();
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Schema impianto (PDF esterno)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showOpenDialog(context.callbacks().getStage());
        if (file != null) {
            try {
                PraticaDto updated = context.facade().importExternalSchemaPdf(current.id(), Path.of(file.toURI()));
                current = updated;
                schemaPathField.setText(displayPath(updated.schemaAllegatoPath()));
                refreshSchemaStatus(updated);
                context.callbacks().onPraticaUpdated(updated);
            } catch (IllegalStateException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        }
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        current = pratica;
        telefonoAssistenzaField.setText(pratica.telefonoAssistenza() != null ? pratica.telefonoAssistenza() : "");
        schemaPathField.setText(displayPath(pratica.schemaAllegatoPath()));
        reloadSchemaLibraryItems();
        refreshSchemaStatus(pratica);
    }

    private void refreshSchemaStatus(PraticaDto pratica) {
        if (pratica == null || pratica.id() == null) {
            schemaLibraryStatusLabel.setText("Schema libreria: —");
            schemaPdfStatusLabel.setText("PDF export: —");
            return;
        }
        UnifilareSchemaSummary selected = schemaLibraryCombo.getValue();
        if (selected != null) {
            schemaLibraryStatusLabel.setText("Schema libreria: " + selected.name() + " (riferimento live)");
            boolean pdf = context.facade().resolveSchemaPdfForExport(
                            PraticaDtoCopy.withUnifilareSchemaId(pratica, selected.id()))
                    .isPresent();
            schemaPdfStatusLabel.setText("PDF export: " + (pdf ? "disponibile" : "mancante"));
        } else if (pratica.unifilareSchemaId() != null) {
            schemaLibraryStatusLabel.setText("Schema libreria: id " + pratica.unifilareSchemaId());
            schemaPdfStatusLabel.setText("PDF export: —");
        } else {
            schemaLibraryStatusLabel.setText("Schema libreria: nessuno");
            schemaPdfStatusLabel.setText(
                    "PDF allegato: " + (context.facade().hasSchemaPdf(pratica.id()) ? "sì" : "no"));
        }
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        PraticaDto source = current != null ? current : base;
        UnifilareSchemaSummary selected = schemaLibraryCombo.getValue();
        Long schemaId = selected != null ? selected.id() : null;
        PraticaDto withSchema = PraticaDtoCopy.withUnifilareSchemaId(base, schemaId);
        if (schemaId != null && !withSchema.allegatoSchema()) {
            withSchema = PraticaDtoCopy.withAllegatoSchema(withSchema, true);
        }
        return PraticaDtoCopy.withLibretto(
                withSchema,
                emptyToNull(telefonoAssistenzaField.getText()),
                source.schemaAllegatoPath(),
                source.schemaUnifilareJsonPath());
    }

    private void requestAutosave() {
        if (current != null) {
            context.callbacks().onPraticaUpdated(collectPratica(current));
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String displayPath(String path) {
        return path == null ? "" : path;
    }
}
