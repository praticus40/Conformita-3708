package it.frank.conformita.gef;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SchemaEditorCliArgsTest {

    @Test
    void parseRequiredAndOptional() {
        SchemaEditorCliArgs args = SchemaEditorCliArgs.parse(new String[] {
            "--data-dir", "C:/data",
            "--schema-id", "42",
            "--x", "10",
            "--y", "20",
            "--width", "800",
            "--height", "600"
        });
        assertEquals(42L, args.schemaId());
        assertEquals(10, args.x());
        assertEquals(20, args.y());
        assertEquals(800, args.width());
        assertEquals(600, args.height());
        assertTrue(args.exitOnClose());
    }
}
