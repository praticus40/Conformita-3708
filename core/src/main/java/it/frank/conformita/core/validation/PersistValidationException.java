package it.frank.conformita.core.validation;

/** Thrown when DTO field values exceed DB column limits or fail pre-persist checks. */
public final class PersistValidationException extends RuntimeException {

    private final ValidationResult result;

    public PersistValidationException(ValidationResult result) {
        super(formatMessage(result));
        this.result = result;
    }

    public ValidationResult getResult() {
        return result;
    }

    private static String formatMessage(ValidationResult result) {
        if (result.issues().isEmpty()) {
            return "Validazione persistenza non superata";
        }
        return result.issues().getFirst().message();
    }
}
