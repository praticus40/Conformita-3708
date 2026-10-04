package it.frank.conformita.core.service;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.MaterialeRigaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.VerificaRigaDto;
import it.frank.conformita.core.entity.DestinazioneUso;
import it.frank.conformita.core.entity.StatoPratica;
import it.frank.conformita.core.entity.TipoIntervento;
import it.frank.conformita.core.mapper.EntityMapper;
import it.frank.conformita.core.validation.ValidationResult;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationServiceTest {

    private final ValidationService validationService = new ValidationService();

    @Test
    void emptyCommittenteProducesError() {
        ValidationResult result = validationService.validate(
                Optional.of(validImpresa()), emptyPratica());
        assertTrue(result.hasErrors());
        assertTrue(result.issues().stream().anyMatch(i -> i.message().toLowerCase().contains("committente")));
    }

    @Test
    void impresaProvinciaTooLongFailsPersistenceValidation() {
        ImpresaDto dto = new ImpresaDto(
                "Test Srl",
                "12345678901",
                "Via Roma 1",
                null,
                null,
                null,
                null,
                null,
                "Palermo",
                "palermo",
                null,
                null,
                null,
                null,
                "Impianti Elettrici");
        ValidationResult result = validationService.validateForPersistence(dto);
        assertTrue(result.hasErrors());
        assertTrue(result.issues().stream().anyMatch(i -> i.message().toLowerCase().contains("provincia")));
    }

    @Test
    void validDataPassesCoreFields() {
        ValidationResult result = validationService.validate(
                Optional.of(validImpresa()), validPraticaPartial());
        assertFalse(result.issues().stream().anyMatch(i -> i.context().name().equals("COMMITTENTE")
                && i.message().contains("committente obbligatorio")));
    }

    @Test
    void invalidPartitaIvaFails() {
        ImpresaDto impresa = new ImpresaDto(
                "ACME Srl", "123", "Via Roma 1", null, null, null, null, null, null, null, null, null, null, null, null);
        ValidationResult result = validationService.validate(Optional.of(impresa), validPraticaPartial());
        assertTrue(result.issues().stream().anyMatch(i -> i.message().contains("Partita IVA")));
    }

    private static ImpresaDto validImpresa() {
        return new ImpresaDto(
                "ACME Srl",
                "12345678901",
                "Via Roma 1",
                null,
                "Mario Rossi",
                "Mario Rossi",
                "Via Roma 1",
                "90100",
                "Palermo",
                "PA",
                "3331234567",
                null,
                "442467",
                "117634",
                "Impianti Elettrici");
    }

    private static PraticaDto emptyPratica() {
        return base(1L, "", "", "", "", null, null, null, null, List.of(), List.of());
    }

    private static PraticaDto validPraticaPartial() {
        return base(
                1L,
                "Mario Rossi",
                "Via Verdi 10",
                "Palermo",
                "PA",
                TipoIntervento.NUOVO,
                DestinazioneUso.CIVILE,
                "Impianto test",
                "Palermo",
                List.of(new MaterialeRigaDto(null, "Cavo", "CE", null, null, null)),
                List.of(new VerificaRigaDto(null, "Terra", "22 Ohm", null, null)));
    }

    private static PraticaDto base(
            Long id,
            String committente,
            String indirizzo,
            String comune,
            String provincia,
            TipoIntervento tipo,
            DestinazioneUso uso,
            String descrizione,
            String comuneImpianto,
            List<MaterialeRigaDto> materiali,
            List<VerificaRigaDto> verifiche) {
        Instant now = Instant.now();
        return new PraticaDto(
                id,
                now,
                now,
                StatoPratica.BOZZA,
                comuneImpianto,
                provincia,
                null,
                null,
                descrizione,
                committente,
                null,
                indirizzo,
                null,
                null,
                comune,
                null,
                tipo,
                uso,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                "CEI 64-8",
                true,
                true,
                true,
                true,
                false,
                false,
                false,
                null,
                LocalDate.now(),
                null,
                null,
                false,
                false,
                false,
                "3331234567",
                null,
                null,
                null,
                materiali != null ? materiali : EntityMapper.emptyMateriali(),
                verifiche != null ? verifiche : EntityMapper.emptyVerifiche());
    }
}
