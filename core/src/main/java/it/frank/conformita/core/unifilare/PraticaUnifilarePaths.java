package it.frank.conformita.core.unifilare;

public final class PraticaUnifilarePaths {

    private PraticaUnifilarePaths() {}

    public static String jsonRelativePath(long praticaId) {
        return "pratiche/" + praticaId + "/unifilare/v1.json";
    }

    public static String pdfRelativePath(long praticaId) {
        return "pratiche/" + praticaId + "/unifilare/schema-unifilare.pdf";
    }

    public static String importPdfRelativePath(long praticaId, String originalName) {
        String safe = originalName == null ? "import.pdf" : originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "pratiche/" + praticaId + "/unifilare/import-" + safe;
    }
}
