package it.frank.conformita.core.unifilare;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UnifilareTemplates {

    private UnifilareTemplates() {}

    public static UnifilareDocument residentialDefault() {
        return residentialDefault(30);
    }

    public static UnifilareDocument residentialDefault(int differentialMa) {
        UnifilareDocument doc = new UnifilareDocument();
        doc.getGeneral().setLabel("Generale");
        doc.getGeneral().setInAmps(32);
        doc.getGeneral().setX(400);
        doc.getGeneral().setY(40);

        doc.getBusbar().setY(120);
        doc.getBusbar().setX1(80);
        doc.getBusbar().setX2(720);

        doc.setCircuits(new ArrayList<>(List.of(
                circuit("Linea Prese", 16, 150, 0, differentialMa),
                circuit("Linea Luce", 10, 290, 1, differentialMa),
                circuit("Linea P.cottura", 25, 430, 2, differentialMa),
                circuit("Linea Cucina", 16, 570, 3, differentialMa),
                circuit("Linea Condizionatore", 16, 710, 4, differentialMa))));
        return doc;
    }

    private static UnifilareDocument.Circuit circuit(
            String label, int inAmps, double anchorX, int order, int differentialMa) {
        UnifilareDocument.Circuit c = new UnifilareDocument.Circuit();
        c.setId(UUID.randomUUID().toString().substring(0, 8));
        c.setLabel(label);
        c.setInAmps(inAmps);
        c.setAnchorX(anchorX);
        c.setOrder(order);
        c.setDifferentialMa(differentialMa);
        return c;
    }

    public static UnifilareDocument emptyWithOneCircuit() {
        UnifilareDocument doc = new UnifilareDocument();
        doc.getGeneral().setInAmps(32);
        UnifilareDocument.Circuit c = new UnifilareDocument.Circuit();
        c.setId(UUID.randomUUID().toString().substring(0, 8));
        c.setLabel("Linea 1");
        c.setInAmps(16);
        c.setDifferentialMa(30);
        c.setAnchorX(400);
        c.setOrder(0);
        doc.getCircuits().add(c);
        return doc;
    }
}
