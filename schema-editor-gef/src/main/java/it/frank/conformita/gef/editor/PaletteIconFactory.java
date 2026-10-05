package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.UnifilareSymbolGlyphs;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public final class PaletteIconFactory {

    public static final int SIZE_MEDIUM = 32;
    public static final int SIZE_LARGE = 40;

    private static final Map<String, Image> CACHE = new HashMap<>();

    private static final Map<String, Map<String, String>> ICON_PROPS = Map.of(
            "general", Map.of("inAmps", "32"),
            "circuit", Map.of("inAmps", "16", "differentialMa", "30"),
            "busbar", Map.of());

    private PaletteIconFactory() {}

    public static Image iconFor(Display display, String type) {
        return iconFor(display, type, SIZE_LARGE);
    }

    public static Image iconFor(Display display, String type, int size) {
        String key = type + "@" + size;
        return CACHE.computeIfAbsent(key, k -> renderIcon(display, type, size));
    }

    private static Image renderIcon(Display display, String type, int size) {
        Image image = new Image(display, size, size);
        GC gc = new GC(image);
        try {
            gc.setAntialias(SWT.ON);
            gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            gc.fillRectangle(0, 0, size, size);
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.setLineWidth(1);
            SwtGcDrawTarget target = new SwtGcDrawTarget(gc);
            Map<String, String> props = ICON_PROPS.getOrDefault(type, Map.of());
            UnifilareSymbolGlyphs.paintNode(type, target, 1, 1, size - 2, size - 2, "", props);
        } finally {
            gc.dispose();
        }
        return image;
    }
}
