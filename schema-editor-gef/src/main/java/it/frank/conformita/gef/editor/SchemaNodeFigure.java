package it.frank.conformita.gef.editor;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class SchemaNodeFigure extends Figure {

    private final Label title;
    private boolean selected;

    public SchemaNodeFigure() {
        setLayoutManager(new ToolbarLayout());
        title = new Label();
        title.setBorder(new MarginBorder(4));
        add(title);
        applyBorderColor();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        applyBorderColor();
    }

    private void applyBorderColor() {
        Color color = selected
                ? new Color(Display.getDefault(), 0, 90, 200)
                : new Color(Display.getDefault(), 40, 40, 40);
        setBorder(new LineBorder(color, selected ? 2 : 1, SWT.LINE_SOLID));
    }

    public void applyLabel(String text) {
        title.setText(text != null ? text : "");
    }

    public void applyBounds(Rectangle rect) {
        setBounds(rect);
    }
}
