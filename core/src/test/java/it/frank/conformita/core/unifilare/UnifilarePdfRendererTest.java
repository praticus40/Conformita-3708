package it.frank.conformita.core.unifilare;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

class UnifilarePdfRendererTest {

    @Test
    void rendersNonEmptyPdf() throws Exception {
        UnifilareDocument doc = UnifilareTemplates.residentialDefault();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        UnifilarePdfRenderer.render(baos, doc);
        byte[] bytes = baos.toByteArray();
        assertTrue(bytes.length > 500);
        PdfReader reader = new PdfReader(bytes);
        assertTrue(reader.getNumberOfPages() >= 1);
        reader.close();
    }
}
