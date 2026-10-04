package it.frank.conformita.core.service;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.MaterialeRigaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.VerificaRigaDto;
import it.frank.conformita.core.validation.ValidationContext;
import it.frank.conformita.core.validation.ValidationIssue;
import it.frank.conformita.core.validation.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationService {

    public ValidationResult validate(Optional<ImpresaDto> impresa, PraticaDto pratica) {
        List<ValidationIssue> issues = new ArrayList<>();
        validateImpresa(impresa, issues);
        validatePratica(pratica, issues);
        validateCertificato(impresa, pratica, issues);
        return new ValidationResult(List.copyOf(issues));
    }

    /** Checks string lengths and formats before INSERT/UPDATE (aligned with JPA column sizes). */
    public ValidationResult validateForPersistence(ImpresaDto dto) {
        List<ValidationIssue> issues = new ArrayList<>();
        checkMaxLength(issues, ValidationContext.IMPRESA, "Ragione sociale", dto.ragioneSociale(), 255);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Partita IVA", dto.partitaIva(), 16);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Sede legale", dto.sedeLegale(), 512);
        checkMaxLength(issues, ValidationContext.IMPRESA, "REA", dto.rea(), 64);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Responsabile tecnico", dto.responsabileTecnico(), 255);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Legale rappresentante", dto.legaleRappresentante(), 255);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Via sede", dto.viaSede(), 255);
        checkMaxLength(issues, ValidationContext.IMPRESA, "CAP", dto.cap(), 10);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Comune", dto.comune(), 128);
        checkProvincia(issues, ValidationContext.IMPRESA, dto.provincia());
        checkMaxLength(issues, ValidationContext.IMPRESA, "Telefono", dto.telefono(), 32);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Email", dto.email(), 128);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Numero CCIAA", dto.cciaaNumero(), 64);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Albo artigiani", dto.alboArtigianiNumero(), 64);
        checkMaxLength(issues, ValidationContext.IMPRESA, "Settore", dto.settore(), 128);
        return new ValidationResult(List.copyOf(issues));
    }

    /** Checks string lengths before INSERT/UPDATE on pratica and child rows. */
    public ValidationResult validateForPersistence(PraticaDto dto) {
        List<ValidationIssue> issues = new ArrayList<>();
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Comune impianto", dto.comuneImpianto(), 128);
        checkProvincia(issues, ValidationContext.COMMITTENTE, dto.provincia());
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Protocollo", dto.protocollo(), 64);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Committente", dto.committenteNome(), 255);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Codice fiscale committente", dto.committenteCodiceFiscale(), 16);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Indirizzo impianto", dto.indirizzoImpianto(), 512);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Indirizzo committente", dto.indirizzoCommittente(), 512);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Proprietario", dto.proprietarioDescrizione(), 512);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "Comune", dto.comune(), 128);
        checkMaxLength(issues, ValidationContext.COMMITTENTE, "CAP", dto.cap(), 10);
        checkMaxLength(issues, ValidationContext.INTERVENTO, "Altri usi", dto.altriUsi(), 128);
        checkMaxLength(issues, ValidationContext.INTERVENTO, "Tensione nominale", dto.tensioneNominale(), 64);
        checkMaxLength(issues, ValidationContext.INTERVENTO, "Norma tecnica", dto.normaTecnica(), 255);
        checkMaxLength(issues, ValidationContext.DICHIARAZIONE, "Riferimenti precedenti", dto.riferimentiPrecedenti(), 512);
        checkMaxLength(issues, ValidationContext.DICHIARAZIONE, "Responsabile tecnico (firma)", dto.responsabileTecnicoNome(), 255);
        checkMaxLength(issues, ValidationContext.LIBRETTO, "Telefono assistenza", dto.telefonoAssistenza(), 32);
        checkMaxLength(issues, ValidationContext.LIBRETTO, "Percorso schema allegato", dto.schemaAllegatoPath(), 1024);
        checkMaxLength(
                issues, ValidationContext.LIBRETTO, "Percorso JSON schema unifilare", dto.schemaUnifilareJsonPath(), 512);
        if (dto.materiali() != null) {
            for (int i = 0; i < dto.materiali().size(); i++) {
                MaterialeRigaDto row = dto.materiali().get(i);
                String prefix = "Materiali riga " + (i + 1) + ": ";
                checkMaxLength(issues, ValidationContext.MATERIALI, prefix + "descrizione", row.descrizione(), 512);
                checkMaxLength(issues, ValidationContext.MATERIALI, prefix + "marchio", row.marchio(), 128);
                checkMaxLength(issues, ValidationContext.MATERIALI, prefix + "modello", row.modello(), 128);
                checkMaxLength(issues, ValidationContext.MATERIALI, prefix + "posa", row.posa(), 128);
                checkMaxLength(issues, ValidationContext.MATERIALI, prefix + "sigla", row.siglaRif(), 128);
            }
        }
        if (dto.verifiche() != null) {
            for (int i = 0; i < dto.verifiche().size(); i++) {
                VerificaRigaDto row = dto.verifiche().get(i);
                String prefix = "Verifiche riga " + (i + 1) + ": ";
                checkMaxLength(issues, ValidationContext.VERIFICHE, prefix + "tipo prova", row.tipoProva(), 512);
                checkMaxLength(issues, ValidationContext.VERIFICHE, prefix + "valore", row.valore(), 128);
                checkMaxLength(issues, ValidationContext.VERIFICHE, prefix + "marca strumento", row.marcaStrumento(), 128);
                checkMaxLength(
                        issues, ValidationContext.VERIFICHE, prefix + "modello strumento", row.modelloStrumento(), 128);
            }
        }
        return new ValidationResult(List.copyOf(issues));
    }

    private void validateImpresa(Optional<ImpresaDto> impresa, List<ValidationIssue> issues) {
        if (impresa.isEmpty()) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Anagrafica impresa non compilata"));
            return;
        }
        ImpresaDto dto = impresa.get();
        if (isBlank(dto.ragioneSociale())) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Ragione sociale obbligatoria"));
        }
        if (isBlank(dto.partitaIva())) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Partita IVA obbligatoria"));
        } else if (!isValidPartitaIva(dto.partitaIva())) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Partita IVA deve contenere 11 cifre"));
        }
        if (isBlank(dto.sedeLegale()) && isBlank(dto.viaSede())) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Sede legale obbligatoria"));
        }
        if (isBlank(dto.cciaaNumero())) {
            issues.add(new ValidationIssue(ValidationContext.IMPRESA, "Numero iscrizione CCIAA consigliato per export certificato"));
        }
    }

    private void validatePratica(PraticaDto pratica, List<ValidationIssue> issues) {
        if (isBlank(pratica.committenteNome())) {
            issues.add(new ValidationIssue(ValidationContext.COMMITTENTE, "Nome committente obbligatorio"));
        }
        if (isBlank(pratica.indirizzoImpianto())) {
            issues.add(new ValidationIssue(ValidationContext.COMMITTENTE, "Indirizzo impianto obbligatorio"));
        }
        if (isBlank(pratica.comune())) {
            issues.add(new ValidationIssue(ValidationContext.COMMITTENTE, "Comune obbligatorio"));
        }
        if (isBlank(pratica.provincia())) {
            issues.add(new ValidationIssue(ValidationContext.COMMITTENTE, "Provincia obbligatoria"));
        }
        if (pratica.tipoIntervento() == null) {
            issues.add(new ValidationIssue(ValidationContext.INTERVENTO, "Tipo intervento obbligatorio"));
        }
        if (pratica.destinazioneUso() == null) {
            issues.add(new ValidationIssue(ValidationContext.INTERVENTO, "Destinazione d'uso obbligatoria"));
        }
    }

    private void validateCertificato(Optional<ImpresaDto> impresa, PraticaDto pratica, List<ValidationIssue> issues) {
        if (isBlank(pratica.descrizioneImpianto())) {
            issues.add(new ValidationIssue(ValidationContext.INTERVENTO, "Descrizione impianto obbligatoria per il certificato"));
        }
        if (isBlank(pratica.comuneImpianto())) {
            issues.add(new ValidationIssue(ValidationContext.COMMITTENTE, "Comune impianto (intestazione) obbligatorio"));
        }
        if (!pratica.dichiaraNorma() || !pratica.dichiaraMateriali() || !pratica.dichiaraControlli()) {
            issues.add(new ValidationIssue(ValidationContext.DICHIARAZIONE, "Confermare tutte le dichiarazioni art.6"));
        }
        if (pratica.dataDichiarazione() == null) {
            issues.add(new ValidationIssue(ValidationContext.DICHIARAZIONE, "Data dichiarazione obbligatoria"));
        }
        if (pratica.materiali() == null || pratica.materiali().isEmpty()) {
            issues.add(new ValidationIssue(ValidationContext.MATERIALI, "Inserire almeno una riga materiali"));
        }
        if (pratica.verifiche() == null || pratica.verifiche().isEmpty()) {
            issues.add(new ValidationIssue(ValidationContext.VERIFICHE, "Inserire almeno una prova strumentale"));
        }
        if (isBlank(pratica.telefonoAssistenza()) && impresa.map(ImpresaDto::telefono).orElse("").isBlank()) {
            issues.add(new ValidationIssue(ValidationContext.LIBRETTO, "Telefono assistenza obbligatorio"));
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    static boolean isValidPartitaIva(String partitaIva) {
        String digits = partitaIva.replaceAll("\\D", "");
        return digits.length() == 11;
    }

    private static void checkMaxLength(
            List<ValidationIssue> issues, ValidationContext context, String label, String value, int max) {
        if (value == null) {
            return;
        }
        if (value.length() > max) {
            issues.add(new ValidationIssue(
                    context, label + ": massimo " + max + " caratteri (attuali " + value.length() + ")"));
        }
    }

    private static void checkProvincia(List<ValidationIssue> issues, ValidationContext context, String provincia) {
        if (provincia == null || provincia.isBlank()) {
            return;
        }
        String trimmed = provincia.trim();
        if (trimmed.length() > 4) {
            issues.add(new ValidationIssue(
                    context,
                    "Provincia: massimo 4 caratteri (sigla, es. PA). Valore troppo lungo: \"" + trimmed + "\""));
            return;
        }
        if (trimmed.length() > 2 && !trimmed.matches("(?i)[A-Za-z]{3,4}")) {
            issues.add(new ValidationIssue(context, "Provincia: usare la sigla (es. PA), non il nome esteso"));
        }
    }
}
