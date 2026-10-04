package it.frank.conformita.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.frank.conformita.core.PersistenceBootstrap;
import it.frank.conformita.core.dto.UnifilareSchemaSummary;
import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;
import jakarta.persistence.EntityManagerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SchemaLibraryServiceTest {

    @TempDir
    Path dataDir;

    private EntityManagerFactory emf;

    @AfterEach
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void createLoadSaveAndResolvePdf() throws Exception {
        emf = PersistenceBootstrap.createInMemoryEntityManagerFactory("schema-lib-" + System.nanoTime());
        SchemaLibraryService service = new SchemaLibraryService(emf, dataDir);

        UnifilareSchemaSummary created = service.createSchema("Test schema");
        assertEquals("Test schema", created.name());

        UnifilareSchemaDocumentV2 doc = service.loadDocument(created.id());
        GefDiagramModel diagram = doc.getDiagram();
        GefDiagramNode node = new GefDiagramNode();
        node.setId("n1");
        node.setType("quadro");
        node.setLabel("Q1");
        node.setX(40);
        node.setY(40);
        diagram.getNodes().add(node);
        service.saveDocument(created.id(), doc);

        Path pdf = service.resolvePdfPath(created.id()).orElseThrow();
        assertTrue(Files.isRegularFile(pdf));

        assertFalse(service.listSchemas().isEmpty());
    }
}
