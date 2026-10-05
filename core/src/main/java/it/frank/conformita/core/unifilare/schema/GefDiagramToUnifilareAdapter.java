package it.frank.conformita.core.unifilare.schema;

import it.frank.conformita.core.unifilare.UnifilareDocument;
import it.frank.conformita.core.unifilare.UnifilareTemplates;
import java.util.Comparator;

/** Best-effort export PDF from free-form GEF diagram using semantic nodes when present. */
public final class GefDiagramToUnifilareAdapter {

    private GefDiagramToUnifilareAdapter() {}

    public static UnifilareDocument toUnifilareV1(UnifilareSchemaDocumentV2 document) {
        if (document == null || document.getDiagram().getNodes().isEmpty()) {
            return UnifilareTemplates.residentialDefault();
        }
        UnifilareDocument semantic = tryBuildSemantic(document);
        if (semantic != null) {
            return semantic;
        }
        return UnifilareTemplates.residentialDefault();
    }

    private static UnifilareDocument tryBuildSemantic(UnifilareSchemaDocumentV2 document) {
        boolean hasCircuit = document.getDiagram().getNodes().stream()
                .anyMatch(n -> "circuit".equals(n.getType()));
        if (!hasCircuit) {
            return null;
        }
        UnifilareDocument doc = UnifilareTemplates.emptyWithOneCircuit();
        doc.getCircuits().clear();
        int order = 0;
        for (GefDiagramNode node : document.getDiagram().getNodes().stream()
                .filter(n -> "circuit".equals(n.getType()))
                .sorted(Comparator.comparingDouble(GefDiagramNode::getX))
                .toList()) {
            UnifilareDocument.Circuit c = new UnifilareDocument.Circuit();
            c.setId(node.getId());
            c.setLabel(node.getLabel() != null ? node.getLabel() : "Linea");
            c.setInAmps(parseInt(node.getProperties().get("inAmps"), 16));
            c.setDifferentialMa(parseInt(node.getProperties().get("differentialMa"), 30));
            c.setAnchorX(node.getX() + node.getWidth() / 2);
            c.setOrder(order++);
            doc.getCircuits().add(c);
        }
        document.getDiagram().getNodes().stream()
                .filter(n -> "general".equals(n.getType()))
                .findFirst()
                .ifPresent(g -> {
                    doc.getGeneral().setLabel(g.getLabel() != null ? g.getLabel() : "Generale");
                    doc.getGeneral().setInAmps(parseInt(g.getProperties().get("inAmps"), 32));
                    doc.getGeneral().setX(g.getX());
                    doc.getGeneral().setY(g.getY());
                });
        document.getDiagram().getNodes().stream()
                .filter(n -> "busbar".equals(n.getType()))
                .findFirst()
                .ifPresent(b -> {
                    doc.getBusbar().setY(b.getY() + b.getHeight() / 2);
                    doc.getBusbar().setX1(b.getX());
                    doc.getBusbar().setX2(b.getX() + b.getWidth());
                });
        doc.getLayout().setWidth(document.getDiagram().getCanvasWidth());
        doc.getLayout().setHeight(document.getDiagram().getCanvasHeight());
        return doc;
    }

    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
