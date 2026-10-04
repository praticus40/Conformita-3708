package it.frank.conformita.core.unifilare;

public interface UnifilareDrawTarget {

    void line(double x1, double y1, double x2, double y2);

    void rect(double x, double y, double w, double h);

    void arc(double cx, double cy, double r, double startDeg, double extentDeg);

    void text(double x, double y, String text, TextAnchor anchor);

    enum TextAnchor {
        START,
        MIDDLE,
        END
    }
}
