package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.entity.SistemaDistribuzione;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardRelazioneTecnicaPanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));
    private PraticaDto current;

    @FXML
    private TextField tensioneNominaleField;

    @FXML
    private ComboBox<SistemaDistribuzione> sistemaDistribuzioneCombo;

    @FXML
    private TextField potenzaInstallataField;

    @FXML
    private TextArea descrizioneOpereArea;

    public WizardRelazioneTecnicaPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sistemaDistribuzioneCombo.setItems(FXCollections.observableArrayList(SistemaDistribuzione.values()));
        autosave.setOnFinished(e -> requestAutosave());
        Runnable schedule = () -> autosave.playFromStart();
        tensioneNominaleField.textProperty().addListener((o, a, b) -> schedule.run());
        sistemaDistribuzioneCombo.valueProperty().addListener((o, a, b) -> schedule.run());
        potenzaInstallataField.textProperty().addListener((o, a, b) -> schedule.run());
        descrizioneOpereArea.textProperty().addListener((o, a, b) -> schedule.run());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        current = pratica;
        tensioneNominaleField.setText(pratica.tensioneNominale() != null ? pratica.tensioneNominale() : "");
        sistemaDistribuzioneCombo.setValue(pratica.sistemaDistribuzione());
        potenzaInstallataField.setText(
                pratica.potenzaInstallataKw() != null ? String.valueOf(pratica.potenzaInstallataKw()) : "");
        descrizioneOpereArea.setText(pratica.descrizioneOpere() != null ? pratica.descrizioneOpere() : "");
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withRelazioneTecnica(
                base,
                emptyToNull(tensioneNominaleField.getText()),
                sistemaDistribuzioneCombo.getValue(),
                parseDouble(potenzaInstallataField.getText()),
                emptyToNull(descrizioneOpereArea.getText()));
    }

    private void requestAutosave() {
        if (current != null) {
            context.callbacks().onPraticaUpdated(collectPratica(current));
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static Double parseDouble(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
