package it.frank.conformita.ui;

import it.frank.conformita.core.dto.PraticaDto;

public interface PraticaPanel {

    void loadPratica(PraticaDto pratica);

    PraticaDto collectPratica(PraticaDto base);
}
