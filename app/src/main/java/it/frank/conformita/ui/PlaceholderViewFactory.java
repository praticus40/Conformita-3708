package it.frank.conformita.ui;

import it.frank.conformita.AppSection;
import it.frank.conformita.WizardStep;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class PlaceholderViewFactory {

    private PlaceholderViewFactory() {}

    public static Node forSection(AppSection section) {
        return build(
                section.getLabel(),
                "Area in costruzione — contenuti disponibili in una versione successiva.");
    }

    public static Node forWizardStep(WizardStep step) {
        return build(
                "Step " + step.getNumber() + " — " + step.getTitle(),
                "In costruzione — modulo del wizard non ancora implementato.");
    }

    private static VBox build(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("placeholder-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("placeholder-subtitle");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setMaxWidth(560);

        VBox box = new VBox(12, titleLabel, subtitleLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(48, 32, 48, 32));
        box.getStyleClass().add("placeholder-root");
        return box;
    }
}
