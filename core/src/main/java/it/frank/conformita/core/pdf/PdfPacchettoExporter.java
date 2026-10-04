package it.frank.conformita.core.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.MaterialeRigaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.VerificaRigaDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PdfPacchettoExporter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TEMPLATE = "/templates/dico-pacchetto-template.pdf";

    public void export(
            Path targetFile, Optional<ImpresaDto> impresa, PraticaDto pratica, Optional<Path> schemaPdfAttachment) {
        try {
            byte[] templateStamped = stampTemplate(impresa, pratica);
            byte[] extraPages = buildSupplementPages(impresa, pratica);
            List<Path> attachments = new ArrayList<>();
            schemaPdfAttachment.ifPresent(attachments::add);
            mergeToFile(targetFile, templateStamped, extraPages, 3, attachments);
        } catch (IOException e) {
            throw new IllegalStateException("Export pacchetto PDF fallito: " + targetFile, e);
        }
    }

    public static Optional<Path> resolveSchemaPdfPath(PraticaDto pratica, Path dataDirectory) {
        String path = pratica.schemaAllegatoPath();
        if (path == null || path.isBlank()) {
            return Optional.empty();
        }
        Path relative = dataDirectory.resolve(path.replace('\\', '/')).normalize();
        if (Files.isRegularFile(relative)) {
            return Optional.of(relative);
        }
        Path absolute = Path.of(path);
        if (Files.isRegularFile(absolute)) {
            return Optional.of(absolute);
        }
        return Optional.empty();
    }

    private byte[] stampTemplate(Optional<ImpresaDto> impresa, PraticaDto pratica) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(TEMPLATE)) {
            if (in == null) {
                throw new IllegalStateException("Template PDF non trovato: " + TEMPLATE);
            }
            PdfReader reader = new PdfReader(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, baos);
            Map<String, String> values = buildFieldValues(impresa, pratica);
            overlayPage(stamper, 1, values);
            overlayPage(stamper, 2, values);
            stamper.close();
            reader.close();
            return baos.toByteArray();
        }
    }

    private void overlayPage(PdfStamper stamper, int page, Map<String, String> values) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        float[][] positions = page == 1 ? page1Positions() : page2Positions();
        String[] keys = page == 1 ? page1Keys() : page2Keys();
        for (int i = 0; i < keys.length; i++) {
            String text = values.getOrDefault(keys[i], "");
            if (text.isBlank()) {
                continue;
            }
            float x = positions[i][0];
            float y = positions[i][1];
            ColumnText.showTextAligned(
                    stamper.getOverContent(page), Element.ALIGN_LEFT, new Phrase(text, font), x, y, 0);
        }
    }

    private static String[] page1Keys() {
        return new String[] {
            "comuneImpianto", "provincia", "protocollo", "descrizioneImpianto", "indirizzoImpianto", "committenteNome",
            "impresaRagioneSociale"
        };
    }

    private static float[][] page1Positions() {
        return new float[][] {
            {120, 720}, {120, 705}, {120, 690}, {80, 620}, {120, 580}, {120, 555}, {120, 530}
        };
    }

    private static String[] page2Keys() {
        return new String[] {
            "legaleRappresentante", "impresaRagioneSociale", "committenteNome", "indirizzoImpianto", "dataDichiarazione"
        };
    }

    private static float[][] page2Positions() {
        return new float[][] {
            {180, 750}, {300, 735}, {160, 520}, {120, 480}, {100, 180}
        };
    }

    private Map<String, String> buildFieldValues(Optional<ImpresaDto> impresa, PraticaDto pratica) {
        Map<String, String> map = new HashMap<>();
        map.put("comuneImpianto", nv(pratica.comuneImpianto()));
        map.put("provincia", nv(pratica.provincia()));
        map.put("protocollo", nv(pratica.protocollo()));
        map.put("descrizioneImpianto", nv(pratica.descrizioneImpianto()));
        map.put("indirizzoImpianto", nv(pratica.indirizzoImpianto()));
        map.put("committenteNome", nv(pratica.committenteNome()));
        impresa.ifPresent(i -> {
            map.put("impresaRagioneSociale", nv(i.ragioneSociale()));
            map.put("legaleRappresentante", nv(i.legaleRappresentante() != null ? i.legaleRappresentante() : i.responsabileTecnico()));
        });
        if (pratica.dataDichiarazione() != null) {
            map.put("dataDichiarazione", pratica.dataDichiarazione().format(DATE));
        }
        return map;
    }

    private byte[] buildSupplementPages(Optional<ImpresaDto> impresa, PraticaDto pratica) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font body = FontFactory.getFont(FontFactory.HELVETICA, 11);

        document.newPage();
        document.add(new Paragraph("RELAZIONE TECNICA (estratto)", title));
        document.add(new Paragraph("Tensione: " + nv(pratica.tensioneNominale()), body));
        document.add(new Paragraph("Sistema: " + (pratica.sistemaDistribuzione() != null ? pratica.sistemaDistribuzione().name() : "—"), body));
        document.add(new Paragraph("Potenza installata: " + nv(pratica.potenzaInstallataKw()), body));
        document.add(new Paragraph("Norma: " + nv(pratica.normaTecnica()), body));
        document.add(new Paragraph(nv(pratica.descrizioneOpere()), body));

        document.newPage();
        document.add(new Paragraph("RELAZIONE TIPOLOGICA MATERIALI", title));
        if (pratica.materiali() != null) {
            for (MaterialeRigaDto row : pratica.materiali()) {
                document.add(new Paragraph(
                        "- " + row.descrizione() + " | " + nv(row.marchio()) + " | " + nv(row.modello()), body));
            }
        }

        document.newPage();
        document.add(new Paragraph("VERIFICHE E PROVE", title));
        document.add(new Paragraph("Prove a vista: sezioni=" + pratica.provaVistaSezioni()
                + ", bagno=" + pratica.provaVistaBagno()
                + ", interruttori=" + pratica.provaVistaInterruttori(), body));
        if (pratica.verifiche() != null) {
            for (VerificaRigaDto v : pratica.verifiche()) {
                document.add(new Paragraph("- " + v.tipoProva() + ": " + nv(v.valore()), body));
            }
        }

        document.newPage();
        document.add(new Paragraph("LIBRETTO USO E MANUTENZIONE (estratto)", title));
        impresa.ifPresent(i -> document.add(new Paragraph("Impresa: " + i.ragioneSociale(), body)));
        document.add(new Paragraph("Telefono assistenza: " + nv(pratica.telefonoAssistenza()), body));

        document.close();
        return baos.toByteArray();
    }

    private void mergeToFile(
            Path target,
            byte[] templateStamped,
            byte[] extraPages,
            int templatePagesToInclude,
            List<Path> attachmentPdfs)
            throws IOException {
        try (OutputStream out = Files.newOutputStream(target)) {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, out);
            document.open();

            PdfReader stamped = new PdfReader(templateStamped);
            int pages = Math.min(templatePagesToInclude, stamped.getNumberOfPages());
            for (int i = 1; i <= pages; i++) {
                copy.addPage(copy.getImportedPage(stamped, i));
            }
            stamped.close();

            PdfReader extra = new PdfReader(extraPages);
            for (int i = 1; i <= extra.getNumberOfPages(); i++) {
                copy.addPage(copy.getImportedPage(extra, i));
            }
            extra.close();

            if (attachmentPdfs != null) {
                for (Path attachment : attachmentPdfs) {
                    PdfReader attachmentReader = new PdfReader(Files.readAllBytes(attachment));
                    for (int i = 1; i <= attachmentReader.getNumberOfPages(); i++) {
                        copy.addPage(copy.getImportedPage(attachmentReader, i));
                    }
                    attachmentReader.close();
                }
            }

            document.close();
        }
    }

    private static String nv(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private static String nv(String value) {
        return value == null ? "" : value;
    }
}
