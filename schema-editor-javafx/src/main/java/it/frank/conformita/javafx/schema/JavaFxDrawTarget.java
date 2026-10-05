package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.UnifilareDrawTarget;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.canvas.GraphicsContext;

final class JavaFxDrawTarget implements UnifilareDrawTarget {

    private final GraphicsContext gc;

    JavaFxDrawTarget(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public void line(double x1, double y1, double x2, double y2) {
        gc.strokeLine(x1, y1, x2, y2);
    }

    @Override
    public void rect(double x, double y, double w, double h) {
        gc.strokeRect(x, y, w, h);
    }

    @Override
    public void arc(double cx, double cy, double r, double startDeg, double extentDeg) {
        gc.strokeArc(cx - r, cy - r, 2 * r, 2 * r, startDeg, extentDeg, javafx.scene.shape.ArcType.OPEN);
    }

    @Override
    public void text(double x, double y, String text, TextAnchor anchor) {
        if (text == null || text.isBlank()) {
            return;
        }
        Text measure = new Text(text);
        measure.setFont(Font.getDefault());
        double w = measure.getLayoutBounds().getWidth();
        double drawX = x;
        if (anchor == TextAnchor.MIDDLE) {
            drawX -= w / 2;
        } else if (anchor == TextAnchor.END) {
            drawX -= w;
        }
        gc.fillText(text, drawX, y);
    }
}
