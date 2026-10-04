package it.frank.conformita.ui;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.ui.geo.GeoComboSupport;
import it.frank.conformita.ui.geo.ProvinciaItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class ImpresaPanelController implements Initializable {

    private final UiContext context;
    private ImpresaDto loaded = ImpresaDto.empty();

    @FXML
    private TextField ragioneSocialeField;

    @FXML
    private TextField partitaIvaField;

    @FXML
    private TextField sedeLegaleField;

    @FXML
    private TextField reaField;

    @FXML
    private TextField responsabileTecnicoField;

    @FXML
    private TextField legaleRappresentanteField;

    @FXML
    private TextField telefonoField;

    @FXML
    private TextField cciaaField;

    @FXML
    private ComboBox<String> comuneCombo;

    @FXML
    private ComboBox<ProvinciaItem> provinciaCombo;

    @FXML
    private Button salvaButton;

    public ImpresaPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GeoComboSupport.setupProvinciaCombo(provinciaCombo, null);
        GeoComboSupport.setupComuneCombo(comuneCombo, () -> GeoComboSupport.readProvinciaSigla(provinciaCombo), null);
        salvaButton.setOnAction(e -> save());
        loadFromDatabase();
    }

    public void loadFromDatabase() {
        FxTasks.runAsync(
                () -> context.facade().getImpresa().orElse(ImpresaDto.empty()),
                dto -> {
                    loaded = dto;
                    ragioneSocialeField.setText(dto.ragioneSociale());
                    partitaIvaField.setText(dto.partitaIva());
                    sedeLegaleField.setText(dto.sedeLegale());
                    reaField.setText(dto.rea() != null ? dto.rea() : "");
                    responsabileTecnicoField.setText(
                            dto.responsabileTecnico() != null ? dto.responsabileTecnico() : "");
                    legaleRappresentanteField.setText(
                            dto.legaleRappresentante() != null ? dto.legaleRappresentante() : "");
                    telefonoField.setText(dto.telefono() != null ? dto.telefono() : "");
                    GeoComboSupport.setComuneValue(
                            comuneCombo, dto.comune(), () -> GeoComboSupport.readProvinciaSigla(provinciaCombo));
                    GeoComboSupport.setProvinciaValue(provinciaCombo, dto.provincia());
                },
                ex -> context.callbacks().updateStatusMessage("Errore caricamento impresa: " + ex.getMessage()));
    }

    public void save() {
        ImpresaDto dto = collect();
        var check = context.facade().validateImpresaForPersistence(dto);
        if (check.hasErrors()) {
            ValidationAlerts.showPersistErrors("Impossibile salvare l'impresa", check);
            context.callbacks().updateStatusMessage("Correggere i campi dell'impresa prima del salvataggio");
            return;
        }
        FxTasks.runAsync(
                () -> context.facade().saveImpresa(dto),
                saved -> {
                    loaded = saved;
                    context.callbacks().updateStatusMessage("Impresa salvata");
                },
                ex -> context.callbacks().updateStatusMessage("Errore salvataggio impresa: " + ex.getMessage()));
    }

    public ImpresaDto collect() {
        return new ImpresaDto(
                ragioneSocialeField.getText(),
                partitaIvaField.getText(),
                sedeLegaleField.getText(),
                emptyToNull(reaField.getText()),
                emptyToNull(responsabileTecnicoField.getText()),
                emptyToNull(legaleRappresentanteField.getText()),
                loaded.viaSede(),
                loaded.cap(),
                emptyToNull(GeoComboSupport.readComuneText(comuneCombo)),
                emptyToNull(GeoComboSupport.readProvinciaSigla(provinciaCombo)),
                emptyToNull(telefonoField.getText()),
                loaded.email(),
                emptyToNull(cciaaField.getText()),
                loaded.alboArtigianiNumero(),
                loaded.settore() != null ? loaded.settore() : "Impianti Elettrici");
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
