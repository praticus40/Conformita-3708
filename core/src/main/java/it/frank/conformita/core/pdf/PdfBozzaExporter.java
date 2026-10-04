package it.frank.conformita.core.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.PraticaDto;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PdfBozzaExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void export(Path targetFile, Optional<ImpresaDto> impresa, PraticaDto pratica) {
        try (OutputStream out = Files.newOutputStream(targetFile)) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Bozza dichiarazione di conformità", titleFont));
            document.add(new Paragraph("Documento non ufficiale — solo per revisione dati.", bodyFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Impresa installatrice", sectionFont));
            if (impresa.isPresent()) {
                ImpresaDto i = impresa.get();
                document.add(line(bodyFont, "Ragione sociale", i.ragioneSociale()));
                document.add(line(bodyFont, "Partita IVA", i.partitaIva()));
                document.add(line(bodyFont, "Sede legale", i.sedeLegale()));
                document.add(line(bodyFont, "REA", i.rea()));
                document.add(line(bodyFont, "Responsabile tecnico", i.responsabileTecnico()));
            } else {
                document.add(new Paragraph("Anagrafica impresa non presente.", bodyFont));
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Committente e ubicazione", sectionFont));
            document.add(line(bodyFont, "Committente", pratica.committenteNome()));
            document.add(line(bodyFont, "Codice fiscale", pratica.committenteCodiceFiscale()));
            document.add(line(bodyFont, "Indirizzo impianto", pratica.indirizzoImpianto()));
            document.add(line(bodyFont, "Comune", pratica.comune()));
            document.add(line(bodyFont, "CAP", pratica.cap()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Intervento e impianto", sectionFont));
            document.add(line(bodyFont, "Tipo intervento", enumLabel(pratica.tipoIntervento())));
            document.add(line(bodyFont, "Destinazione d'uso", enumLabel(pratica.destinazioneUso())));
            document.add(line(
                    bodyFont,
                    "Data inizio",
                    pratica.dataInizio() != null ? pratica.dataInizio().format(DATE_FORMAT) : null));
            document.add(line(
                    bodyFont,
                    "Potenza (kW)",
                    pratica.potenzaKw() != null ? String.valueOf(pratica.potenzaKw()) : null));
            document.add(line(
                    bodyFont,
                    "Tensione",
                    pratica.tensione() != null ? pratica.tensione().getLabel() : null));

            document.close();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Export PDF fallito: " + targetFile, e);
        }
    }

    private static Paragraph line(Font font, String label, String value) {
        String text = label + ": " + (value == null || value.isBlank() ? "—" : value);
        return new Paragraph(text, font);
    }

    private static String enumLabel(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
