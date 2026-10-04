package it.frank.conformita.ui;

import it.frank.conformita.core.dto.MaterialeRigaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Duration;

public class WizardMaterialiPanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));
    private final ObservableList<MaterialeRigaDto> rows = FXCollections.observableArrayList();
    private PraticaDto current;

    @FXML
    private Button btnAggiungi;

    @FXML
    private Button btnRimuovi;

    @FXML
    private TableView<MaterialeRigaDto> materialiTable;

    @FXML
    private TableColumn<MaterialeRigaDto, String> colDescrizione;

    @FXML
    private TableColumn<MaterialeRigaDto, String> colMarchio;

    @FXML
    private TableColumn<MaterialeRigaDto, String> colModello;

    @FXML
    private TableColumn<MaterialeRigaDto, String> colPosa;

    @FXML
    private TableColumn<MaterialeRigaDto, String> colSigla;

    public WizardMaterialiPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumn(colDescrizione, MaterialeRigaDto::descrizione, (row, v) -> copyRow(row, v, null, null, null, null));
        setupColumn(colMarchio, MaterialeRigaDto::marchio, (row, v) -> copyRow(row, null, v, null, null, null));
        setupColumn(colModello, MaterialeRigaDto::modello, (row, v) -> copyRow(row, null, null, v, null, null));
        setupColumn(colPosa, MaterialeRigaDto::posa, (row, v) -> copyRow(row, null, null, null, v, null));
        setupColumn(colSigla, MaterialeRigaDto::siglaRif, (row, v) -> copyRow(row, null, null, null, null, v));

        materialiTable.setItems(rows);
        autosave.setOnFinished(e -> requestAutosave());
        rows.addListener((javafx.collections.ListChangeListener<MaterialeRigaDto>) c -> autosave.playFromStart());

        btnAggiungi.setOnAction(e -> {
            rows.add(MaterialeRigaDto.empty());
            autosave.playFromStart();
        });
        btnRimuovi.setOnAction(e -> {
            MaterialeRigaDto sel = materialiTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                rows.remove(sel);
            }
        });
    }

    private interface RowUpdater {
        MaterialeRigaDto apply(MaterialeRigaDto row, String value);
    }

    private void setupColumn(
            TableColumn<MaterialeRigaDto, String> col,
            java.util.function.Function<MaterialeRigaDto, String> getter,
            RowUpdater updater) {
        col.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(getter.apply(c.getValue()))));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit(e -> {
            MaterialeRigaDto updated = updater.apply(e.getRowValue(), e.getNewValue());
            int idx = rows.indexOf(e.getRowValue());
            if (idx >= 0) {
                rows.set(idx, updated);
            }
            autosave.playFromStart();
        });
    }

    private static MaterialeRigaDto copyRow(
            MaterialeRigaDto row, String desc, String marchio, String modello, String posa, String sigla) {
        return new MaterialeRigaDto(
                row.id(),
                desc != null ? desc : nullToEmpty(row.descrizione()),
                marchio != null ? marchio : row.marchio(),
                modello != null ? modello : row.modello(),
                posa != null ? posa : row.posa(),
                sigla != null ? sigla : row.siglaRif());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        current = pratica;
        rows.clear();
        if (pratica.materiali() != null) {
            rows.addAll(pratica.materiali());
        }
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withMateriali(base, new ArrayList<>(rows));
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
