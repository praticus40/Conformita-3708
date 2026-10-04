package it.frank.conformita.core.unifilare;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UnifilarePdfRenderer {

    private UnifilarePdfRenderer() {}

    public static void render(Path targetFile, UnifilareDocument doc) throws IOException {
        try (OutputStream out = Files.newOutputStream(targetFile)) {
            render(out, doc);
        }
    }

    public static void render(OutputStream out, UnifilareDocument doc) throws IOException {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 48, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            document.add(new com.lowagie.text.Paragraph("Schema unifilare", com.lowagie.text.FontFactory.getFont(
                    BaseFont.HELVETICA_BOLD, 14)));
            document.add(new com.lowagie.text.Paragraph(" "));

            PdfContentByte canvas = writer.getDirectContent();
            float pageW = document.getPageSize().getWidth() - 72;
            float pageH = document.getPageSize().getHeight() - 120;
            double docW = doc.getLayout().getWidth();
            double docH = Math.max(doc.getLayout().getHeight(), UnifilareGeometry.branchBottomY(doc));
            float scale = (float) Math.min(pageW / docW, pageH / docH);
            float offsetX = 36;
            float offsetY = 100;

            canvas.saveState();
            canvas.concatCTM(scale, 0, 0, scale, offsetX, offsetY);
            PdfDrawTarget target = new PdfDrawTarget(canvas);
            UnifilareSymbolPainter.paint(doc, target);
            canvas.restoreState();

            document.close();
        } catch (DocumentException e) {
            throw new IOException("PDF schema unifilare fallito", e);
        }
    }

    private static final class PdfDrawTarget implements UnifilareDrawTarget {
        private final PdfContentByte cb;

        PdfDrawTarget(PdfContentByte cb) {
            this.cb = cb;
        }

        @Override
        public void line(double x1, double y1, double x2, double y2) {
            cb.setLineWidth((float) UnifilareGeometry.STROKE);
            cb.moveTo((float) x1, flipY(y1));
            cb.lineTo((float) x2, flipY(y2));
            cb.stroke();
        }

        @Override
        public void rect(double x, double y, double w, double h) {
            cb.setLineWidth((float) UnifilareGeometry.STROKE);
            cb.rectangle((float) x, flipY(y + h), (float) w, (float) h);
            cb.stroke();
        }

        @Override
        public void arc(double cx, double cy, double r, double startDeg, double extentDeg) {
            cb.setLineWidth((float) UnifilareGeometry.STROKE);
            cb.circle((float) cx, flipY(cy), (float) r);
            cb.stroke();
        }

        @Override
        public void text(double x, double y, String text, TextAnchor anchor) {
            cb.beginText();
            try {
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 10);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            float tx = (float) x;
            if (anchor == TextAnchor.MIDDLE) {
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, tx, flipY(y), 0);
            } else if (anchor == TextAnchor.END) {
                cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, text, tx, flipY(y), 0);
            } else {
                cb.showTextAligned(PdfContentByte.ALIGN_LEFT, text, tx, flipY(y), 0);
            }
            cb.endText();
        }

        private float flipY(double y) {
            return (float) (520 - y);
        }
    }
}
