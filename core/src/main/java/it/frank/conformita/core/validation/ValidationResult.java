package it.frank.conformita.core.validation;

import java.util.List;

public record ValidationResult(List<ValidationIssue> issues) {

    public boolean hasErrors() {
        return !issues.isEmpty();
    }
}
