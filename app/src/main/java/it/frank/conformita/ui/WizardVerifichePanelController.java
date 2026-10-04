package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.dto.VerificaRigaDto;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Duration;

public class WizardVerifichePanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));
    private final ObservableList<VerificaRigaDto> rows = FXCollections.observableArrayList();
    private PraticaDto current;

    @FXML
    private CheckBox provaSezioniCheck;

    @FXML
    private CheckBox provaBagnoCheck;

    @FXML
    private CheckBox provaInterruttoriCheck;

    @FXML
    private Button btnAggiungi;

    @FXML
    private Button btnRimuovi;

    @FXML
    private TableView<VerificaRigaDto> verificheTable;

    @FXML
    private TableColumn<VerificaRigaDto, String> colTipo;

    @FXML
    private TableColumn<VerificaRigaDto, String> colValore;

    @FXML
    private TableColumn<VerificaRigaDto, String> colMarca;

    @FXML
    private TableColumn<VerificaRigaDto, String> colModelloStrumento;

    public WizardVerifichePanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumn(colTipo, VerificaRigaDto::tipoProva, (row, v) -> copyRow(row, v, null, null, null));
        setupColumn(colValore, VerificaRigaDto::valore, (row, v) -> copyRow(row, null, v, null, null));
        setupColumn(colMarca, VerificaRigaDto::marcaStrumento, (row, v) -> copyRow(row, null, null, v, null));
        setupColumn(
                colModelloStrumento, VerificaRigaDto::modelloStrumento, (row, v) -> copyRow(row, null, null, null, v));

        verificheTable.setItems(rows);
        autosave.setOnFinished(e -> requestAutosave());
        rows.addListener((javafx.collections.ListChangeListener<VerificaRigaDto>) c -> autosave.playFromStart());

        Runnable schedule = () -> autosave.playFromStart();
        provaSezioniCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        provaBagnoCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        provaInterruttoriCheck.selectedProperty().addListener((o, a, b) -> schedule.run());

        btnAggiungi.setOnAction(e -> {
            rows.add(VerificaRigaDto.empty());
            autosave.playFromStart();
        });
        btnRimuovi.setOnAction(e -> {
            VerificaRigaDto sel = verificheTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                rows.remove(sel);
            }
        });
    }

    private interface RowUpdater {
        VerificaRigaDto apply(VerificaRigaDto row, String value);
    }

    private void setupColumn(
            TableColumn<VerificaRigaDto, String> col,
            java.util.function.Function<VerificaRigaDto, String> getter,
            RowUpdater updater) {
        col.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(getter.apply(c.getValue()))));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit(e -> {
            VerificaRigaDto updated = updater.apply(e.getRowValue(), e.getNewValue());
            int idx = rows.indexOf(e.getRowValue());
            if (idx >= 0) {
                rows.set(idx, updated);
            }
            autosave.playFromStart();
        });
    }

    private static VerificaRigaDto copyRow(
            VerificaRigaDto row, String tipo, String valore, String marca, String modello) {
        return new VerificaRigaDto(
                row.id(),
                tipo != null ? tipo : nullToEmpty(row.tipoProva()),
                valore != null ? valore : row.valore(),
                marca != null ? marca : row.marcaStrumento(),
                modello != null ? modello : row.modelloStrumento());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        current = pratica;
        provaSezioniCheck.setSelected(pratica.provaVistaSezioni());
        provaBagnoCheck.setSelected(pratica.provaVistaBagno());
        provaInterruttoriCheck.setSelected(pratica.provaVistaInterruttori());
        rows.clear();
        if (pratica.verifiche() != null) {
            rows.addAll(pratica.verifiche());
        }
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withVerifiche(
                base,
                provaSezioniCheck.isSelected(),
                provaBagnoCheck.isSelected(),
                provaInterruttoriCheck.isSelected(),
                new ArrayList<>(rows));
    }

    private void requestAutosave() {
        if (current != null) {
            context.callbacks().onPraticaUpdated(collectPratica(current));
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
