package it.frank.conformita.core.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.entity.StatoPratica;
import it.frank.conformita.core.mapper.EntityMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfPacchettoExporterSchemaTest {

    @TempDir
    Path dataDir;

    @Test
    void resolveSchemaPdfPath_readsRelativeFile() throws Exception {
        Path schemaPdf = dataDir.resolve("schemi/1/schema.pdf");
        Files.createDirectories(schemaPdf.getParent());
        Files.writeString(schemaPdf, "pdf-placeholder");

        PraticaDto pratica = minimalPratica(true, 1L);
        Path resolved = PdfPacchettoExporter.resolveSchemaPdfPath(
                        minimalPraticaWithPath("schemi/1/schema.pdf"), dataDir)
                .orElseThrow();
        assertTrue(Files.isSameFile(schemaPdf, resolved));
    }

    private static PraticaDto minimalPraticaWithPath(String schemaPath) {
        PraticaDto base = minimalPratica(true, null);
        return new PraticaDto(
                base.id(),
                base.createdAt(),
                base.updatedAt(),
                base.stato(),
                base.comuneImpianto(),
                base.provincia(),
                base.protocollo(),
                base.dataProtocollo(),
                base.descrizioneImpianto(),
                base.committenteNome(),
                base.committenteCodiceFiscale(),
                base.indirizzoImpianto(),
                base.indirizzoCommittente(),
                base.proprietarioDescrizione(),
                base.comune(),
                base.cap(),
                base.tipoIntervento(),
                base.destinazioneUso(),
                base.altriUsi(),
                base.dataInizio(),
                base.potenzaKw(),
                base.potenzaImpegnabileKw(),
                base.potenzaInstallataKw(),
                base.fotovoltaico(),
                base.altroIntervento(),
                base.tensione(),
                base.tensioneNominale(),
                base.sistemaDistribuzione(),
                base.normaTecnica(),
                base.dichiaraProgetto(),
                base.dichiaraNorma(),
                base.dichiaraMateriali(),
                base.dichiaraControlli(),
                base.allegatoProgetto(),
                base.allegatoMateriali(),
                base.allegatoSchema(),
                base.riferimentiPrecedenti(),
                base.dataDichiarazione(),
                base.responsabileTecnicoNome(),
                base.descrizioneOpere(),
                base.provaVistaSezioni(),
                base.provaVistaBagno(),
                base.provaVistaInterruttori(),
                base.telefonoAssistenza(),
                schemaPath,
                base.schemaUnifilareJsonPath(),
                base.unifilareSchemaId(),
                base.materiali(),
                base.verifiche());
    }

    private static PraticaDto minimalPratica(boolean allegatoSchema, Long unifilareSchemaId) {
        Instant now = Instant.now();
        return new PraticaDto(
                1L,
                now,
                now,
                StatoPratica.BOZZA,
                "Roma",
                "RM",
                null,
                null,
                "Impianto test",
                "Mario Rossi",
                "RSSMRA80A01H501Z",
                "Via Roma 1",
                null,
                null,
                "Roma",
                "00100",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                false,
                allegatoSchema,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                null,
                null,
                null,
                unifilareSchemaId,
                EntityMapper.emptyMateriali(),
                EntityMapper.emptyVerifiche());
    }
}
