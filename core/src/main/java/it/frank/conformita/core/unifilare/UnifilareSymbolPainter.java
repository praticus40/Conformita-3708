package it.frank.conformita.core.unifilare;

import java.util.Comparator;

public final class UnifilareSymbolPainter {

    private UnifilareSymbolPainter() {}

    public static void paint(UnifilareDocument doc, UnifilareDrawTarget target) {
        UnifilareDocument.GeneralSwitch general = doc.getGeneral();
        UnifilareDocument.Busbar bus = doc.getBusbar();

        drawGeneral(general, target);
        target.line(general.getX(), general.getY() + 28, general.getX(), bus.getY());
        target.line(bus.getX1(), bus.getY(), bus.getX2(), bus.getY());

        doc.getCircuits().stream()
                .sorted(Comparator.comparingInt(UnifilareDocument.Circuit::getOrder))
                .forEach(c -> drawCircuit(doc, c, target));
    }

    private static void drawGeneral(UnifilareDocument.GeneralSwitch general, UnifilareDrawTarget target) {
        double x = general.getX();
        double y = general.getY();
        target.text(x, y - 4, general.getLabel() + " " + general.getInAmps() + "A", UnifilareDrawTarget.TextAnchor.MIDDLE);
        target.rect(x - 18, y + 4, 36, UnifilareGeometry.BREAKER_HEIGHT - 8);
        target.line(x - 10, y + 12, x + 10, y + UnifilareGeometry.BREAKER_HEIGHT - 4);
        target.line(x - 6, y + 16, x + 6, y + UnifilareGeometry.BREAKER_HEIGHT - 8);
    }

    private static void drawCircuit(
            UnifilareDocument doc, UnifilareDocument.Circuit circuit, UnifilareDrawTarget target) {
        double x = circuit.getAnchorX();
        double busY = doc.getBusbar().getY();
        double y0 = busY + UnifilareGeometry.DROP_FROM_BUS;

        target.line(x, busY, x, y0);
        target.text(x, busY - 6, circuit.getLabel(), UnifilareDrawTarget.TextAnchor.MIDDLE);
        target.text(x + 42, y0 + 8, circuit.getInAmps() + "A", UnifilareDrawTarget.TextAnchor.START);

        double yBreaker = y0;
        target.rect(x - 14, yBreaker, 28, UnifilareGeometry.BREAKER_HEIGHT);
        target.line(x - 8, yBreaker + 10, x + 8, yBreaker + UnifilareGeometry.BREAKER_HEIGHT - 6);

        double yRcd = yBreaker + UnifilareGeometry.BREAKER_HEIGHT;
        target.line(x, yBreaker + UnifilareGeometry.BREAKER_HEIGHT, x, yRcd);
        drawRcd(x, yRcd, circuit.getDifferentialMa(), target);

        double yTerm = yRcd + UnifilareGeometry.RCD_HEIGHT;
        target.line(x, yRcd + UnifilareGeometry.RCD_HEIGHT, x, yTerm + UnifilareGeometry.TERMINAL_HEIGHT);
        drawTerminal(x, yTerm, target);
    }

    private static void drawRcd(double x, double y, Integer differentialMa, UnifilareDrawTarget target) {
        target.rect(x - 16, y, 32, UnifilareGeometry.RCD_HEIGHT);
        target.arc(x, y + 14, 8, 0, 180);
        target.line(x - 8, y + 22, x + 8, y + 22);
        if (differentialMa != null) {
            target.text(x + 38, y + 18, "I\u2099 " + differentialMa + "mA", UnifilareDrawTarget.TextAnchor.START);
        }
    }

    private static void drawTerminal(double x, double y, UnifilareDrawTarget target) {
        double h = UnifilareGeometry.TERMINAL_HEIGHT;
        for (int i = 0; i < 3; i++) {
            double ox = x - 8 + i * 8;
            target.line(ox, y, ox + 6, y + h);
        }
        target.arc(x, y + h + 4, 4, 0, 360);
    }
}
