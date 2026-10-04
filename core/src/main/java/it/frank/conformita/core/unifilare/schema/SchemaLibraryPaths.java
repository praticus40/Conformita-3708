package it.frank.conformita.core.unifilare.schema;

public final class SchemaLibraryPaths {

    private SchemaLibraryPaths() {}

    public static String documentRelativePath(long schemaId) {
        return "schemi/" + schemaId + "/document.json";
    }

    public static String pdfRelativePath(long schemaId) {
        return "schemi/" + schemaId + "/schema.pdf";
    }

    public static String lockRelativePath(long schemaId) {
        return "schemi/" + schemaId + "/.lock";
    }
}
