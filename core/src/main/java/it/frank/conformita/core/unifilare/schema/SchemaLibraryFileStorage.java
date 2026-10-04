package it.frank.conformita.core.unifilare.schema;

import it.frank.conformita.core.unifilare.UnifilarePdfRenderer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

public final class SchemaLibraryFileStorage {

    private final Path dataDirectory;

    public SchemaLibraryFileStorage(Path dataDirectory) {
        this.dataDirectory = dataDirectory.toAbsolutePath().normalize();
    }

    public Path resolveRelative(String relativePath) {
        return dataDirectory.resolve(relativePath.replace('\\', '/')).normalize();
    }

    public UnifilareSchemaDocumentV2 loadDocument(String relativePath) throws IOException {
        Path file = resolveRelative(relativePath);
        if (!Files.isRegularFile(file)) {
            return defaultDocument();
        }
        return SchemaDocumentCodec.read(file);
    }

    public void saveDocument(String relativePath, UnifilareSchemaDocumentV2 document) throws IOException {
        SchemaDocumentCodec.write(resolveRelative(relativePath), document);
    }

    public Path renderPdf(long schemaId, UnifilareSchemaDocumentV2 document) throws IOException {
        Path pdf = resolveRelative(SchemaLibraryPaths.pdfRelativePath(schemaId));
        Files.createDirectories(pdf.getParent());
        UnifilarePdfRenderer.render(pdf, GefDiagramToUnifilareAdapter.toUnifilareV1(document));
        return pdf;
    }

    public boolean isLocked(long schemaId) {
        return Files.isRegularFile(resolveRelative(SchemaLibraryPaths.lockRelativePath(schemaId)));
    }

    public void acquireLock(long schemaId, String owner) throws IOException {
        Path lock = resolveRelative(SchemaLibraryPaths.lockRelativePath(schemaId));
        Files.createDirectories(lock.getParent());
        Files.writeString(lock, owner + "@" + Instant.now(), StandardCharsets.UTF_8);
    }

    public void releaseLock(long schemaId) throws IOException {
        Files.deleteIfExists(resolveRelative(SchemaLibraryPaths.lockRelativePath(schemaId)));
    }

    public Optional<Path> pdfIfExists(long schemaId) {
        Path pdf = resolveRelative(SchemaLibraryPaths.pdfRelativePath(schemaId));
        return Files.isRegularFile(pdf) ? Optional.of(pdf) : Optional.empty();
    }

    public static UnifilareSchemaDocumentV2 defaultDocument() {
        UnifilareSchemaDocumentV2 doc = new UnifilareSchemaDocumentV2();
        GefDiagramNode root = new GefDiagramNode();
        root.setId("sheet");
        root.setType("sheet");
        root.setLabel("Schema");
        doc.getDiagram().getNodes().add(root);
        return doc;
    }
}
