package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.entity.DestinazioneUso;
import it.frank.conformita.core.entity.TensioneImpianto;
import it.frank.conformita.core.entity.TipoIntervento;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardInterventoPanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));

    @FXML
    private TextArea descrizioneImpiantoArea;

    @FXML
    private ComboBox<TipoIntervento> tipoInterventoCombo;

    @FXML
    private CheckBox fotovoltaicoCheck;

    @FXML
    private CheckBox altroInterventoCheck;

    @FXML
    private ComboBox<DestinazioneUso> destinazioneUsoCombo;

    @FXML
    private TextField altriUsiField;

    @FXML
    private DatePicker dataInizioPicker;

    @FXML
    private TextField potenzaKwField;

    @FXML
    private TextField potenzaImpegnabileField;

    @FXML
    private ComboBox<TensioneImpianto> tensioneCombo;

    @FXML
    private TextField normaTecnicaField;

    private PraticaDto current;

    public WizardInterventoPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tipoInterventoCombo.setItems(FXCollections.observableArrayList(TipoIntervento.values()));
        destinazioneUsoCombo.setItems(FXCollections.observableArrayList(DestinazioneUso.values()));
        tensioneCombo.setItems(FXCollections.observableArrayList(TensioneImpianto.values()));

        autosave.setOnFinished(e -> requestAutosave());
        Runnable schedule = () -> autosave.playFromStart();
        descrizioneImpiantoArea.textProperty().addListener((obs, o, n) -> schedule.run());
        tipoInterventoCombo.valueProperty().addListener((obs, o, n) -> schedule.run());
        fotovoltaicoCheck.selectedProperty().addListener((obs, o, n) -> schedule.run());
        altroInterventoCheck.selectedProperty().addListener((obs, o, n) -> schedule.run());
        destinazioneUsoCombo.valueProperty().addListener((obs, o, n) -> schedule.run());
        altriUsiField.textProperty().addListener((obs, o, n) -> schedule.run());
        dataInizioPicker.valueProperty().addListener((obs, o, n) -> schedule.run());
        potenzaKwField.textProperty().addListener((obs, o, n) -> schedule.run());
        potenzaImpegnabileField.textProperty().addListener((obs, o, n) -> schedule.run());
        tensioneCombo.valueProperty().addListener((obs, o, n) -> schedule.run());
        normaTecnicaField.textProperty().addListener((obs, o, n) -> schedule.run());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        this.current = pratica;
        descrizioneImpiantoArea.setText(pratica.descrizioneImpianto() != null ? pratica.descrizioneImpianto() : "");
        tipoInterventoCombo.setValue(pratica.tipoIntervento());
        fotovoltaicoCheck.setSelected(pratica.fotovoltaico());
        altroInterventoCheck.setSelected(pratica.altroIntervento());
        destinazioneUsoCombo.setValue(pratica.destinazioneUso());
        altriUsiField.setText(pratica.altriUsi() != null ? pratica.altriUsi() : "");
        dataInizioPicker.setValue(pratica.dataInizio());
        potenzaKwField.setText(pratica.potenzaKw() != null ? String.valueOf(pratica.potenzaKw()) : "");
        potenzaImpegnabileField.setText(
                pratica.potenzaImpegnabileKw() != null ? String.valueOf(pratica.potenzaImpegnabileKw()) : "");
        tensioneCombo.setValue(pratica.tensione());
        normaTecnicaField.setText(
                pratica.normaTecnica() != null ? pratica.normaTecnica() : "CEI 64-8");
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withIntervento(
                base,
                emptyToNull(descrizioneImpiantoArea.getText()),
                tipoInterventoCombo.getValue(),
                destinazioneUsoCombo.getValue(),
                emptyToNull(altriUsiField.getText()),
                dataInizioPicker.getValue(),
                parsePotenza(potenzaKwField.getText()),
                parsePotenza(potenzaImpegnabileField.getText()),
                base.potenzaInstallataKw(),
                fotovoltaicoCheck.isSelected(),
                altroInterventoCheck.isSelected(),
                tensioneCombo.getValue(),
                emptyToNull(normaTecnicaField.getText()));
    }

    private void requestAutosave() {
        if (current != null) {
            context.callbacks().onPraticaUpdated(collectPratica(current));
        }
    }

    private static Double parsePotenza(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
