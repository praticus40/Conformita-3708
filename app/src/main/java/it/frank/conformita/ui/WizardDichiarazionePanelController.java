package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardDichiarazionePanelController implements Initializable, PraticaPanel {

    private final UiContext context;
    private final PauseTransition autosave = new PauseTransition(Duration.seconds(2));
    private PraticaDto current;

    @FXML
    private CheckBox dichiaraProgettoCheck;

    @FXML
    private CheckBox dichiaraNormaCheck;

    @FXML
    private CheckBox dichiaraMaterialiCheck;

    @FXML
    private CheckBox dichiaraControlliCheck;

    @FXML
    private CheckBox allegatoProgettoCheck;

    @FXML
    private CheckBox allegatoMaterialiCheck;

    @FXML
    private CheckBox allegatoSchemaCheck;

    @FXML
    private TextArea riferimentiField;

    @FXML
    private DatePicker dataDichiarazionePicker;

    @FXML
    private TextField responsabileTecnicoField;

    public WizardDichiarazionePanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autosave.setOnFinished(e -> requestAutosave());
        Runnable schedule = () -> autosave.playFromStart();
        dichiaraProgettoCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        dichiaraNormaCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        dichiaraMaterialiCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        dichiaraControlliCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        allegatoProgettoCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        allegatoMaterialiCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        allegatoSchemaCheck.selectedProperty().addListener((o, a, b) -> schedule.run());
        riferimentiField.textProperty().addListener((o, a, b) -> schedule.run());
        dataDichiarazionePicker.valueProperty().addListener((o, a, b) -> schedule.run());
        responsabileTecnicoField.textProperty().addListener((o, a, b) -> schedule.run());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        current = pratica;
        dichiaraProgettoCheck.setSelected(pratica.dichiaraProgetto());
        dichiaraNormaCheck.setSelected(pratica.dichiaraNorma());
        dichiaraMaterialiCheck.setSelected(pratica.dichiaraMateriali());
        dichiaraControlliCheck.setSelected(pratica.dichiaraControlli());
        allegatoProgettoCheck.setSelected(pratica.allegatoProgetto());
        allegatoMaterialiCheck.setSelected(pratica.allegatoMateriali());
        allegatoSchemaCheck.setSelected(pratica.allegatoSchema());
        riferimentiField.setText(pratica.riferimentiPrecedenti() != null ? pratica.riferimentiPrecedenti() : "");
        dataDichiarazionePicker.setValue(pratica.dataDichiarazione());
        responsabileTecnicoField.setText(
                pratica.responsabileTecnicoNome() != null ? pratica.responsabileTecnicoNome() : "");
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return PraticaDtoCopy.withDichiarazione(
                base,
                dichiaraProgettoCheck.isSelected(),
                dichiaraNormaCheck.isSelected(),
                dichiaraMaterialiCheck.isSelected(),
                dichiaraControlliCheck.isSelected(),
                allegatoProgettoCheck.isSelected(),
                allegatoMaterialiCheck.isSelected(),
                allegatoSchemaCheck.isSelected(),
                emptyToNull(riferimentiField.getText()),
                dataDichiarazionePicker.getValue(),
                emptyToNull(responsabileTecnicoField.getText()));
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
