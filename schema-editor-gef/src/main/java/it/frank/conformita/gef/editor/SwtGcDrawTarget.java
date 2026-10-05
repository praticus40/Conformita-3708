package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.UnifilareDrawTarget;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

final class SwtGcDrawTarget implements UnifilareDrawTarget {

    private final GC gc;

    SwtGcDrawTarget(GC gc) {
        this.gc = gc;
    }

    @Override
    public void line(double x1, double y1, double x2, double y2) {
        gc.drawLine(toInt(x1), toInt(y1), toInt(x2), toInt(y2));
    }

    @Override
    public void rect(double x, double y, double w, double h) {
        gc.drawRectangle(toInt(x), toInt(y), toInt(w), toInt(h));
    }

    @Override
    public void arc(double cx, double cy, double r, double startDeg, double extentDeg) {
        gc.drawArc(
                toInt(cx - r),
                toInt(cy - r),
                toInt(2 * r),
                toInt(2 * r),
                toInt(startDeg),
                toInt(extentDeg));
    }

    @Override
    public void text(double x, double y, String text, TextAnchor anchor) {
        if (text == null || text.isBlank()) {
            return;
        }
        Point extent = gc.textExtent(text);
        int drawX = toInt(x);
        if (anchor == TextAnchor.MIDDLE) {
            drawX -= extent.x / 2;
        } else if (anchor == TextAnchor.END) {
            drawX -= extent.x;
        }
        gc.drawText(text, drawX, toInt(y), true);
    }

    private static int toInt(double value) {
        return (int) Math.round(value);
    }
}
