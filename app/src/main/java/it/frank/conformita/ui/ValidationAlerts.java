package it.frank.conformita.ui;

import it.frank.conformita.core.validation.ValidationIssue;
import it.frank.conformita.core.validation.ValidationResult;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class ValidationAlerts {

    private ValidationAlerts() {}

    public static void showPersistErrors(String header, ValidationResult result) {
        String body = String.join("\n", result.issues().stream().map(ValidationIssue::message).toList());
        Alert alert = new Alert(Alert.AlertType.WARNING, body, ButtonType.OK);
        alert.setTitle("Conformità 37/08");
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
