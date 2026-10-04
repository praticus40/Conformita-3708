package it.frank.conformita.core.unifilare;

/** Shared layout metrics for canvas and PDF/SVG renderers. */
public final class UnifilareGeometry {

    public static final double STROKE = 1.5;
    public static final double BREAKER_HEIGHT = 36;
    public static final double RCD_HEIGHT = 44;
    public static final double DROP_FROM_BUS = 28;
    public static final double TERMINAL_HEIGHT = 36;
    public static final double LABEL_OFFSET = 14;
    public static final double SNAP_GRID = 10;

    private UnifilareGeometry() {}

    public static double snap(double value) {
        return Math.round(value / SNAP_GRID) * SNAP_GRID;
    }

    public static double branchBottomY(UnifilareDocument doc) {
        return doc.getBusbar().getY() + DROP_FROM_BUS + BREAKER_HEIGHT + RCD_HEIGHT + TERMINAL_HEIGHT + 40;
    }
}
