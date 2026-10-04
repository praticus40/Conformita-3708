package it.frank.conformita.core.service;

import it.frank.conformita.core.dto.UnifilareSchemaSummary;
import it.frank.conformita.core.entity.UnifilareSchemaLibraryEntry;
import it.frank.conformita.core.unifilare.schema.SchemaLibraryFileStorage;
import it.frank.conformita.core.unifilare.schema.SchemaLibraryPaths;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class SchemaLibraryService {

    private final EntityManagerFactory entityManagerFactory;
    private final SchemaLibraryFileStorage fileStorage;

    public SchemaLibraryService(EntityManagerFactory entityManagerFactory, Path dataDirectory) {
        this.entityManagerFactory = entityManagerFactory;
        this.fileStorage = new SchemaLibraryFileStorage(dataDirectory);
    }

    public List<UnifilareSchemaSummary> listSchemas() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return em.createQuery(
                            "select s from UnifilareSchemaLibraryEntry s order by s.updatedAt desc",
                            UnifilareSchemaLibraryEntry.class)
                    .getResultStream()
                    .map(s -> new UnifilareSchemaSummary(s.getId(), s.getName(), s.getUpdatedAt()))
                    .toList();
        } finally {
            em.close();
        }
    }

    public UnifilareSchemaSummary createSchema(String name) {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            UnifilareSchemaLibraryEntry entry = new UnifilareSchemaLibraryEntry();
            entry.setName(name == null || name.isBlank() ? "Nuovo schema" : name.trim());
            Instant now = Instant.now();
            entry.setCreatedAt(now);
            entry.setUpdatedAt(now);
            entry.setDocumentPath("pending");
            em.persist(entry);
            em.flush();
            entry.setDocumentPath(SchemaLibraryPaths.documentRelativePath(entry.getId()));
            em.merge(entry);
            tx.commit();
            fileStorage.saveDocument(entry.getDocumentPath(), SchemaLibraryFileStorage.defaultDocument());
            return new UnifilareSchemaSummary(entry.getId(), entry.getName(), entry.getUpdatedAt());
        } catch (IOException e) {
            tx.rollback();
            throw new IllegalStateException("Creazione schema fallita", e);
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public UnifilareSchemaDocumentV2 loadDocument(long schemaId) {
        UnifilareSchemaLibraryEntry entry = findEntryOrThrow(schemaId);
        try {
            return fileStorage.loadDocument(entry.getDocumentPath());
        } catch (IOException e) {
            throw new IllegalStateException("Lettura schema " + schemaId, e);
        }
    }

    public void saveDocument(long schemaId, UnifilareSchemaDocumentV2 document) {
        UnifilareSchemaLibraryEntry entry = findEntryOrThrow(schemaId);
        try {
            fileStorage.saveDocument(entry.getDocumentPath(), document);
            touchUpdatedAt(schemaId);
        } catch (IOException e) {
            throw new IllegalStateException("Salvataggio schema " + schemaId, e);
        }
    }

    public Path renderPdf(long schemaId) {
        UnifilareSchemaDocumentV2 document = loadDocument(schemaId);
        try {
            return fileStorage.renderPdf(schemaId, document);
        } catch (IOException e) {
            throw new IllegalStateException("PDF schema " + schemaId, e);
        }
    }

    public Optional<Path> resolvePdfPath(long schemaId) {
        Optional<Path> cached = fileStorage.pdfIfExists(schemaId);
        if (cached.isPresent()) {
            return cached;
        }
        return Optional.of(renderPdf(schemaId));
    }

    public void deleteSchema(long schemaId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            UnifilareSchemaLibraryEntry entry = em.find(UnifilareSchemaLibraryEntry.class, schemaId);
            if (entry != null) {
                em.remove(entry);
            }
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public UnifilareSchemaSummary duplicateSchema(long sourceId, String newName) {
        UnifilareSchemaDocumentV2 source = loadDocument(sourceId);
        UnifilareSchemaSummary created = createSchema(newName);
        saveDocument(created.id(), source);
        return created;
    }

    public boolean isLocked(long schemaId) {
        return fileStorage.isLocked(schemaId);
    }

    public void acquireLock(long schemaId, String owner) {
        try {
            if (fileStorage.isLocked(schemaId)) {
                throw new IllegalStateException("Schema già in modifica");
            }
            fileStorage.acquireLock(schemaId, owner);
        } catch (IOException e) {
            throw new IllegalStateException("Lock schema " + schemaId, e);
        }
    }

    public void releaseLock(long schemaId) {
        try {
            fileStorage.releaseLock(schemaId);
        } catch (IOException e) {
            throw new IllegalStateException("Unlock schema " + schemaId, e);
        }
    }

    public SchemaLibraryFileStorage fileStorage() {
        return fileStorage;
    }

    private void touchUpdatedAt(long schemaId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            UnifilareSchemaLibraryEntry entry = em.find(UnifilareSchemaLibraryEntry.class, schemaId);
            if (entry != null) {
                entry.setUpdatedAt(Instant.now());
                em.merge(entry);
            }
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private UnifilareSchemaLibraryEntry findEntryOrThrow(long schemaId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            UnifilareSchemaLibraryEntry entry = em.find(UnifilareSchemaLibraryEntry.class, schemaId);
            if (entry == null) {
                throw new IllegalArgumentException("Schema non trovato: " + schemaId);
            }
            return entry;
        } finally {
            em.close();
        }
    }
}
