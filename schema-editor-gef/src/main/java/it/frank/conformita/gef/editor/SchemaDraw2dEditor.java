package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.UnifilareEditorLayout;
import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import it.frank.conformita.core.unifilare.schema.editor.UnifilareNodeDefaults;
import it.frank.conformita.gef.editor.figures.ConnectionOverlayFigure;
import it.frank.conformita.gef.editor.figures.GridFigure;
import it.frank.conformita.gef.editor.figures.UnifilareSymbolFigure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;

public final class SchemaDraw2dEditor extends FigureCanvas {

    private final SchemaDiagramRoot root;
    private final FreeformLayer rootLayer;
    private final Figure contentLayer;
    private final ConnectionOverlayFigure overlayFigure;
    private final Map<GefDiagramNode, UnifilareSymbolFigure> figures = new HashMap<>();
    private final List<GefDiagramNode> selection = new ArrayList<>();
    private Consumer<List<GefDiagramNode>> selectionListener = nodes -> {};

    private GefDiagramNode dragNode;
    private Point dragStart;
    private Rectangle dragStartBounds;
    private int canvasWidth;
    private int canvasHeight;

    public SchemaDraw2dEditor(Composite parent, SchemaDiagramRoot root) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        this.root = root;
        rootLayer = new FreeformLayer();
        GefDiagramModel diagram = root.diagram();
        canvasWidth = Math.max(800, (int) diagram.getCanvasWidth());
        canvasHeight = Math.max(600, (int) diagram.getCanvasHeight());
        rootLayer.setPreferredSize(new Dimension(canvasWidth, canvasHeight));

        GridFigure grid = new GridFigure(canvasWidth, canvasHeight);
        grid.setBounds(new Rectangle(0, 0, canvasWidth, canvasHeight));
        rootLayer.add(grid);

        contentLayer = new Figure();
        contentLayer.setLayoutManager(new XYLayout());
        contentLayer.setBounds(new Rectangle(0, 0, canvasWidth, canvasHeight));
        rootLayer.add(contentLayer);

        overlayFigure = new ConnectionOverlayFigure();
        overlayFigure.setBounds(new Rectangle(0, 0, canvasWidth, canvasHeight));
        overlayFigure.setDiagram(diagram);
        rootLayer.add(overlayFigure);

        setContents(rootLayer);
        rebuildFigures();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseDown(MouseEvent e) {
                onMouseDown(e);
            }

            @Override
            public void mouseUp(MouseEvent e) {
                onMouseUp();
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // no-op
            }
        });
        addMouseMoveListener((MouseMoveListener) this::onMouseMove);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }
        });
    }

    public void setSelectionListener(Consumer<List<GefDiagramNode>> listener) {
        this.selectionListener = listener != null ? listener : nodes -> {};
    }

    public boolean addNode(String type) {
        GefDiagramModel diagram = root.diagram();
        if (!UnifilareEditorLayout.canAdd(type, diagram)) {
            return false;
        }
        GefDiagramNode node = UnifilareNodeDefaults.newNode(type, diagram);
        diagram.getNodes().add(node);
        addFigureFor(node);
        overlayFigure.setDiagram(root.diagram());
        contentLayer.getLayoutManager().layout(contentLayer);
        repaintSymbolsAndOverlay();
        return true;
    }

    public String addBlockedReason(String type) {
        return UnifilareEditorLayout.addBlockedReason(type, root.diagram());
    }

    public void rebuildFigures() {
        contentLayer.removeAll();
        figures.clear();
        for (GefDiagramNode node : root.diagram().getNodes()) {
            if (isCanvasNode(node)) {
                addFigureFor(node);
            }
        }
        overlayFigure.setDiagram(root.diagram());
        contentLayer.getLayoutManager().layout(contentLayer);
        redraw();
    }

    public void refreshNode(GefDiagramNode node) {
        UnifilareSymbolFigure figure = figures.get(node);
        if (figure != null) {
            figure.bind(node);
            setFigureBounds(node, figure);
            contentLayer.getLayoutManager().layout(contentLayer);
            overlayFigure.setDiagram(root.diagram());
            repaintSymbolsAndOverlay();
        }
    }

    public void selectNodes(List<GefDiagramNode> nodes) {
        selectNodes(nodes, true);
    }

    public void selectNodes(List<GefDiagramNode> nodes, boolean notifyListener) {
        selection.clear();
        if (nodes != null) {
            selection.addAll(nodes);
        }
        refreshSelectionVisuals();
        if (notifyListener) {
            selectionListener.accept(List.copyOf(selection));
        }
    }

    public void selectNode(GefDiagramNode node) {
        if (node == null) {
            selectNodes(List.of());
        } else {
            selectNodes(List.of(node));
            scrollToNode(node);
        }
    }

    /** Updates canvas selection without notifying shell (avoids tree↔canvas sync loops). */
    public void selectNodeFromOutline(GefDiagramNode node) {
        if (node == null) {
            selectNodes(List.of(), false);
        } else {
            selectNodes(List.of(node), false);
            scrollToNode(node);
        }
    }

    public List<GefDiagramNode> selection() {
        return List.copyOf(selection);
    }

    private void addFigureFor(GefDiagramNode node) {
        UnifilareSymbolFigure figure = new UnifilareSymbolFigure(node);
        figure.setSelected(selection.contains(node));
        setFigureBounds(node, figure);
        contentLayer.add(figure, boundsFor(node));
        figures.put(node, figure);
    }

    private void setFigureBounds(GefDiagramNode node, UnifilareSymbolFigure figure) {
        Rectangle rect = boundsFor(node);
        figure.setBounds(rect);
        XYLayout layout = (XYLayout) contentLayer.getLayoutManager();
        layout.setConstraint(figure, rect);
    }

    private Rectangle boundsFor(GefDiagramNode node) {
        return new Rectangle(
                (int) node.getX(), (int) node.getY(), (int) node.getWidth(), (int) node.getHeight());
    }

    private void onMouseDown(MouseEvent e) {
        setFocus();
        Point p = translateToDiagram(new Point(e.x, e.y));
        GefDiagramNode hit = findNodeAt(p);
        if ((e.stateMask & SWT.MOD1) != 0 && hit != null) {
            toggleSelection(hit);
        } else if (hit != null) {
            selection.clear();
            selection.add(hit);
            dragNode = hit;
            dragStart = p;
            UnifilareSymbolFigure fig = figures.get(hit);
            dragStartBounds = fig.getBounds().getCopy();
        } else {
            selection.clear();
            dragNode = null;
        }
        refreshSelectionVisuals();
        repaintSymbolsAndOverlay();
        selectionListener.accept(List.copyOf(selection));
    }

    private void onMouseUp() {
        if (dragNode != null) {
            for (GefDiagramNode node : selection) {
                snapNode(node);
                setFigureBounds(node, figures.get(node));
            }
            contentLayer.getLayoutManager().layout(contentLayer);
            repaintSymbolsAndOverlay();
        }
        dragNode = null;
        dragStart = null;
        dragStartBounds = null;
    }

    private void onMouseMove(MouseEvent e) {
        if (dragNode == null || dragStart == null || dragStartBounds == null) {
            return;
        }
        Point p = translateToDiagram(new Point(e.x, e.y));
        int dx = p.x - dragStart.x;
        int dy = p.y - dragStart.y;
        if (selection.size() <= 1) {
            moveNode(dragNode, dragStartBounds.x + dx, dragStartBounds.y + dy, false);
        } else {
            for (GefDiagramNode node : selection) {
                UnifilareSymbolFigure fig = figures.get(node);
                Rectangle b = fig.getBounds();
                moveNode(node, b.x + dx, b.y + dy, false);
            }
            dragStart = p;
        }
        contentLayer.getLayoutManager().layout(contentLayer);
        repaintSymbolsAndOverlay();
    }

    /** Repaint symbols and connection guides without redrawing the static grid layer. */
    private void repaintSymbolsAndOverlay() {
        contentLayer.repaint();
        overlayFigure.repaint();
    }

    private void moveNode(GefDiagramNode node, int x, int y, boolean snap) {
        UnifilareEditorLayout.Position pos = UnifilareEditorLayout.constrainMove(
                node, x, y, root.diagram(), canvasWidth, canvasHeight, snap);
        node.setX(pos.x());
        node.setY(pos.y());
        UnifilareSymbolFigure figure = figures.get(node);
        if (figure != null) {
            setFigureBounds(node, figure);
        }
    }

    private void snapNode(GefDiagramNode node) {
        moveNode(node, (int) node.getX(), (int) node.getY(), true);
    }

    private void onKeyPressed(KeyEvent e) {
        if (e.keyCode == SWT.DEL && !selection.isEmpty()) {
            root.diagram().getNodes().removeAll(selection);
            selection.clear();
            rebuildFigures();
            selectionListener.accept(List.of());
            e.doit = false;
        } else if (e.keyCode == 'a' && (e.stateMask & SWT.CTRL) != 0) {
            selection.clear();
            root.diagram().getNodes().stream().filter(SchemaDraw2dEditor::isCanvasNode).forEach(selection::add);
            refreshSelectionVisuals();
            selectionListener.accept(List.copyOf(selection));
            e.doit = false;
        }
    }

    private static boolean isCanvasNode(GefDiagramNode node) {
        return node != null && !"sheet".equals(node.getType());
    }

    private GefDiagramNode findNodeAt(Point p) {
        for (int i = root.diagram().getNodes().size() - 1; i >= 0; i--) {
            GefDiagramNode node = root.diagram().getNodes().get(i);
            if (!isCanvasNode(node)) {
                continue;
            }
            UnifilareSymbolFigure fig = figures.get(node);
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
        for (Map.Entry<GefDiagramNode, UnifilareSymbolFigure> entry : figures.entrySet()) {
            entry.getValue().setSelected(selection.contains(entry.getKey()));
        }
    }

    private void scrollToNode(GefDiagramNode node) {
        Rectangle b = boundsFor(node);
        getViewport().setHorizontalLocation(b.x - 40);
        getViewport().setVerticalLocation(b.y - 40);
    }

    private Point translateToDiagram(Point p) {
        org.eclipse.draw2d.geometry.Point loc = getViewport().getViewLocation();
        return new Point(p.x + loc.x, p.y + loc.y);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
