package it.frank.conformita.gef.editor.figures;

import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

public final class ConnectionOverlayFigure extends Figure {

    private GefDiagramModel diagram;

    public void setDiagram(GefDiagramModel diagram) {
        this.diagram = diagram;
        repaint();
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        if (diagram == null) {
            return;
        }
        GefDiagramNode busbar = diagram.getNodes().stream()
                .filter(n -> "busbar".equals(n.getType()))
                .findFirst()
                .orElse(null);
        if (busbar == null) {
            return;
        }
        double busY = busbar.getY() + busbar.getHeight() / 2;
        double busX1 = busbar.getX();
        double busX2 = busbar.getX() + busbar.getWidth();
        List<Double> anchors = new ArrayList<>();
        for (GefDiagramNode node : diagram.getNodes()) {
            if ("circuit".equals(node.getType())) {
                anchors.add(node.getX() + node.getWidth() / 2);
            }
        }
        Draw2dDrawTarget target = new Draw2dDrawTarget(graphics);
        UnifilareSymbolGlyphs.paintBusConnections(target, busY, busX1, busX2, anchors);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return false;
    }
}
