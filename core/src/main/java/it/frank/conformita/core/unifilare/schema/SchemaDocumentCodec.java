package it.frank.conformita.core.unifilare.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SchemaDocumentCodec {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private SchemaDocumentCodec() {}

    public static UnifilareSchemaDocumentV2 read(Path file) throws IOException {
        return readString(Files.readString(file, StandardCharsets.UTF_8));
    }

    public static UnifilareSchemaDocumentV2 readString(String json) throws IOException {
        return MAPPER.readValue(json, UnifilareSchemaDocumentV2.class);
    }

    public static void write(Path file, UnifilareSchemaDocumentV2 document) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, writeString(document), StandardCharsets.UTF_8);
    }

    public static String writeString(UnifilareSchemaDocumentV2 document) throws IOException {
        return MAPPER.writeValueAsString(document);
    }
}
