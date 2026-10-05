package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public final class PaletteIconFactory {

    public static final int SIZE_MEDIUM = 32;

    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final Map<String, Map<String, String>> ICON_PROPS = Map.of(
            "general", Map.of("inAmps", "32"),
            "circuit", Map.of("inAmps", "16", "differentialMa", "30"),
            "busbar", Map.of());

    private PaletteIconFactory() {}

    public static Image iconFor(String type, int size) {
        String key = type + "@" + size;
        return CACHE.computeIfAbsent(key, k -> render(type, size));
    }

    private static Image render(String type, int size) {
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#f0f0f0"));
        gc.fillRect(0, 0, size, size);
        gc.setStroke(Color.BLACK);
        JavaFxDrawTarget target = new JavaFxDrawTarget(gc);
        Map<String, String> props = ICON_PROPS.getOrDefault(type, Map.of());
        UnifilareSymbolGlyphs.paintNode(type, target, 1, 1, size - 2, size - 2, "", props);
        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }
}
