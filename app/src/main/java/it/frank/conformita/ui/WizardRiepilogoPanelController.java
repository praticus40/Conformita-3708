package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class WizardRiepilogoPanelController implements Initializable, PraticaPanel {

    private final UiContext context;

    @FXML
    private TextArea summaryArea;

    @FXML
    private Button btnValida;

    @FXML
    private Button btnEsporta;

    public WizardRiepilogoPanelController(UiContext context) {
        this.context = context;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnValida.setOnAction(e -> context.callbacks().runValidationForCurrentPratica());
        btnEsporta.setOnAction(e -> context.callbacks().exportPdfForCurrentPratica());
    }

    @Override
    public void loadPratica(PraticaDto pratica) {
        StringBuilder sb = new StringBuilder();
        sb.append("Committente: ").append(nullSafe(pratica.committenteNome())).append('\n');
        sb.append("Ubicazione: ").append(nullSafe(pratica.indirizzoImpianto())).append(", ")
                .append(nullSafe(pratica.comune())).append('\n');
        sb.append("Impianto: ").append(nullSafe(pratica.descrizioneImpianto())).append('\n');
        sb.append("Intervento: ").append(pratica.tipoIntervento() != null ? pratica.tipoIntervento() : "—")
                .append('\n');
        sb.append("Materiali: ")
                .append(pratica.materiali() != null ? pratica.materiali().size() : 0)
                .append(" righe\n");
        sb.append("Verifiche strumentali: ")
                .append(pratica.verifiche() != null ? pratica.verifiche().size() : 0)
                .append(" righe\n");
        if (pratica.allegatoSchema() && pratica.id() != null && !context.facade().hasSchemaPdf(pratica.id())) {
            sb.append("\nAttenzione: dichiarato allegato schema ma PDF non presente.\n");
        }
        summaryArea.setText(sb.toString());
    }

    @Override
    public PraticaDto collectPratica(PraticaDto base) {
        return base;
    }

    private static String nullSafe(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
