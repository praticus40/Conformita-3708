package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaSummary;
import javafx.stage.Stage;

public interface MainCallbacks {

    Stage getStage();

    void openPratica(long id);

    void openImpresaSection();

    void onPraticaUpdated(PraticaDto dto);

    void refreshPraticheList();

    void updateStatusMessage(String message);

    void createNewPraticaFromHub();

    void deletePratica(PraticaSummary summary);

    void exportPraticaFromHub(PraticaSummary summary);

    void backToPraticheList();

    void previewPdfForCurrentPratica();

    void runValidationForCurrentPratica();

    void exportPdfForCurrentPratica();
}
