package it.frank.conformita.gef.editor;

import java.util.function.Consumer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public final class SchemaSymbolToolbar {

    private static final String[][] ENTRIES = {
        {"Generale", "general"},
        {"Linea", "circuit"},
        {"Sbarra collettore", "busbar"},
    };

    private SchemaSymbolToolbar() {}

    public static Composite create(Composite parent, Consumer<String> onAddType) {
        Composite row = new Composite(parent, SWT.NONE);
        row.setLayout(new GridLayout(2, false));
        row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label heading = new Label(row, SWT.NONE);
        heading.setText("Simboli");

        ToolBar toolbar = new ToolBar(row, SWT.FLAT | SWT.WRAP);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Display display = parent.getDisplay();
        for (String[] entry : ENTRIES) {
            String title = entry[0];
            String type = entry[1];
            ToolItem item = new ToolItem(toolbar, SWT.PUSH);
            Image icon = PaletteIconFactory.iconFor(display, type, PaletteIconFactory.SIZE_MEDIUM);
            item.setImage(icon);
            item.setToolTipText(title);
            item.addListener(SWT.Selection, e -> onAddType.accept(type));
        }
        return row;
    }
}
