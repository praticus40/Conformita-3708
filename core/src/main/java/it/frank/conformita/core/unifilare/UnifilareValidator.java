package it.frank.conformita.core.unifilare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class UnifilareValidator {

    private UnifilareValidator() {}

    public static void validateOrThrow(UnifilareDocument doc) {
        List<String> issues = validate(doc);
        if (!issues.isEmpty()) {
            throw new UnifilareValidationException(issues);
        }
    }

    public static List<String> validate(UnifilareDocument doc) {
        List<String> issues = new ArrayList<>();
        if (doc == null) {
            issues.add("Documento assente");
            return issues;
        }
        if (!UnifilareDocument.FORMAT_ID.equals(doc.getFormat())) {
            issues.add("Formato non supportato: " + doc.getFormat());
        }
        if (doc.getVersion() != UnifilareDocument.FORMAT_VERSION) {
            issues.add("Versione non supportata: " + doc.getVersion());
        }
        if (doc.getLayout().getWidth() < 200 || doc.getLayout().getHeight() < 200) {
            issues.add("Layout troppo piccolo");
        }
        UnifilareDocument.GeneralSwitch general = doc.getGeneral();
        if (general.getLabel() == null || general.getLabel().isBlank()) {
            issues.add("Interruttore generale: etichetta obbligatoria");
        }
        if (general.getInAmps() <= 0) {
            issues.add("Interruttore generale: In deve essere > 0");
        }
        if (doc.getCircuits().isEmpty()) {
            issues.add("Almeno un circuito richiesto");
        }
        Set<String> ids = new HashSet<>();
        for (UnifilareDocument.Circuit circuit : doc.getCircuits()) {
            if (circuit.getId() == null || circuit.getId().isBlank()) {
                issues.add("Circuito senza id");
            } else if (!ids.add(circuit.getId())) {
                issues.add("Id circuito duplicato: " + circuit.getId());
            }
            if (circuit.getLabel() == null || circuit.getLabel().isBlank()) {
                issues.add("Circuito " + circuit.getId() + ": etichetta obbligatoria");
            }
            if (circuit.getInAmps() <= 0) {
                issues.add("Circuito " + circuit.getId() + ": In deve essere > 0");
            }
            if (circuit.getDifferentialMa() != null && circuit.getDifferentialMa() < 0) {
                issues.add("Circuito " + circuit.getId() + ": Id non valido");
            }
        }
        return issues;
    }
}
