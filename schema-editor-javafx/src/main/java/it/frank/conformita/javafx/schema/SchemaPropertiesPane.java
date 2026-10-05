package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

final class SchemaPropertiesPane extends GridPane {

    private final TextField labelField = new TextField();
    private final TextField inAmpsField = new TextField();
    private final TextField differentialField = new TextField();
    private final Label differentialLabel = new Label("IΔn (mA)");

    private Consumer<GefDiagramNode> changeListener = n -> {};
    private GefDiagramNode bound;
    private boolean updating;

    SchemaPropertiesPane() {
        setPadding(new Insets(8));
        setHgap(8);
        setVgap(6);
        add(new Label("Etichetta"), 0, 0);
        add(labelField, 1, 0);
        GridPane.setHgrow(labelField, Priority.ALWAYS);
        add(new Label("In (A)"), 0, 1);
        add(inAmpsField, 1, 1);
        GridPane.setHgrow(inAmpsField, Priority.ALWAYS);
        add(differentialLabel, 0, 2);
        add(differentialField, 1, 2);
        GridPane.setHgrow(differentialField, Priority.ALWAYS);

        labelField.textProperty().addListener((o, a, b) -> onChange());
        inAmpsField.textProperty().addListener((o, a, b) -> onChange());
        differentialField.textProperty().addListener((o, a, b) -> onChange());
    }

    void setChangeListener(Consumer<GefDiagramNode> listener) {
        this.changeListener = listener != null ? listener : n -> {};
    }

    void bindSelection(List<GefDiagramNode> selection) {
        updating = true;
        try {
            if (selection == null || selection.isEmpty()) {
                bound = null;
                labelField.clear();
                inAmpsField.clear();
                differentialField.clear();
                setFieldsDisabled(true);
                return;
            }
            bound = selection.get(0);
            labelField.setText(bound.getLabel() != null ? bound.getLabel() : "");
            inAmpsField.setText(prop(bound, "inAmps"));
            differentialField.setText(prop(bound, "differentialMa"));
            boolean amps = "circuit".equals(bound.getType()) || "general".equals(bound.getType());
            inAmpsField.setDisable(!amps);
            boolean diff = "circuit".equals(bound.getType());
            differentialField.setDisable(!diff);
            differentialLabel.setDisable(!diff);
            labelField.setDisable(false);
        } finally {
            updating = false;
        }
    }

    private void setFieldsDisabled(boolean disabled) {
        labelField.setDisable(disabled);
        inAmpsField.setDisable(disabled);
        differentialField.setDisable(disabled);
        differentialLabel.setDisable(disabled);
    }

    private static String prop(GefDiagramNode node, String key) {
        String v = node.getProperties().get(key);
        return v != null ? v : "";
    }

    private void onChange() {
        if (updating || bound == null) {
            return;
        }
        bound.setLabel(labelField.getText().trim());
        putProp("inAmps", inAmpsField.getText());
        putProp("differentialMa", differentialField.getText());
        changeListener.accept(bound);
    }

    private void putProp(String key, String value) {
        if (value == null || value.isBlank()) {
            bound.getProperties().remove(key);
        } else {
            bound.getProperties().put(key, value.trim());
        }
    }
}
