package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.UnifilareGeometry;
import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.UnifilareEditorLayout;
import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import it.frank.conformita.core.unifilare.schema.editor.UnifilareNodeDefaults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public final class SchemaCanvasView extends Pane {

    private final SchemaDiagramRoot root;
    private final Canvas gridCanvas;
    private final Canvas overlayCanvas;
    private final Pane nodeLayer;
    private final Map<GefDiagramNode, UnifilareSymbolNode> nodes = new HashMap<>();
    private final List<GefDiagramNode> selection = new ArrayList<>();
    private Consumer<List<GefDiagramNode>> selectionListener = n -> {};

    private final int canvasWidth;
    private final int canvasHeight;

    private GefDiagramNode dragNode;
    private Point2D dragStart;
    private double dragStartX;
    private double dragStartY;

    public SchemaCanvasView(SchemaDiagramRoot root) {
        this.root = root;
        GefDiagramModel diagram = root.diagram();
        canvasWidth = Math.max(800, (int) diagram.getCanvasWidth());
        canvasHeight = Math.max(600, (int) diagram.getCanvasHeight());

        gridCanvas = new Canvas(canvasWidth, canvasHeight);
        overlayCanvas = new Canvas(canvasWidth, canvasHeight);
        nodeLayer = new Pane();
        nodeLayer.setPrefSize(canvasWidth, canvasHeight);

        getChildren().addAll(gridCanvas, nodeLayer, overlayCanvas);
        setPrefSize(canvasWidth, canvasHeight);
        drawGrid();
        rebuildNodes();
        installHandlers();
    }

    public void setSelectionListener(Consumer<List<GefDiagramNode>> listener) {
        this.selectionListener = listener != null ? listener : n -> {};
    }

    public boolean addNode(String type) {
        GefDiagramModel diagram = root.diagram();
        if (!UnifilareEditorLayout.canAdd(type, diagram)) {
            return false;
        }
        GefDiagramNode node = UnifilareNodeDefaults.newNode(type, diagram);
        diagram.getNodes().add(node);
        addNodeView(node);
        redrawOverlay();
        return true;
    }

    public String addBlockedReason(String type) {
        return UnifilareEditorLayout.addBlockedReason(type, root.diagram());
    }

    public void rebuildNodes() {
        nodeLayer.getChildren().clear();
        nodes.clear();
        for (GefDiagramNode node : root.diagram().getNodes()) {
            if (isCanvasNode(node)) {
                addNodeView(node);
            }
        }
        redrawOverlay();
    }

    public void refreshNode(GefDiagramNode node) {
        UnifilareSymbolNode view = nodes.get(node);
        if (view != null) {
            view.bind(node);
            view.relocate(node.getX(), node.getY());
            view.redraw();
            redrawOverlay();
        }
    }

    public List<GefDiagramNode> selection() {
        return List.copyOf(selection);
    }

    public void selectNodes(List<GefDiagramNode> nodes, boolean notify) {
        selection.clear();
        if (nodes != null) {
            selection.addAll(nodes);
        }
        refreshSelectionVisuals();
        if (notify) {
            selectionListener.accept(List.copyOf(selection));
        }
    }

    public void selectNodeFromOutline(GefDiagramNode node) {
        if (node == null) {
            selectNodes(List.of(), false);
        } else {
            selectNodes(List.of(node), false);
            scrollToNode(node);
        }
    }

    private void addNodeView(GefDiagramNode node) {
        UnifilareSymbolNode view = new UnifilareSymbolNode(node);
        view.relocate(node.getX(), node.getY());
        view.setSelected(selection.contains(node));
        nodeLayer.getChildren().add(view);
        nodes.put(node, view);
    }

    private void installHandlers() {
        setFocusTraversable(true);
        nodeLayer.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> handleMousePressed(e.getX(), e.getY(), e.isControlDown(), e.getButton()));
        nodeLayer.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, e -> handleMouseDragged(e.getX(), e.getY()));
        nodeLayer.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_RELEASED, e -> handleMouseReleased());
        setOnMousePressed(e -> {
            requestFocus();
            if (e.getTarget() != this && e.getTarget() != gridCanvas && e.getTarget() != overlayCanvas) {
                return;
            }
            Point2D p = toDiagram(e.getX(), e.getY());
            handleMousePressed(p.getX(), p.getY(), e.isControlDown(), e.getButton());
            e.consume();
        });
        setOnMouseDragged(e -> {
            if (dragNode == null) {
                return;
            }
            handleMouseDragged(e.getX(), e.getY());
            e.consume();
        });
        setOnMouseReleased(e -> {
            handleMouseReleased();
        });
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE && !selection.isEmpty()) {
                root.diagram().getNodes().removeAll(selection);
                selection.clear();
                rebuildNodes();
                selectionListener.accept(List.of());
                e.consume();
            } else if (e.getCode() == KeyCode.A && e.isControlDown()) {
                selection.clear();
                root.diagram().getNodes().stream().filter(SchemaCanvasView::isCanvasNode).forEach(selection::add);
                refreshSelectionVisuals();
                selectionListener.accept(List.copyOf(selection));
                e.consume();
            }
        });
    }

    private void handleMousePressed(double x, double y, boolean controlDown, MouseButton button) {
            requestFocus();
            Point2D p = new Point2D(x, y);
            GefDiagramNode hit = findNodeAt(p);
            if (controlDown && hit != null) {
                toggleSelection(hit);
            } else if (hit != null && button == MouseButton.PRIMARY) {
                selection.clear();
                selection.add(hit);
                dragNode = hit;
                dragStart = p;
                dragStartX = hit.getX();
                dragStartY = hit.getY();
            } else {
                selection.clear();
                dragNode = null;
            }
            refreshSelectionVisuals();
            selectionListener.accept(List.copyOf(selection));
    }

    private void handleMouseDragged(double x, double y) {
            if (dragNode == null || dragStart == null) {
                return;
            }
            Point2D p = new Point2D(x, y);
            double dx = p.getX() - dragStart.getX();
            double dy = p.getY() - dragStart.getY();
            if (selection.size() <= 1) {
                moveNode(dragNode, dragStartX + dx, dragStartY + dy, false);
            } else {
                for (GefDiagramNode node : selection) {
                    UnifilareSymbolNode view = nodes.get(node);
                    moveNode(node, view.getLayoutX() + dx, view.getLayoutY() + dy, false);
                }
                dragStart = p;
            }
            redrawOverlay();
    }

    private void handleMouseReleased() {
            if (dragNode != null) {
                for (GefDiagramNode node : selection) {
                    moveNode(node, node.getX(), node.getY(), true);
                }
                redrawOverlay();
            }
            dragNode = null;
            dragStart = null;
    }

    private void moveNode(GefDiagramNode node, double x, double y, boolean snap) {
        UnifilareEditorLayout.Position pos = UnifilareEditorLayout.constrainMove(
                node, x, y, root.diagram(), canvasWidth, canvasHeight, snap);
        node.setX(pos.x());
        node.setY(pos.y());
        UnifilareSymbolNode view = nodes.get(node);
        if (view != null) {
            view.relocate(pos.x(), pos.y());
            if (snap) {
                view.redraw();
            }
        }
    }

    private void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(Color.web("#ECECF0"));
        int step = (int) UnifilareGeometry.SNAP_GRID;
        for (int x = 0; x <= canvasWidth; x += step) {
            gc.strokeLine(x, 0, x, canvasHeight);
        }
        for (int y = 0; y <= canvasHeight; y += step) {
            gc.strokeLine(0, y, canvasWidth, y);
        }
    }

    private void redrawOverlay() {
        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        GefDiagramModel diagram = root.diagram();
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
        JavaFxDrawTarget target = new JavaFxDrawTarget(gc);
        UnifilareSymbolGlyphs.paintBusConnections(target, busY, busX1, busX2, anchors);
    }

    private GefDiagramNode findNodeAt(Point2D p) {
        for (int i = root.diagram().getNodes().size() - 1; i >= 0; i--) {
            GefDiagramNode node = root.diagram().getNodes().get(i);
            if (!isCanvasNode(node)) {
                continue;
            }
            if (p.getX() >= node.getX()
                    && p.getX() <= node.getX() + node.getWidth()
                    && p.getY() >= node.getY()
                    && p.getY() <= node.getY() + node.getHeight()) {
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
        for (Map.Entry<GefDiagramNode, UnifilareSymbolNode> e : nodes.entrySet()) {
            e.getValue().setSelected(selection.contains(e.getKey()));
        }
    }

    private void scrollToNode(GefDiagramNode node) {
        // ScrollPane parent handles via layout; optional future
    }

    private Point2D toDiagram(double x, double y) {
        return new Point2D(x, y);
    }

    private static boolean isCanvasNode(GefDiagramNode node) {
        return node != null && !"sheet".equals(node.getType());
    }
}
