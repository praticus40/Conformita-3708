package it.frank.conformita.core.unifilare;

import java.util.List;

public class UnifilareValidationException extends IllegalArgumentException {

    private final List<String> issues;

    public UnifilareValidationException(List<String> issues) {
        super(String.join("; ", issues));
        this.issues = List.copyOf(issues);
    }

    public List<String> getIssues() {
        return issues;
    }
}
