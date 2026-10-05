package it.frank.conformita.gef.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public final class SchemaOutlineTree {

    private final TreeViewer treeViewer;
    private SchemaDiagramRoot root;
    private Consumer<GefDiagramNode> selectionListener = node -> {};
    private boolean suppressSelectionEvents;

    public SchemaOutlineTree(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Struttura");
        group.setLayout(new FillLayout());
        treeViewer = new TreeViewer(group, SWT.BORDER | SWT.SINGLE);
        treeViewer.setContentProvider(new ITreeContentProvider() {
            @Override
            public Object[] getElements(Object input) {
                if (!(input instanceof SchemaDiagramRoot diagramRoot)) {
                    return new Object[0];
                }
                return roots(diagramRoot).toArray();
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (!(parentElement instanceof GefDiagramNode parent) || root == null) {
                    return new Object[0];
                }
                return childrenOf(parent.getId()).toArray();
            }

            @Override
            public Object getParent(Object element) {
                if (!(element instanceof GefDiagramNode node) || root == null) {
                    return null;
                }
                String parentId = node.getParentId();
                if (parentId == null || parentId.isBlank()) {
                    return root;
                }
                return root.diagram().getNodes().stream()
                        .filter(n -> parentId.equals(n.getId()))
                        .findFirst()
                        .orElse(null);
            }

            @Override
            public boolean hasChildren(Object element) {
                if (!(element instanceof GefDiagramNode parent)) {
                    return false;
                }
                return !childrenOf(parent.getId()).isEmpty();
            }

            private List<GefDiagramNode> childrenOf(String parentId) {
                List<GefDiagramNode> out = new ArrayList<>();
                if (root == null || parentId == null) {
                    return out;
                }
                for (GefDiagramNode n : root.diagram().getNodes()) {
                    if (parentId.equals(n.getParentId())) {
                        out.add(n);
                    }
                }
                return out;
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
        treeViewer.addSelectionChangedListener((ISelectionChangedListener) this::onSelectionChanged);
        treeViewer.addDoubleClickListener((IDoubleClickListener) this::onDoubleClick);
    }

    private List<GefDiagramNode> roots(SchemaDiagramRoot diagramRoot) {
        List<GefDiagramNode> nodes = diagramRoot.diagram().getNodes();
        Set<String> ids = new HashSet<>();
        for (GefDiagramNode n : nodes) {
            ids.add(n.getId());
        }
        List<GefDiagramNode> roots = new ArrayList<>();
        for (GefDiagramNode n : nodes) {
            String pid = n.getParentId();
            if ("sheet".equals(n.getType()) || pid == null || pid.isBlank() || !ids.contains(pid)) {
                roots.add(n);
            }
        }
        return roots;
    }

    private void onSelectionChanged(SelectionChangedEvent event) {
        if (suppressSelectionEvents) {
            return;
        }
        Object first = treeViewer.getStructuredSelection().getFirstElement();
        if (first instanceof GefDiagramNode node) {
            selectionListener.accept(node);
        }
    }

    private void onDoubleClick(DoubleClickEvent event) {
        Object first = treeViewer.getStructuredSelection().getFirstElement();
        if (first instanceof GefDiagramNode node) {
            selectionListener.accept(node);
        }
    }

    public Composite control() {
        return treeViewer.getTree().getParent();
    }

    public void setInput(SchemaDiagramRoot root) {
        this.root = root;
        treeViewer.setInput(root);
    }

    public void setSelectionListener(Consumer<GefDiagramNode> listener) {
        this.selectionListener = listener != null ? listener : node -> {};
    }

    public void refresh() {
        if (root != null) {
            treeViewer.refresh();
        }
    }

    public void selectNode(GefDiagramNode node) {
        suppressSelectionEvents = true;
        try {
            if (node == null) {
                treeViewer.setSelection(StructuredSelection.EMPTY);
            } else {
                treeViewer.setSelection(new StructuredSelection(node), true);
            }
        } finally {
            suppressSelectionEvents = false;
        }
    }
}
