package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public final class SchemaDraw2dEditor extends FigureCanvas {

    private final SchemaDiagramRoot root;
    private final Figure diagramFigure;
    private final Map<GefDiagramNode, SchemaNodeFigure> figures = new HashMap<>();
    private final List<GefDiagramNode> selection = new ArrayList<>();
    private Consumer<List<GefDiagramNode>> selectionListener = nodes -> {};

    private GefDiagramNode dragNode;
    private Point dragStart;
    private Rectangle dragStartBounds;

    public SchemaDraw2dEditor(Composite parent, SchemaDiagramRoot root) {
        super(parent, SWT.NONE);
        this.root = root;
        diagramFigure = new Figure();
        diagramFigure.setLayoutManager(new XYLayout());
        GefDiagramModel diagram = root.diagram();
        diagramFigure.setPreferredSize(new Dimension((int) diagram.getCanvasWidth(), (int) diagram.getCanvasHeight()));
        setContents(diagramFigure);
        rebuildFigures();
        addMouseListener(new MouseListener() {
            @Override
            public void mouseDown(MouseEvent e) {
                onMouseDown(e);
            }

            @Override
            public void mouseUp(MouseEvent e) {
                dragNode = null;
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // no-op
            }
        });
        addMouseMoveListener((MouseMoveListener) e -> onMouseMove(e));
    }

    public void setSelectionListener(Consumer<List<GefDiagramNode>> listener) {
        this.selectionListener = listener != null ? listener : nodes -> {};
    }

    public void addNode(String type, String label) {
        GefDiagramNode node = new GefDiagramNode();
        node.setId(UUID.randomUUID().toString());
        node.setType(type);
        node.setLabel(label);
        node.setX(40 + root.diagram().getNodes().size() * 20);
        node.setY(40 + root.diagram().getNodes().size() * 16);
        node.setWidth(96);
        node.setHeight(64);
        root.diagram().getNodes().add(node);
        addFigureFor(node);
    }

    public void rebuildFigures() {
        diagramFigure.removeAll();
        figures.clear();
        for (GefDiagramNode node : root.diagram().getNodes()) {
            addFigureFor(node);
        }
        diagramFigure.getLayoutManager().layout(diagramFigure);
        redraw();
    }

    private void addFigureFor(GefDiagramNode node) {
        SchemaNodeFigure figure = new SchemaNodeFigure();
        refreshFigure(node, figure);
        diagramFigure.add(figure);
        figures.put(node, figure);
    }

    private void refreshFigure(GefDiagramNode node, SchemaNodeFigure figure) {
        figure.applyLabel(node.getLabel() != null ? node.getLabel() : node.getType());
        figure.applyBounds(new Rectangle(
                (int) node.getX(), (int) node.getY(), (int) node.getWidth(), (int) node.getHeight()));
        figure.setSelected(selection.contains(node));
    }

    private void onMouseDown(MouseEvent e) {
        Point p = translateToRoot(new Point(e.x, e.y));
        GefDiagramNode hit = findNodeAt(p);
        if ((e.stateMask & SWT.MOD1) != 0 && hit != null) {
            toggleSelection(hit);
        } else if (hit != null) {
            selection.clear();
            selection.add(hit);
            dragNode = hit;
            dragStart = p;
            SchemaNodeFigure fig = figures.get(hit);
            dragStartBounds = fig.getBounds().getCopy();
        } else {
            selection.clear();
        }
        refreshSelectionVisuals();
        selectionListener.accept(List.copyOf(selection));
    }

    private void onMouseMove(MouseEvent e) {
        if (dragNode == null || dragStart == null || dragStartBounds == null) {
            return;
        }
        Point p = translateToRoot(new Point(e.x, e.y));
        int dx = p.x - dragStart.x;
        int dy = p.y - dragStart.y;
        if (selection.size() <= 1) {
            moveNode(dragNode, dragStartBounds.x + dx, dragStartBounds.y + dy);
        } else {
            for (GefDiagramNode node : selection) {
                SchemaNodeFigure fig = figures.get(node);
                Rectangle b = fig.getBounds();
                moveNode(node, b.x + dx, b.y + dy);
            }
            dragStart = p;
        }
        diagramFigure.getLayoutManager().layout(diagramFigure);
        redraw();
    }

    private void moveNode(GefDiagramNode node, int x, int y) {
        node.setX(Math.max(0, x));
        node.setY(Math.max(0, y));
        refreshFigure(node, figures.get(node));
    }

    private GefDiagramNode findNodeAt(Point p) {
        for (int i = root.diagram().getNodes().size() - 1; i >= 0; i--) {
            GefDiagramNode node = root.diagram().getNodes().get(i);
            SchemaNodeFigure fig = figures.get(node);
            if (fig != null && fig.getBounds().contains(p)) {
                return node;
            }
        }
        return null;
    }

    private void toggleSelection(GefDiagramNode node) {
        if (selection.contains(node)) {
            selection.remove(node);
        } else {
            selection.add(node);
        }
    }

    private void refreshSelectionVisuals() {
        for (Map.Entry<GefDiagramNode, SchemaNodeFigure> entry : figures.entrySet()) {
            refreshFigure(entry.getKey(), entry.getValue());
        }
    }

    private Point translateToRoot(Point p) {
        org.eclipse.draw2d.geometry.Point result = getViewport().getClientArea().getLocation();
        return new Point(p.x + result.x, p.y + result.y);
    }
}
