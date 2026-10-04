package it.frank.conformita.core.unifilare;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UnifilareSvgExporter {

    private UnifilareSvgExporter() {}

    public static String toSvgString(UnifilareDocument doc) {
        StringBuilder sb = new StringBuilder();
        double w = doc.getLayout().getWidth();
        double h = Math.max(doc.getLayout().getHeight(), UnifilareGeometry.branchBottomY(doc));
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
                .append(w)
                .append("\" height=\"")
                .append(h)
                .append("\" viewBox=\"0 0 ")
                .append(w)
                .append(' ')
                .append(h)
                .append("\">\n");
        sb.append("<g stroke=\"black\" stroke-width=\"")
                .append(UnifilareGeometry.STROKE)
                .append("\" fill=\"none\">\n");
        SvgTarget target = new SvgTarget(sb);
        UnifilareSymbolPainter.paint(doc, target);
        sb.append("</g>\n</svg>");
        return sb.toString();
    }

    public static void write(Path file, UnifilareDocument doc) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, toSvgString(doc), StandardCharsets.UTF_8);
    }

    private static final class SvgTarget implements UnifilareDrawTarget {
        private final StringBuilder sb;

        SvgTarget(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void line(double x1, double y1, double x2, double y2) {
            sb.append("<line x1=\"")
                    .append(x1)
                    .append("\" y1=\"")
                    .append(y1)
                    .append("\" x2=\"")
                    .append(x2)
                    .append("\" y2=\"")
                    .append(y2)
                    .append("\"/>\n");
        }

        @Override
        public void rect(double x, double y, double w, double h) {
            sb.append("<rect x=\"")
                    .append(x)
                    .append("\" y=\"")
                    .append(y)
                    .append("\" width=\"")
                    .append(w)
                    .append("\" height=\"")
                    .append(h)
                    .append("\"/>\n");
        }

        @Override
        public void arc(double cx, double cy, double r, double startDeg, double extentDeg) {
            sb.append("<circle cx=\"")
                    .append(cx)
                    .append("\" cy=\"")
                    .append(cy)
                    .append("\" r=\"")
                    .append(r)
                    .append("\"/>\n");
        }

        @Override
        public void text(double x, double y, String text, TextAnchor anchor) {
            String anchorAttr =
                    switch (anchor) {
                        case MIDDLE -> "middle";
                        case END -> "end";
                        default -> "start";
                    };
            sb.append("<text x=\"")
                    .append(x)
                    .append("\" y=\"")
                    .append(y)
                    .append("\" font-family=\"sans-serif\" font-size=\"11\" fill=\"black\" stroke=\"none\" text-anchor=\"")
                    .append(anchorAttr)
                    .append("\">")
                    .append(escape(text))
                    .append("</text>\n");
        }

        private static String escape(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
