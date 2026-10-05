package it.frank.conformita.gef.editor.figures;

import it.frank.conformita.core.unifilare.UnifilareDrawTarget;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;

public final class Draw2dDrawTarget implements UnifilareDrawTarget {

    private final Graphics graphics;

    public Draw2dDrawTarget(Graphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public void line(double x1, double y1, double x2, double y2) {
        graphics.drawLine(toInt(x1), toInt(y1), toInt(x2), toInt(y2));
    }

    @Override
    public void rect(double x, double y, double w, double h) {
        graphics.drawRectangle(toInt(x), toInt(y), toInt(w), toInt(h));
    }

    @Override
    public void arc(double cx, double cy, double r, double startDeg, double extentDeg) {
        graphics.drawArc(
                toInt(cx - r),
                toInt(cy - r),
                toInt(2 * r),
                toInt(2 * r),
                toInt(startDeg * 64),
                toInt(extentDeg * 64));
    }

    @Override
    public void text(double x, double y, String text, TextAnchor anchor) {
        if (text == null || text.isBlank()) {
            return;
        }
        int approxWidth = text.length() * 6;
        int drawX = toInt(x);
        if (anchor == TextAnchor.MIDDLE) {
            drawX -= approxWidth / 2;
        } else if (anchor == TextAnchor.END) {
            drawX -= approxWidth;
        }
        graphics.drawText(text, drawX, toInt(y));
    }

    private static int toInt(double value) {
        return (int) Math.round(value);
    }
}
