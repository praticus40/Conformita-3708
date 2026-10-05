package it.frank.conformita.gef.editor.figures;

import it.frank.conformita.core.unifilare.UnifilareGeometry;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

/** Static background grid; painted once per invalidation, not on every symbol drag. */
public final class GridFigure extends Figure {

    private static final Color GRID_LINE = new Color(null, 236, 236, 240);

    private final int width;
    private final int height;

    public GridFigure(int width, int height) {
        this.width = width;
        this.height = height;
        setOpaque(true);
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        graphics.setBackgroundColor(ColorConstants.white);
        graphics.fillRectangle(getBounds());
        graphics.setForegroundColor(GRID_LINE);
        graphics.setLineWidth(1);
        int step = (int) UnifilareGeometry.SNAP_GRID;
        for (int x = 0; x <= width; x += step) {
            graphics.drawLine(x, 0, x, height);
        }
        for (int y = 0; y <= height; y += step) {
            graphics.drawLine(0, y, width, y);
        }
    }

    @Override
    public Dimension getPreferredSize(int wHint, int hHint) {
        return new Dimension(width, height);
    }
}
