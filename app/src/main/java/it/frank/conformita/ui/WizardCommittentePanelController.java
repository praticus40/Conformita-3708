package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.ui.geo.GeoComboSupport;
import it.frank.conformita.ui.geo.ProvinciaItem;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardCommittentePanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));

    @FXML
    private ComboBox<ProvinciaItem> provinciaCombo;

    @FXML
    private ComboBox<String> comuneImpiantoCombo;

    @FXML
    private TextField committenteNomeField;

    @FXML
    private TextField codiceFiscaleField;

    @FXML
    private TextField indirizzoCommittenteField;

    @FXML
    private TextField indirizzoField;

    @FXML
    private ComboBox<String> comuneCombo;

    @FXML
    private TextField capField;

    @FXML
    private TextArea proprietarioArea;

    private PraticaDto current;

    public WizardCommittentePanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autosave.setOnFinished(e -> requestAutosave());
        Runnable schedule = () -> autosave.playFromStart();

        GeoComboSupport.setupProvinciaCombo(provinciaCombo, schedule);
        GeoComboSupport.setupComuneCombo(comuneImpiantoCombo, this::currentProvinciaSigla, schedule);
        GeoComboSupport.setupComuneCombo(comuneCombo, this::currentProvinciaSigla, schedule);

        committenteNomeField.textProperty().addListener((obs, o, n) -> schedule.run());
        codiceFiscaleField.textProperty().addListener((obs, o, n) -> schedule.run());
        indirizzoCommittenteField.textProperty().addListener((obs, o, n) -> schedule.run());
        indirizzoField.textProperty().addListener((obs, o, n) -> schedule.run());
        capField.textProperty().addListener((obs, o, n) -> schedule.run());
        proprietarioArea.textProperty().addListener((obs, o, n) -> schedule.run());
    }

    private String currentProvinciaSigla() {
        return GeoComboSupport.readProvinciaSigla(provinciaCombo);
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        this.current = pratica;
        GeoComboSupport.setProvinciaValue(provinciaCombo, pratica.provincia());
        GeoComboSupport.setComuneValue(
                comuneImpiantoCombo, pratica.comuneImpianto(), this::currentProvinciaSigla);
        committenteNomeField.setText(pratica.committenteNome());
        codiceFiscaleField.setText(pratica.committenteCodiceFiscale() != null ? pratica.committenteCodiceFiscale() : "");
        indirizzoCommittenteField.setText(
                pratica.indirizzoCommittente() != null ? pratica.indirizzoCommittente() : "");
        indirizzoField.setText(pratica.indirizzoImpianto());
        GeoComboSupport.setComuneValue(comuneCombo, pratica.comune(), this::currentProvinciaSigla);
        capField.setText(pratica.cap() != null ? pratica.cap() : "");
        proprietarioArea.setText(
                pratica.proprietarioDescrizione() != null ? pratica.proprietarioDescrizione() : "");
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withCommittente(
                base,
                emptyToNull(GeoComboSupport.readComuneText(comuneImpiantoCombo)),
                emptyToNull(GeoComboSupport.readProvinciaSigla(provinciaCombo)),
                committenteNomeField.getText(),
                emptyToNull(codiceFiscaleField.getText()),
                indirizzoField.getText(),
                emptyToNull(indirizzoCommittenteField.getText()),
                emptyToNull(proprietarioArea.getText()),
                GeoComboSupport.readComuneText(comuneCombo),
                emptyToNull(capField.getText()));
    }

    private void requestAutosave() {
        if (current != null) {
            context.callbacks().onPraticaUpdated(collectPratica(current));
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
