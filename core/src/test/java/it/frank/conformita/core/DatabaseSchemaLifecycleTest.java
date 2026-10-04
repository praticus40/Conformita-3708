package it.frank.conformita.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.service.ImpresaService;
import it.frank.conformita.core.service.PraticaService;
import jakarta.persistence.EntityManagerFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseSchemaLifecycleTest {

    @TempDir
    Path tempDir;

    @Test
    void fileBootstrap_resetsWhenSchemaVersionMissing_andCreatePraticaSucceeds() {
        PersistenceBootstrap.FileBootstrapResult bootstrap = PersistenceBootstrap.createFileEntityManagerFactory(tempDir);
        assertTrue(bootstrap.lifecycle().wasResetPerformed());

        EntityManagerFactory emf = bootstrap.entityManagerFactory();
        try {
            PraticaService service = new PraticaService(emf);
            PraticaDto created = service.createPratica();
            assertNotNull(created.id());
            assertFalse(created.dichiaraMateriali());
            assertFalse(created.allegatoMateriali());
        } finally {
            emf.close();
        }

        assertEquals(
                DatabaseSchemaLifecycle.SCHEMA_VERSION,
                DatabaseSchemaLifecycle.readStoredVersion(tempDir).orElse(-1));
    }

    @Test
    void fileBootstrap_skipsResetOnSecondStartup() {
        PersistenceBootstrap.FileBootstrapResult first = PersistenceBootstrap.createFileEntityManagerFactory(tempDir);
        first.entityManagerFactory().close();
        assertTrue(first.lifecycle().wasResetPerformed());

        PersistenceBootstrap.FileBootstrapResult second = PersistenceBootstrap.createFileEntityManagerFactory(tempDir);
        second.entityManagerFactory().close();
        assertFalse(second.lifecycle().wasResetPerformed());
    }

    @Test
    void requiresFullReset_v2ToV3_isInPlace() {
        assertFalse(DatabaseSchemaLifecycle.requiresFullReset(2, 3));
        assertTrue(DatabaseSchemaLifecycle.requiresFullReset(1, 3));
        assertTrue(DatabaseSchemaLifecycle.requiresFullReset(0, 3));
        assertFalse(DatabaseSchemaLifecycle.requiresFullReset(3, 3));
    }

    @Test
    void requiresFullReset_v3ToV4_isInPlace() {
        assertFalse(DatabaseSchemaLifecycle.requiresFullReset(3, 4));
    }

    @Test
    void upgradeFrom2To3_preservesData() throws Exception {
        PersistenceBootstrap.FileBootstrapResult first = PersistenceBootstrap.createFileEntityManagerFactory(tempDir);
        EntityManagerFactory emf = first.entityManagerFactory();
        long praticaId;
        try {
            ImpresaService impresaService = new ImpresaService(emf);
            impresaService.saveImpresa(new ImpresaDto(
                    "Impresa Test",
                    "12345678901",
                    "Via Roma 1",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Impianti Elettrici"));
            PraticaService praticaService = new PraticaService(emf);
            PraticaDto created = praticaService.createPratica();
            praticaId = created.id();
            praticaService.savePratica(PraticaDtoCopy.withSchemaPaths(
                    created, null, "pratiche/" + praticaId + "/unifilare/v1.json"));
        } finally {
            emf.close();
        }

        Files.writeString(tempDir.resolve("schema-version"), "2", StandardCharsets.UTF_8);

        PersistenceBootstrap.FileBootstrapResult second = PersistenceBootstrap.createFileEntityManagerFactory(tempDir);
        assertFalse(second.lifecycle().wasResetPerformed());
        assertEquals(2, second.lifecycle().getPreviousVersion());

        EntityManagerFactory emf2 = second.entityManagerFactory();
        try {
            assertTrue(new ImpresaService(emf2).getImpresa().isPresent());
            PraticaDto loaded = new PraticaService(emf2).loadPratica(praticaId);
            assertEquals("pratiche/" + praticaId + "/unifilare/v1.json", loaded.schemaUnifilareJsonPath());
            List<PraticaSummary> list = new PraticaService(emf2).listPratiche();
            assertEquals(1, list.size());
        } finally {
            emf2.close();
        }

        assertEquals(
                DatabaseSchemaLifecycle.SCHEMA_VERSION,
                DatabaseSchemaLifecycle.readStoredVersion(tempDir).orElse(-1));
    }

    @Test
    void inMemoryCreatePratica_persistsExtendedBooleanColumns() {
        EntityManagerFactory emf = PersistenceBootstrap.createInMemoryEntityManagerFactory("schema-test");
        try {
            PraticaDto dto = new PraticaService(emf).createPratica();
            assertNotNull(dto.id());
            assertFalse(dto.provaVistaSezioni());
        } finally {
            emf.close();
        }
    }
}
