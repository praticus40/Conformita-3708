package it.frank.conformita.core.unifilare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class PraticaUnifilareStorage {

    private final Path dataDirectory;

    public PraticaUnifilareStorage(Path dataDirectory) {
        this.dataDirectory = dataDirectory.toAbsolutePath().normalize();
    }

    public Path resolveRelative(String relativePath) {
        return dataDirectory.resolve(relativePath).normalize();
    }

    public Optional<UnifilareDocument> load(long praticaId, String jsonRelativePath) throws IOException {
        if (jsonRelativePath == null || jsonRelativePath.isBlank()) {
            Path defaultPath = resolveRelative(PraticaUnifilarePaths.jsonRelativePath(praticaId));
            if (!Files.isRegularFile(defaultPath)) {
                return Optional.empty();
            }
            return Optional.of(UnifilareJsonCodec.read(defaultPath));
        }
        Path file = resolveRelative(jsonRelativePath);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        return Optional.of(UnifilareJsonCodec.read(file));
    }

    public String saveJson(long praticaId, UnifilareDocument doc) throws IOException {
        UnifilareValidator.validateOrThrow(doc);
        String rel = PraticaUnifilarePaths.jsonRelativePath(praticaId);
        UnifilareJsonCodec.write(resolveRelative(rel), doc);
        return rel;
    }

    public String renderPdf(long praticaId, UnifilareDocument doc) throws IOException {
        UnifilareValidator.validateOrThrow(doc);
        String rel = PraticaUnifilarePaths.pdfRelativePath(praticaId);
        Path target = resolveRelative(rel);
        UnifilarePdfRenderer.render(target, doc);
        return rel;
    }

    public String copyImportedPdf(long praticaId, Path sourceFile) throws IOException {
        String rel = PraticaUnifilarePaths.importPdfRelativePath(praticaId, sourceFile.getFileName().toString());
        Path target = resolveRelative(rel);
        Files.createDirectories(target.getParent());
        Files.copy(sourceFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return rel;
    }

    public boolean existsRelative(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return false;
        }
        return Files.isRegularFile(resolveRelative(relativePath));
    }
}
