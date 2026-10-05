package it.frank.conformita.gef.editor;



import it.frank.conformita.core.unifilare.schema.SchemaLibraryFileStorage;

import it.frank.conformita.core.unifilare.schema.SchemaLibraryPaths;

import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;

import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import it.frank.conformita.core.unifilare.schema.editor.SchemaEditorCliArgs;

import java.io.IOException;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.SashForm;

import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.layout.GridData;

import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.widgets.Menu;

import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.swt.widgets.Shell;



public final class SchemaEditorShell {



    private final Shell shell;

    private final SchemaEditorCliArgs cli;

    private final SchemaLibraryFileStorage storage;

    private final SchemaDiagramRoot root;

    private final SchemaDraw2dEditor canvas;

    private final SchemaOutlineTree outlineTree;

    private final SchemaPropertiesPanel propertiesPanel;



    private boolean savedOnExit;

    private int exitCode = 1;

    private boolean syncingSelection;

    private Consumer<String> addSymbolFromToolbar = type -> {};



    public SchemaEditorShell(Display display, SchemaEditorCliArgs cli) {

        this.cli = cli;

        this.storage = new SchemaLibraryFileStorage(cli.dataDir());

        this.shell = new Shell(display, SWT.SHELL_TRIM);

        this.root = new SchemaDiagramRoot(loadDocument());

        shell.setText("Editor schema — " + cli.schemaId());

        shell.setBounds(cli.x(), cli.y(), cli.width(), cli.height());

        shell.setLayout(new GridLayout(1, false));



        try {

            storage.acquireLock(cli.schemaId(), "schema-editor-gef");

        } catch (IOException e) {

            throw new IllegalStateException("Impossibile acquisire lock schema", e);

        }



        createFileMenu();



        SchemaSymbolToolbar.create(shell, type -> addSymbolFromToolbar.accept(type));



        SashForm sash = new SashForm(shell, SWT.HORIZONTAL);

        sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));



        Composite center = new Composite(sash, SWT.NONE);

        center.setLayout(new FillLayout());

        canvas = new SchemaDraw2dEditor(center, root);



        Composite rightColumn = new Composite(sash, SWT.NONE);

        rightColumn.setLayout(new FillLayout());

        SashForm rightSash = new SashForm(rightColumn, SWT.VERTICAL);

        outlineTree = new SchemaOutlineTree(rightSash);

        propertiesPanel = new SchemaPropertiesPanel(rightSash);

        rightSash.setWeights(new int[] {65, 35});



        sash.setWeights(new int[] {78, 22});



        outlineTree.setInput(root);

        addSymbolFromToolbar = type -> {
            if (!canvas.addNode(type)) {
                String reason = canvas.addBlockedReason(type);
                MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
                box.setText("Simbolo");
                box.setMessage(reason != null ? reason : "Impossibile aggiungere il simbolo.");
                box.open();
                return;
            }
            outlineTree.refresh();
        };

        wireSelectionSync();



        shell.addListener(SWT.Close, e -> {

            if (!confirmClose()) {

                e.doit = false;

                return;

            }

            closeShell(savedOnExit);

        });

    }



    private void createFileMenu() {

        Menu menuBar = new Menu(shell, SWT.BAR);

        shell.setMenuBar(menuBar);



        MenuItem fileRoot = new MenuItem(menuBar, SWT.CASCADE);

        fileRoot.setText("&File");

        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);

        fileRoot.setMenu(fileMenu);



        MenuItem saveItem = new MenuItem(fileMenu, SWT.PUSH);

        saveItem.setText("&Salva\tCtrl+S");

        saveItem.setAccelerator(SWT.CTRL | 'S');

        saveItem.addListener(SWT.Selection, e -> saveAndRender());



        new MenuItem(fileMenu, SWT.SEPARATOR);



        MenuItem closeItem = new MenuItem(fileMenu, SWT.PUSH);

        closeItem.setText("&Chiudi\tAlt+F4");

        closeItem.setAccelerator(SWT.ALT | SWT.F4);

        closeItem.addListener(SWT.Selection, e -> {

            if (confirmClose()) {

                closeShell(savedOnExit);

            }

        });

    }



    /** @return false if the user cancelled close */

    private boolean confirmClose() {

        if (savedOnExit) {

            return true;

        }

        MessageBox box = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);

        box.setText("Chiudi editor");

        box.setMessage("Salvare le modifiche prima di chiudere?");

        int answer = box.open();

        if (answer == SWT.CANCEL) {

            return false;

        }

        if (answer == SWT.YES) {

            saveAndRender();

        }

        return true;

    }



    private void wireSelectionSync() {

        canvas.setSelectionListener(nodes -> {

            if (syncingSelection) {

                return;

            }

            syncingSelection = true;

            try {

                outlineTree.selectNode(nodes.isEmpty() ? null : nodes.get(0));

                propertiesPanel.bindSelection(nodes);

            } finally {

                syncingSelection = false;

            }

        });

        outlineTree.setSelectionListener(node -> {

            if (syncingSelection) {

                return;

            }

            syncingSelection = true;

            try {

                canvas.selectNodeFromOutline(node);

            } finally {

                syncingSelection = false;

            }

        });

        propertiesPanel.setChangeListener(node -> {

            canvas.refreshNode(node);

            outlineTree.refresh();

        });

        propertiesPanel.bindSelection(canvas.selection());

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

            MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);

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

        shell.dispose();

    }



    public boolean isDisposed() {

        return shell.isDisposed();

    }



    public int exitCode() {

        return exitCode;

    }



    public void openBlocking() {

        shell.open();

    }

}


