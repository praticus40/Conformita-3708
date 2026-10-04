package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public final class SchemaOutlineTree {

    private final TreeViewer treeViewer;
    private SchemaDiagramRoot root;

    public SchemaOutlineTree(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Struttura");
        group.setLayout(new FillLayout());
        treeViewer = new TreeViewer(group, SWT.BORDER | SWT.SINGLE);
        treeViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements(Object input) {
                if (input instanceof SchemaDiagramRoot diagramRoot) {
                    List<GefDiagramNode> nodes = diagramRoot.diagram().getNodes();
                    return nodes.toArray();
                }
                return new Object[0];
            }
        });
        treeViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof GefDiagramNode node) {
                    String label = node.getLabel();
                    if (label == null || label.isBlank()) {
                        return node.getType();
                    }
                    return label + " (" + node.getType() + ")";
                }
                return super.getText(element);
            }
        });
    }

    public Composite control() {
        return treeViewer.getTree().getParent();
    }

    public void setInput(SchemaDiagramRoot root) {
        this.root = root;
        treeViewer.setInput(root);
    }

    public void refresh() {
        if (root != null) {
            treeViewer.refresh();
        }
    }

    public void selectNode(GefDiagramNode node) {
        if (node == null) {
            treeViewer.setSelection(StructuredSelection.EMPTY);
        } else {
            treeViewer.setSelection(new StructuredSelection(node), true);
        }
    }
}
