package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.SchemaLibraryFileStorage;
import it.frank.conformita.core.unifilare.schema.SchemaLibraryPaths;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;
import it.frank.conformita.gef.SchemaEditorCliArgs;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public final class SchemaEditorShell extends Shell {

    private final SchemaEditorCliArgs cli;
    private final SchemaLibraryFileStorage storage;
    private final SchemaDiagramRoot root;
    private final SchemaDraw2dEditor canvas;
    private final SchemaOutlineTree outlineTree;

    private boolean savedOnExit;
    private int exitCode = 1;

    public SchemaEditorShell(Display display, SchemaEditorCliArgs cli) {
        super(display, SWT.SHELL_TRIM);
        this.cli = cli;
        this.storage = new SchemaLibraryFileStorage(cli.dataDir());
        this.root = new SchemaDiagramRoot(loadDocument());
        setText("Editor schema — conformita-3708");
        setBounds(cli.x(), cli.y(), cli.width(), cli.height());
        setLayout(new GridLayout(1, false));

        try {
            storage.acquireLock(cli.schemaId(), "schema-editor-gef");
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile acquisire lock schema", e);
        }

        SashForm sash = new SashForm(this, SWT.HORIZONTAL);
        sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite left = new Composite(sash, SWT.NONE);
        left.setLayout(new GridLayout(1, false));
        Label paletteLabel = new Label(left, SWT.NONE);
        paletteLabel.setText("Simboli (Draw2d)");
        addPaletteButton(left, "Quadro", "quadro", "Quadro");
        addPaletteButton(left, "Interruttore", "interruttore", "Interruttore");
        addPaletteButton(left, "Presa", "presa", "Presa");
        addPaletteButton(left, "Lampada", "lampada", "Luce");

        Composite right = new Composite(sash, SWT.NONE);
        right.setLayout(new GridLayout(1, false));
        outlineTree = new SchemaOutlineTree(right);
        outlineTree.control().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        outlineTree.setInput(root);

        Composite center = new Composite(sash, SWT.NONE);
        center.setLayout(new FillLayout());
        canvas = new SchemaDraw2dEditor(center, root);
        canvas.setSelectionListener(nodes -> outlineTree.selectNode(nodes.isEmpty() ? null : nodes.get(0)));

        sash.setWeights(new int[] {18, 62, 20});

        Composite bottom = new Composite(this, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(3, false));
        Button saveButton = new Button(bottom, SWT.PUSH);
        saveButton.setText("Salva");
        saveButton.addListener(SWT.Selection, e -> saveAndRender());
        Button closeButton = new Button(bottom, SWT.PUSH);
        closeButton.setText("Chiudi");
        closeButton.addListener(SWT.Selection, e -> closeShell(savedOnExit));

        addListener(SWT.Close, e -> {
            if (!savedOnExit) {
                MessageBox box = new MessageBox(this, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                box.setText("Chiudi editor");
                box.setMessage("Salvare le modifiche prima di chiudere?");
                int answer = box.open();
                if (answer == SWT.CANCEL) {
                    e.doit = false;
                    return;
                }
                if (answer == SWT.YES) {
                    saveAndRender();
                }
            }
            closeShell(savedOnExit);
        });
    }

    private void addPaletteButton(Composite parent, String text, String type, String label) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(text);
        button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        button.addListener(SWT.Selection, e -> {
            canvas.addNode(type, label);
            outlineTree.refresh();
        });
    }

    private UnifilareSchemaDocumentV2 loadDocument() {
        try {
            String rel = SchemaLibraryPaths.documentRelativePath(cli.schemaId());
            return storage.loadDocument(rel);
        } catch (IOException e) {
            throw new IllegalStateException("Caricamento schema " + cli.schemaId(), e);
        }
    }

    private void saveAndRender() {
        try {
            String rel = SchemaLibraryPaths.documentRelativePath(cli.schemaId());
            storage.saveDocument(rel, root.document());
            storage.renderPdf(cli.schemaId(), root.document());
            savedOnExit = true;
            exitCode = 0;
            outlineTree.refresh();
        } catch (IOException e) {
            MessageBox box = new MessageBox(this, SWT.ICON_ERROR);
            box.setText("Salvataggio");
            box.setMessage("Salvataggio fallito: " + e.getMessage());
            box.open();
        }
    }

    private void closeShell(boolean saved) {
        exitCode = saved ? 0 : 1;
        try {
            storage.releaseLock(cli.schemaId());
        } catch (IOException ignored) {
            // best effort
        }
        dispose();
    }

    public int exitCode() {
        return exitCode;
    }

    public void openBlocking() {
        super.open();
    }
}
