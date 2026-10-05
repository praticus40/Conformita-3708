package it.frank.conformita.core.unifilare.schema.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.UnifilareEditorLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UnifilareNodeDefaults {

    private UnifilareNodeDefaults() {}

    public record Defaults(String label, double width, double height, Map<String, String> properties) {}

    public static Defaults forType(String type) {
        return switch (type) {
            case "general" -> new Defaults("Generale", 72, 52, Map.of("inAmps", "32"));
            case "busbar" -> new Defaults("Sbarra", 400, 16, Map.of());
            case "sheet" -> new Defaults("Schema", 200, 120, Map.of());
            default -> new Defaults("Linea", 56, 130, Map.of("inAmps", "16", "differentialMa", "30"));
        };
    }

    public static GefDiagramNode newNode(String type, GefDiagramModel model) {
        Defaults d = forType(type);
        GefDiagramNode node = new GefDiagramNode();
        node.setId(UUID.randomUUID().toString());
        node.setType(type);
        node.setLabel(d.label());
        node.setWidth(d.width());
        node.setHeight(d.height());
        node.setProperties(new HashMap<>(d.properties()));
        UnifilareEditorLayout.placeNewNode(node, model);
        return node;
    }
}
