package it.frank.conformita.core;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.dto.PraticaDtoCopy;
import it.frank.conformita.core.dto.UnifilareSchemaSummary;
import it.frank.conformita.core.pdf.PdfBozzaExporter;
import it.frank.conformita.core.pdf.PdfPacchettoExporter;
import it.frank.conformita.core.service.ImpresaService;
import it.frank.conformita.core.service.PraticaService;
import it.frank.conformita.core.service.SchemaLibraryService;
import it.frank.conformita.core.service.ValidationService;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;
import it.frank.conformita.core.unifilare.PraticaUnifilareStorage;
import it.frank.conformita.core.unifilare.UnifilareDocument;
import it.frank.conformita.core.unifilare.UnifilareTemplates;
import it.frank.conformita.core.validation.PersistValidationException;
import it.frank.conformita.core.validation.ValidationResult;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ApplicationFacade {

    private EntityManagerFactory entityManagerFactory;
    private Path dataDirectory;

    private ImpresaService impresaService;
    private PraticaService praticaService;
    private ValidationService validationService;
    private PdfBozzaExporter pdfBozzaExporter;
    private PdfPacchettoExporter pdfPacchettoExporter;
    private PraticaUnifilareStorage unifilareStorage;
    private SchemaLibraryService schemaLibraryService;
    private boolean databaseReset;

    public void startup(Path dataDir) {
        shutdown();
        this.dataDirectory = dataDir.toAbsolutePath().normalize();
        PersistenceBootstrap.FileBootstrapResult bootstrap =
                PersistenceBootstrap.createFileEntityManagerFactory(dataDirectory);
        this.entityManagerFactory = bootstrap.entityManagerFactory();
        this.databaseReset = bootstrap.lifecycle().wasResetPerformed();
        this.impresaService = new ImpresaService(entityManagerFactory);
        this.praticaService = new PraticaService(entityManagerFactory);
        this.validationService = new ValidationService();
        this.pdfBozzaExporter = new PdfBozzaExporter();
        this.pdfPacchettoExporter = new PdfPacchettoExporter();
        this.unifilareStorage = new PraticaUnifilareStorage(dataDirectory);
        this.schemaLibraryService = new SchemaLibraryService(entityManagerFactory, dataDirectory);
    }

    public void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
        entityManagerFactory = null;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    /** True when file DB was wiped before startup due to a schema version upgrade. */
    public boolean wasDatabaseReset() {
        return databaseReset;
    }

    public Optional<ImpresaDto> getImpresa() {
        return impresaService.getImpresa();
    }

    public ValidationResult validateImpresaForPersistence(ImpresaDto dto) {
        return validationService.validateForPersistence(dto);
    }

    public ValidationResult validatePraticaForPersistence(PraticaDto dto) {
        return validationService.validateForPersistence(dto);
    }

    public ImpresaDto saveImpresa(ImpresaDto dto) {
        ValidationResult check = validationService.validateForPersistence(dto);
        if (check.hasErrors()) {
            throw new PersistValidationException(check);
        }
        return impresaService.saveImpresa(dto);
    }

    public List<PraticaSummary> listPratiche() {
        return praticaService.listPratiche();
    }

    public PraticaDto createPratica() {
        return praticaService.createPratica();
    }

    public PraticaDto loadPratica(long id) {
        return praticaService.loadPratica(id);
    }

    public PraticaDto savePratica(PraticaDto dto) {
        ValidationResult check = validationService.validateForPersistence(dto);
        if (check.hasErrors()) {
            throw new PersistValidationException(check);
        }
        return praticaService.savePratica(dto);
    }

    public void deletePratica(long id) {
        praticaService.deletePratica(id);
    }

    public ValidationResult validatePratica(long id) {
        PraticaDto pratica = praticaService.loadPratica(id);
        return validationService.validate(impresaService.getImpresa(), pratica);
    }

    public void exportPdfBozza(long id, Path targetFile) {
        ValidationResult validation = validatePratica(id);
        if (validation.hasErrors()) {
            throw new IllegalStateException("Validazione non superata: impossibile esportare il PDF");
        }
        PraticaDto pratica = praticaService.loadPratica(id);
        pdfBozzaExporter.export(targetFile, impresaService.getImpresa(), pratica);
    }

    public void exportPdfPacchetto(long id, Path targetFile) {
        ValidationResult validation = validatePratica(id);
        if (validation.hasErrors()) {
            throw new IllegalStateException("Validazione non superata: impossibile esportare il pacchetto PDF");
        }
        PraticaDto pratica = praticaService.loadPratica(id);
        Optional<Path> schemaPdf = resolveSchemaPdfForExport(pratica);
        pdfPacchettoExporter.export(targetFile, impresaService.getImpresa(), pratica, schemaPdf);
    }

    public List<UnifilareSchemaSummary> listSchemaLibrary() {
        return schemaLibraryService.listSchemas();
    }

    public UnifilareSchemaSummary createSchemaLibraryEntry(String name) {
        return schemaLibraryService.createSchema(name);
    }

    public void deleteSchemaLibraryEntry(long schemaId) {
        schemaLibraryService.deleteSchema(schemaId);
    }

    public UnifilareSchemaSummary duplicateSchemaLibraryEntry(long sourceId, String newName) {
        return schemaLibraryService.duplicateSchema(sourceId, newName);
    }

    public UnifilareSchemaDocumentV2 loadSchemaLibraryDocument(long schemaId) {
        return schemaLibraryService.loadDocument(schemaId);
    }

    public void saveSchemaLibraryDocument(long schemaId, UnifilareSchemaDocumentV2 document) {
        schemaLibraryService.saveDocument(schemaId, document);
    }

    public Path renderSchemaLibraryPdf(long schemaId) {
        return schemaLibraryService.renderPdf(schemaId);
    }

    public PraticaDto associateUnifilareSchema(long praticaId, Long schemaId) {
        PraticaDto pratica = praticaService.loadPratica(praticaId);
        return savePratica(PraticaDtoCopy.withUnifilareSchemaId(pratica, schemaId));
    }

    public boolean isSchemaLibraryLocked(long schemaId) {
        return schemaLibraryService.isLocked(schemaId);
    }

    public void acquireSchemaLibraryLock(long schemaId, String owner) {
        schemaLibraryService.acquireLock(schemaId, owner);
    }

    public void releaseSchemaLibraryLock(long schemaId) {
        schemaLibraryService.releaseLock(schemaId);
    }

    public Optional<Path> resolveSchemaPdfForExport(PraticaDto pratica) {
        if (!pratica.allegatoSchema()) {
            return Optional.empty();
        }
        if (pratica.unifilareSchemaId() != null) {
            return schemaLibraryService.resolvePdfPath(pratica.unifilareSchemaId());
        }
        return PdfPacchettoExporter.resolveSchemaPdfPath(pratica, dataDirectory);
    }

    public UnifilareDocument loadUnifilareOrDefault(long praticaId) {
        try {
            PraticaDto pratica = praticaService.loadPratica(praticaId);
            Optional<UnifilareDocument> loaded =
                    unifilareStorage.load(praticaId, pratica.schemaUnifilareJsonPath());
            return loaded.orElseGet(UnifilareTemplates::residentialDefault);
        } catch (IOException e) {
            throw new IllegalStateException("Lettura schema unifilare fallita", e);
        }
    }

    public PraticaDto saveUnifilareDocument(long praticaId, UnifilareDocument document) {
        try {
            String jsonRel = unifilareStorage.saveJson(praticaId, document);
            PraticaDto pratica = praticaService.loadPratica(praticaId);
            PraticaDto updated = PraticaDtoCopy.withSchemaPaths(
                    pratica, pratica.schemaAllegatoPath(), jsonRel);
            return savePratica(updated);
        } catch (IOException e) {
            throw new IllegalStateException("Salvataggio schema unifilare fallito", e);
        }
    }

    public PraticaDto renderUnifilarePdf(long praticaId, UnifilareDocument document) {
        try {
            String jsonRel = unifilareStorage.saveJson(praticaId, document);
            String pdfRel = unifilareStorage.renderPdf(praticaId, document);
            PraticaDto pratica = praticaService.loadPratica(praticaId);
            return savePratica(PraticaDtoCopy.withSchemaPaths(pratica, pdfRel, jsonRel));
        } catch (IOException e) {
            throw new IllegalStateException("Generazione PDF schema fallita", e);
        }
    }

    public PraticaDto importExternalSchemaPdf(long praticaId, Path sourcePdf) {
        try {
            String pdfRel = unifilareStorage.copyImportedPdf(praticaId, sourcePdf);
            PraticaDto pratica = praticaService.loadPratica(praticaId);
            return savePratica(PraticaDtoCopy.withSchemaPaths(
                    pratica, pdfRel, pratica.schemaUnifilareJsonPath()));
        } catch (IOException e) {
            throw new IllegalStateException("Import PDF schema fallito", e);
        }
    }

    public boolean hasUnifilareJson(long praticaId) {
        PraticaDto pratica = praticaService.loadPratica(praticaId);
        return unifilareStorage.existsRelative(pratica.schemaUnifilareJsonPath());
    }

    public boolean hasSchemaPdf(long praticaId) {
        PraticaDto pratica = praticaService.loadPratica(praticaId);
        return unifilareStorage.existsRelative(pratica.schemaAllegatoPath());
    }
}
