package it.frank.conformita.core.dto;

import it.frank.conformita.core.entity.TipoIntervento;
import java.time.Instant;

public record PraticaSummary(
        Long id,
        String committenteNome,
        String indirizzoImpianto,
        TipoIntervento tipoIntervento,
        Instant updatedAt) {}
