package it.frank.conformita.core.unifilare.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import it.frank.conformita.core.unifilare.UnifilareDocument;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GefDiagramToUnifilareAdapterTest {

    @Test
    void mapsGeneralBusbarAndCircuits() {
        UnifilareSchemaDocumentV2 doc = new UnifilareSchemaDocumentV2();

        GefDiagramNode general = node("g1", "general", 20, 30, 72, 52);
        general.getProperties().put("inAmps", "40");
        general.setLabel("Gen");

        GefDiagramNode busbar = node("b1", "busbar", 80, 120, 400, 16);

        GefDiagramNode c1 = node("c1", "circuit", 100, 160, 56, 130);
        c1.setLabel("L1");
        c1.getProperties().put("inAmps", "16");
        c1.getProperties().put("differentialMa", "30");

        GefDiagramNode c2 = node("c2", "circuit", 200, 160, 56, 130);
        c2.setLabel("L2");

        doc.getDiagram().getNodes().addAll(java.util.List.of(general, busbar, c1, c2));

        UnifilareDocument semantic = GefDiagramToUnifilareAdapter.toUnifilareV1(doc);

        assertEquals("Gen", semantic.getGeneral().getLabel());
        assertEquals(40, semantic.getGeneral().getInAmps());
        assertEquals(128, semantic.getBusbar().getY(), 0.001);
        assertEquals(80, semantic.getBusbar().getX1(), 0.001);
        assertEquals(480, semantic.getBusbar().getX2(), 0.001);
        assertEquals(2, semantic.getCircuits().size());
        assertEquals(128, semantic.getCircuits().get(0).getAnchorX(), 0.001);
        assertEquals(228, semantic.getCircuits().get(1).getAnchorX(), 0.001);
    }

    private static GefDiagramNode node(String id, String type, double x, double y, double w, double h) {
        GefDiagramNode n = new GefDiagramNode();
        n.setId(id);
        n.setType(type);
        n.setX(x);
        n.setY(y);
        n.setWidth(w);
        n.setHeight(h);
        n.setProperties(new java.util.HashMap<>(Map.of()));
        return n;
    }
}
