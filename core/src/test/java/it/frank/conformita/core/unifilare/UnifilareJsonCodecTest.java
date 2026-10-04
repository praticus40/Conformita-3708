package it.frank.conformita.core.unifilare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class UnifilareJsonCodecTest {

    @Test
    void roundTripResidentialTemplate() throws IOException {
        UnifilareDocument original = UnifilareTemplates.residentialDefault();
        String json = UnifilareJsonCodec.writeString(original);
        UnifilareDocument parsed = UnifilareJsonCodec.readString(json);
        assertEquals(original.getGeneral().getInAmps(), parsed.getGeneral().getInAmps());
        assertEquals(5, parsed.getCircuits().size());
        UnifilareValidator.validateOrThrow(parsed);
    }

    @Test
    void exampleResourceParses() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/schema/conformita-unifilare-v1.example.json")) {
            assertTrue(in != null);
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            UnifilareDocument doc = UnifilareJsonCodec.readString(json);
            UnifilareValidator.validateOrThrow(doc);
        }
    }
}
