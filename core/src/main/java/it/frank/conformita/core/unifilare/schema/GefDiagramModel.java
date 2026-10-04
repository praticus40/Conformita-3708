package it.frank.conformita.core.unifilare.schema;

import java.util.ArrayList;
import java.util.List;

public class GefDiagramModel {

    private double canvasWidth = 800;
    private double canvasHeight = 520;
    private List<GefDiagramNode> nodes = new ArrayList<>();

    public double getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public double getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(double canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public List<GefDiagramNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GefDiagramNode> nodes) {
        this.nodes = nodes != null ? new ArrayList<>(nodes) : new ArrayList<>();
    }
}
