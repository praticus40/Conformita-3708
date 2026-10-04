package it.frank.conformita.core.unifilare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UnifilareJsonCodec {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private UnifilareJsonCodec() {}

    public static UnifilareDocument read(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        return readString(new String(bytes, StandardCharsets.UTF_8));
    }

    public static UnifilareDocument readString(String json) throws IOException {
        return MAPPER.readValue(json, UnifilareDocument.class);
    }

    public static void write(Path file, UnifilareDocument document) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, writeString(document), StandardCharsets.UTF_8);
    }

    public static String writeString(UnifilareDocument document) throws IOException {
        return MAPPER.writeValueAsString(document);
    }
}
