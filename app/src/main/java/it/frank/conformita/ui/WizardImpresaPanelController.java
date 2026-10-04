package it.frank.conformita.ui;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.PraticaDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardImpresaPanelController implements Initializable, PraticaPanel {

    private final UiContext context;

    @FXML
    private Label ragioneSocialeLabel;

    @FXML
    private Label partitaIvaLabel;

    @FXML
    private Label sedeLegaleLabel;

    @FXML
    private Label warningLabel;

    @FXML
    private Button vaiImpresaButton;

    private PraticaDto current;

    public WizardImpresaPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vaiImpresaButton.setOnAction(e -> context.callbacks().openImpresaSection());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        this.current = pratica;
        refreshImpresaSummary();
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return base;
    }

    public void refreshImpresaSummary() {
        FxTasks.runAsync(
                () -> context.facade().getImpresa(),
                opt -> {
                    if (opt.isEmpty()) {
                        warningLabel.setText("Anagrafica impresa mancante. Vai a Impresa per compilarla.");
                        ragioneSocialeLabel.setText("—");
                        partitaIvaLabel.setText("—");
                        sedeLegaleLabel.setText("—");
                        return;
                    }
                    ImpresaDto dto = opt.get();
                    warningLabel.setText("");
                    ragioneSocialeLabel.setText(nullToDash(dto.ragioneSociale()));
                    partitaIvaLabel.setText(nullToDash(dto.partitaIva()));
                    sedeLegaleLabel.setText(nullToDash(dto.sedeLegale()));
                },
                ex -> warningLabel.setText("Errore: " + ex.getMessage()));
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
