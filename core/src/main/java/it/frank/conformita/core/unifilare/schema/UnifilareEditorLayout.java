package it.frank.conformita.core.unifilare.schema;

import it.frank.conformita.core.unifilare.UnifilareGeometry;
import java.util.List;
import java.util.Optional;

/** Placement and drag constraints for residential unifilare GEF diagrams. */
public final class UnifilareEditorLayout {

    public static final double DEFAULT_GENERAL_Y = 40;
    public static final double DEFAULT_BUSBAR_Y = 120;
    public static final double DEFAULT_BUSBAR_X = 80;
    public static final double DEFAULT_BUSBAR_WIDTH = 640;
    public static final double CIRCUIT_ANCHOR_SPACING = 140;
    public static final double FIRST_CIRCUIT_OFFSET = 70;

    private UnifilareEditorLayout() {}

    public static boolean canAdd(String type, GefDiagramModel model) {
        return addBlockedReason(type, model) == null;
    }

    public static String addBlockedReason(String type, GefDiagramModel model) {
        if (model == null || type == null) {
            return "Tipo non valido.";
        }
        return switch (type) {
            case "general" ->
                    hasType(model, "general") ? "Esiste già un interruttore generale." : null;
            case "busbar" ->
                    hasType(model, "busbar") ? "Esiste già una sbarra collettore." : null;
            case "circuit" -> null;
            case "sheet" -> "Il foglio schema non si aggiunge dalla palette.";
            default -> null;
        };
    }

    public static void placeNewNode(GefDiagramNode node, GefDiagramModel model) {
        attachToSheet(node, model);
        switch (node.getType()) {
            case "general" -> placeGeneral(node, model);
            case "busbar" -> placeBusbar(node, model);
            case "circuit" -> placeCircuit(node, model);
            default -> {
                node.setX(UnifilareGeometry.snap(40));
                node.setY(UnifilareGeometry.snap(40));
            }
        }
    }

    public static Position constrainMove(
            GefDiagramNode node, double proposedX, double proposedY, GefDiagramModel model, double canvasWidth,
            double canvasHeight) {
        return constrainMove(node, proposedX, proposedY, model, canvasWidth, canvasHeight, true);
    }

    public static Position constrainMove(
            GefDiagramNode node,
            double proposedX,
            double proposedY,
            GefDiagramModel model,
            double canvasWidth,
            double canvasHeight,
            boolean snapToGrid) {
        if (node == null || "sheet".equals(node.getType())) {
            return new Position(proposedX, proposedY);
        }
        return switch (node.getType()) {
            case "general" ->
                    constrainGeneral(node, proposedX, proposedY, model, canvasWidth, canvasHeight, snapToGrid);
            case "busbar" -> constrainBusbar(node, proposedX, model, canvasWidth, snapToGrid);
            case "circuit" -> constrainCircuit(node, proposedX, model, canvasWidth, snapToGrid);
            default ->
                    snapToGrid
                            ? snapPosition(proposedX, proposedY, node.getWidth(), node.getHeight(), canvasWidth, canvasHeight)
                            : new Position(
                                    clamp(proposedX, 0, Math.max(0, canvasWidth - node.getWidth())),
                                    clamp(proposedY, 0, Math.max(0, canvasHeight - node.getHeight())));
        };
    }

    public static double busbarLineY(GefDiagramModel model) {
        return findBusbar(model)
                .map(b -> b.getY() + b.getHeight() / 2.0)
                .orElse(DEFAULT_BUSBAR_Y + 8);
    }

    public static double circuitRowY(GefDiagramModel model) {
        return UnifilareGeometry.snap(busbarLineY(model) + UnifilareGeometry.DROP_FROM_BUS);
    }

    private static void placeGeneral(GefDiagramNode node, GefDiagramModel model) {
        double centerX = model.getCanvasWidth() / 2;
        double x = UnifilareGeometry.snap(centerX - node.getWidth() / 2);
        double y = UnifilareGeometry.snap(DEFAULT_GENERAL_Y);
        node.setX(x);
        node.setY(y);
    }

    private static void placeBusbar(GefDiagramNode node, GefDiagramModel model) {
        node.setX(UnifilareGeometry.snap(DEFAULT_BUSBAR_X));
        node.setY(UnifilareGeometry.snap(DEFAULT_BUSBAR_Y));
        if (node.getWidth() < 200) {
            node.setWidth(DEFAULT_BUSBAR_WIDTH);
        }
        double maxWidth = model.getCanvasWidth() - node.getX() - 20;
        node.setWidth(Math.min(node.getWidth(), Math.max(200, maxWidth)));
    }

    private static void placeCircuit(GefDiagramNode node, GefDiagramModel model) {
        int index = (int) model.getNodes().stream().filter(n -> "circuit".equals(n.getType())).count();
        double anchorX = UnifilareGeometry.snap(DEFAULT_BUSBAR_X + FIRST_CIRCUIT_OFFSET + index * CIRCUIT_ANCHOR_SPACING);
        Optional<GefDiagramNode> busbar = findBusbar(model);
        if (busbar.isPresent()) {
            anchorX = nextCircuitAnchor(model, busbar.get());
        }
        node.setX(UnifilareGeometry.snap(anchorX - node.getWidth() / 2));
        node.setY(circuitRowY(model));
    }

    private static double nextCircuitAnchor(GefDiagramModel model, GefDiagramNode busbar) {
        List<Double> anchors = model.getNodes().stream()
                .filter(n -> "circuit".equals(n.getType()))
                .map(n -> n.getX() + n.getWidth() / 2)
                .sorted()
                .toList();
        double start = busbar.getX() + FIRST_CIRCUIT_OFFSET;
        double end = busbar.getX() + busbar.getWidth() - FIRST_CIRCUIT_OFFSET;
        for (double candidate = start; candidate <= end; candidate += CIRCUIT_ANCHOR_SPACING) {
            double snapped = UnifilareGeometry.snap(candidate);
            if (anchors.stream().noneMatch(a -> Math.abs(a - snapped) < CIRCUIT_ANCHOR_SPACING / 2)) {
                return snapped;
            }
        }
        return UnifilareGeometry.snap(start + anchors.size() * CIRCUIT_ANCHOR_SPACING);
    }

    private static Position constrainGeneral(
            GefDiagramNode node,
            double proposedX,
            double proposedY,
            GefDiagramModel model,
            double canvasWidth,
            double canvasHeight,
            boolean snapToGrid) {
        double busTop = findBusbar(model).map(GefDiagramNode::getY).orElse(DEFAULT_BUSBAR_Y);
        double maxY = busTop - node.getHeight() - 10;
        maxY = Math.max(20, maxY);
        double x = snapToGrid ? UnifilareGeometry.snap(proposedX) : proposedX;
        double y = snapToGrid ? UnifilareGeometry.snap(proposedY) : proposedY;
        x = clamp(x, 0, canvasWidth - node.getWidth());
        y = clamp(y, 20, maxY);
        return new Position(x, y);
    }

    private static Position constrainBusbar(
            GefDiagramNode node, double proposedX, GefDiagramModel model, double canvasWidth, boolean snapToGrid) {
        double y = node.getY() > 0 ? node.getY() : UnifilareGeometry.snap(DEFAULT_BUSBAR_Y);
        if (snapToGrid) {
            y = UnifilareGeometry.snap(y);
        }
        double x = snapToGrid ? UnifilareGeometry.snap(proposedX) : proposedX;
        x = clamp(x, 0, Math.max(0, canvasWidth - node.getWidth()));
        return new Position(x, y);
    }

    private static Position constrainCircuit(
            GefDiagramNode node, double proposedX, GefDiagramModel model, double canvasWidth, boolean snapToGrid) {
        double rowY = circuitRowY(model);
        double centerX = proposedX + node.getWidth() / 2;
        Optional<GefDiagramNode> busbar = findBusbar(model);
        if (busbar.isPresent()) {
            GefDiagramNode bus = busbar.get();
            double minCenter = bus.getX() + 20;
            double maxCenter = bus.getX() + bus.getWidth() - 20;
            centerX = clamp(centerX, minCenter, maxCenter);
            if (snapToGrid) {
                centerX = UnifilareGeometry.snap(centerX);
            }
        } else if (snapToGrid) {
            centerX = UnifilareGeometry.snap(centerX);
        }
        double x = clamp(centerX - node.getWidth() / 2, 0, Math.max(0, canvasWidth - node.getWidth()));
        return new Position(x, rowY);
    }

    private static Position snapPosition(
            double x, double y, double width, double height, double canvasWidth, double canvasHeight) {
        return new Position(
                clamp(UnifilareGeometry.snap(x), 0, Math.max(0, canvasWidth - width)),
                clamp(UnifilareGeometry.snap(y), 0, Math.max(0, canvasHeight - height)));
    }

    private static void attachToSheet(GefDiagramNode node, GefDiagramModel model) {
        model.getNodes().stream()
                .filter(n -> "sheet".equals(n.getType()))
                .findFirst()
                .ifPresent(sheet -> node.setParentId(sheet.getId()));
    }

    private static boolean hasType(GefDiagramModel model, String type) {
        return model.getNodes().stream().anyMatch(n -> type.equals(n.getType()));
    }

    public static Optional<GefDiagramNode> findBusbar(GefDiagramModel model) {
        return model.getNodes().stream().filter(n -> "busbar".equals(n.getType())).findFirst();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Position(double x, double y) {}
}
