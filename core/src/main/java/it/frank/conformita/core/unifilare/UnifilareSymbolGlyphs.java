package it.frank.conformita.core.unifilare;

import java.util.Map;

/** Scaled CEI-style glyphs for editor palette and node figures. */
public final class UnifilareSymbolGlyphs {

    private UnifilareSymbolGlyphs() {}

    public static void paintNode(
            String type,
            UnifilareDrawTarget target,
            double x,
            double y,
            double w,
            double h,
            String label,
            Map<String, String> properties) {
        if (type == null) {
            type = "circuit";
        }
        switch (type) {
            case "general" -> paintGeneral(target, x, y, w, h, label, properties);
            case "busbar" -> paintBusbar(target, x, y, w, h);
            case "sheet" -> paintSheet(target, x, y, w, h, label);
            default -> paintCircuit(target, x, y, w, h, label, properties);
        }
    }

    private static void paintGeneral(
            UnifilareDrawTarget target,
            double x,
            double y,
            double w,
            double h,
            String label,
            Map<String, String> properties) {
        double cx = x + w / 2;
        String amps = properties != null ? properties.get("inAmps") : null;
        String title = (label != null ? label : "Generale") + (amps != null ? " " + amps + "A" : "");
        target.text(cx, y + 8, title, UnifilareDrawTarget.TextAnchor.MIDDLE);
        double bh = Math.min(h - 16, UnifilareGeometry.BREAKER_HEIGHT);
        target.rect(cx - w * 0.35, y + 14, w * 0.7, bh);
        target.line(cx - w * 0.25, y + 22, cx + w * 0.25, y + bh + 8);
        target.line(cx - w * 0.15, y + 26, cx + w * 0.15, y + bh + 4);
    }

    private static void paintBusbar(UnifilareDrawTarget target, double x, double y, double w, double h) {
        double cy = y + h / 2;
        target.line(x + 4, cy, x + w - 4, cy);
        target.line(x + 4, cy - 3, x + w - 4, cy - 3);
    }

    private static void paintSheet(
            UnifilareDrawTarget target, double x, double y, double w, double h, String label) {
        target.rect(x + 2, y + 2, w - 4, h - 4);
        target.text(x + w / 2, y + h / 2, label != null ? label : "Schema", UnifilareDrawTarget.TextAnchor.MIDDLE);
    }

    private static void paintCircuit(
            UnifilareDrawTarget target,
            double x,
            double y,
            double w,
            double h,
            String label,
            Map<String, String> properties) {
        double cx = x + w / 2;
        target.text(cx, y + 6, label != null ? label : "Linea", UnifilareDrawTarget.TextAnchor.MIDDLE);
        double y0 = y + 18;
        double bh = UnifilareGeometry.BREAKER_HEIGHT * 0.85;
        target.line(cx, y0, cx, y0 + 8);
        target.rect(cx - w * 0.35, y0 + 8, w * 0.7, bh);
        target.line(cx - w * 0.2, y0 + 16, cx + w * 0.2, y0 + bh + 2);
        double yRcd = y0 + 8 + bh;
        target.line(cx, yRcd, cx, yRcd + 4);
        target.rect(cx - w * 0.4, yRcd + 4, w * 0.8, UnifilareGeometry.RCD_HEIGHT * 0.75);
        target.arc(cx, yRcd + 18, 6, 0, 180);
        String ma = properties != null ? properties.get("differentialMa") : null;
        if (ma != null) {
            target.text(cx + w * 0.45, yRcd + 20, ma + "mA", UnifilareDrawTarget.TextAnchor.START);
        }
        double yTerm = yRcd + 4 + UnifilareGeometry.RCD_HEIGHT * 0.75;
        target.line(cx, yTerm, cx, Math.min(y + h - 8, yTerm + 20));
        for (int i = 0; i < 3; i++) {
            double ox = cx - 8 + i * 8;
            target.line(ox, yTerm + 4, ox + 5, yTerm + 16);
        }
    }

    /** Connection guides from busbar to circuit anchor X (center of drop). */
    public static void paintBusConnections(
            UnifilareDrawTarget target,
            double busY,
            double busX1,
            double busX2,
            Iterable<Double> circuitAnchorXs) {
        target.line(busX1, busY, busX2, busY);
        for (Double ax : circuitAnchorXs) {
            if (ax == null) {
                continue;
            }
            target.line(ax, busY, ax, busY + UnifilareGeometry.DROP_FROM_BUS);
        }
    }
}
