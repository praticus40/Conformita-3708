package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.entity.TipoIntervento;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

public class PratichePanelController implements Initializable {

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final UiContext context;

    @FXML
    private Button btnNuova;

    @FXML
    private Button btnElimina;

    @FXML
    private Button btnEsporta;

    @FXML
    private Button btnDettaglio;

    @FXML
    private Button btnAggiorna;

    @FXML
    private TableView<PraticaSummary> praticheTable;

    @FXML
    private TableColumn<PraticaSummary, String> colCommittente;

    @FXML
    private TableColumn<PraticaSummary, String> colIndirizzo;

    @FXML
    private TableColumn<PraticaSummary, String> colTipo;

    @FXML
    private TableColumn<PraticaSummary, String> colAggiornata;

    public PratichePanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCommittente.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue() != null ? nullToDash(cell.getValue().committenteNome()) : ""));
        colIndirizzo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue() != null ? nullToDash(cell.getValue().indirizzoImpianto()) : ""));
        colTipo.setCellValueFactory(cell -> {
            if (cell.getValue() == null) {
                return new SimpleStringProperty("");
            }
            TipoIntervento tipo = cell.getValue().tipoIntervento();
            return new SimpleStringProperty(tipo != null ? tipo.name() : "—");
        });
        colAggiornata.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue() != null && cell.getValue().updatedAt() != null
                        ? DATE_TIME.format(cell.getValue().updatedAt())
                        : "—"));

        praticheTable.setRowFactory(tv -> {
            TableRow<PraticaSummary> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    context.callbacks().openPratica(row.getItem().id());
                }
            });
            return row;
        });

        btnNuova.setOnAction(e -> context.callbacks().createNewPraticaFromHub());
        btnDettaglio.setOnAction(e -> selected().ifPresent(s -> context.callbacks().openPratica(s.id())));
        btnElimina.setOnAction(e -> selected().ifPresent(context.callbacks()::deletePratica));
        btnEsporta.setOnAction(e -> selected().ifPresent(context.callbacks()::exportPraticaFromHub));
        btnAggiorna.setOnAction(e -> refresh());

        refresh();
    }

    public void refresh() {
        FxTasks.runAsync(
                () -> context.facade().listPratiche(),
                list -> praticheTable.setItems(FXCollections.observableArrayList(list)),
                ex -> context.callbacks().updateStatusMessage("Errore elenco pratiche: " + ex.getMessage()));
    }

    private java.util.Optional<PraticaSummary> selected() {
        return java.util.Optional.ofNullable(praticheTable.getSelectionModel().getSelectedItem());
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
