package it.frank.conformita.core.unifilare.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UnifilareEditorLayoutTest {

    @Test
    void allowsSingleGeneralAndBusbar() {
        GefDiagramModel model = new GefDiagramModel();
        assertTrue(UnifilareEditorLayout.canAdd("general", model));
        assertTrue(UnifilareEditorLayout.canAdd("busbar", model));

        GefDiagramNode general = node("general");
        model.getNodes().add(general);
        assertFalse(UnifilareEditorLayout.canAdd("general", model));
        assertTrue(UnifilareEditorLayout.canAdd("busbar", model));
    }

    @Test
    void circuitMovesOnHorizontalRow() {
        GefDiagramModel model = new GefDiagramModel();
        GefDiagramNode busbar = node("busbar");
        busbar.setX(80);
        busbar.setY(120);
        busbar.setWidth(400);
        busbar.setHeight(16);
        model.getNodes().add(busbar);

        GefDiagramNode circuit = node("circuit");
        circuit.setWidth(56);
        circuit.setHeight(130);
        model.getNodes().add(circuit);

        UnifilareEditorLayout.placeNewNode(circuit, model);
        double rowY = circuit.getY();

        UnifilareEditorLayout.Position moved =
                UnifilareEditorLayout.constrainMove(circuit, 200, 300, model, 800, 600, false);
        assertEquals(rowY, moved.y(), 0.001);
        assertEquals(200, moved.x(), 0.001);
    }

    private static GefDiagramNode node(String type) {
        GefDiagramNode n = new GefDiagramNode();
        n.setId(type + "-1");
        n.setType(type);
        n.setWidth(56);
        n.setHeight(130);
        return n;
    }
}
