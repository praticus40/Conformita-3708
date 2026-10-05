package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

final class UnifilareSymbolNode extends Region {

    private final Canvas canvas = new Canvas();
    private GefDiagramNode node;
    private boolean selected;

    UnifilareSymbolNode(GefDiagramNode node) {
        this.node = node;
        getChildren().add(canvas);
        bindSize();
        redraw();
    }

    void bind(GefDiagramNode node) {
        this.node = node;
        bindSize();
        redraw();
    }

    GefDiagramNode node() {
        return node;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
        redraw();
    }

    private void bindSize() {
        if (node == null) {
            return;
        }
        setPrefSize(node.getWidth(), node.getHeight());
        setMinSize(node.getWidth(), node.getHeight());
        setMaxSize(node.getWidth(), node.getHeight());
        canvas.setWidth(node.getWidth());
        canvas.setHeight(node.getHeight());
    }

    void redraw() {
        if (node == null) {
            return;
        }
        double w = node.getWidth();
        double h = node.getHeight();
        canvas.setWidth(w);
        canvas.setHeight(h);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        JavaFxDrawTarget target = new JavaFxDrawTarget(gc);
        UnifilareSymbolGlyphs.paintNode(
                node.getType(), target, 0, 0, w, h, node.getLabel(), node.getProperties());
        if (selected) {
            gc.setStroke(Color.DODGERBLUE);
            gc.setLineWidth(2);
            gc.strokeRect(1, 1, w - 2, h - 2);
        }
    }
}
