package it.frank.conformita.gef.editor.figures;

import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ColorConstants;

public final class UnifilareSymbolFigure extends Figure {

    private GefDiagramNode node;
    private boolean selected;

    public UnifilareSymbolFigure(GefDiagramNode node) {
        this.node = node;
    }

    public void bind(GefDiagramNode node) {
        this.node = node;
        repaint();
    }

    public GefDiagramNode node() {
        return node;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        if (node == null) {
            return;
        }
        org.eclipse.draw2d.geometry.Rectangle ca = getClientArea();
        Draw2dDrawTarget target = new Draw2dDrawTarget(graphics);
        UnifilareSymbolGlyphs.paintNode(
                node.getType(),
                target,
                ca.x,
                ca.y,
                ca.width,
                ca.height,
                node.getLabel(),
                node.getProperties());
        if (selected) {
            graphics.setForegroundColor(ColorConstants.blue);
            graphics.setLineWidth(2);
            graphics.drawRectangle(ca.x, ca.y, ca.width - 1, ca.height - 1);
            graphics.setLineWidth(1);
        }
    }
}
