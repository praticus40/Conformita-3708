package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class SchemaPropertiesPanel extends Composite {

    private final Text labelField;
    private final Text inAmpsField;
    private final Text differentialField;
    private final Label differentialLabel;
    private Consumer<GefDiagramNode> changeListener = node -> {};
    private GefDiagramNode bound;
    private boolean updating;

    public SchemaPropertiesPanel(Composite parent) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout(1, false));
        Group group = new Group(this, SWT.NONE);
        group.setText("Proprietà");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        new Label(group, SWT.NONE).setText("Etichetta");
        labelField = new Text(group, SWT.BORDER);
        labelField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(group, SWT.NONE).setText("In (A)");
        inAmpsField = new Text(group, SWT.BORDER);
        inAmpsField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        differentialLabel = new Label(group, SWT.NONE);
        differentialLabel.setText("IΔn (mA)");
        differentialField = new Text(group, SWT.BORDER);
        differentialField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        ModifyListener listener = this::onFieldChanged;
        labelField.addModifyListener(listener);
        inAmpsField.addModifyListener(listener);
        differentialField.addModifyListener(listener);
    }

    public void setChangeListener(Consumer<GefDiagramNode> listener) {
        this.changeListener = listener != null ? listener : node -> {};
    }

    public void bindSelection(List<GefDiagramNode> selection) {
        updating = true;
        try {
            if (selection == null || selection.isEmpty()) {
                bound = null;
                labelField.setText("");
                inAmpsField.setText("");
                differentialField.setText("");
                setFieldsEnabled(false);
                return;
            }
            bound = selection.get(0);
            labelField.setText(bound.getLabel() != null ? bound.getLabel() : "");
            inAmpsField.setText(prop(bound, "inAmps"));
            differentialField.setText(prop(bound, "differentialMa"));
            boolean amps = isAmpsType(bound.getType());
            inAmpsField.setEnabled(amps);
            boolean diff = "circuit".equals(bound.getType());
            differentialField.setEnabled(diff);
            differentialLabel.setEnabled(diff);
            labelField.setEnabled(true);
        } finally {
            updating = false;
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        labelField.setEnabled(enabled);
        inAmpsField.setEnabled(enabled);
        differentialField.setEnabled(enabled);
        differentialLabel.setEnabled(enabled);
    }

    private static boolean isAmpsType(String type) {
        return "circuit".equals(type) || "general".equals(type);
    }

    private static String prop(GefDiagramNode node, String key) {
        String v = node.getProperties().get(key);
        return v != null ? v : "";
    }

    private void onFieldChanged(ModifyEvent e) {
        if (updating || bound == null) {
            return;
        }
        bound.setLabel(labelField.getText().trim());
        if (bound.getProperties() == null) {
            bound.setProperties(new HashMap<>());
        }
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
