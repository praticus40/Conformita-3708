package it.frank.conformita.javafx.schema;

import it.frank.conformita.core.unifilare.schema.GefDiagramNode;
import it.frank.conformita.core.unifilare.schema.editor.SchemaDiagramRoot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

final class SchemaOutlinePane extends TreeView<GefDiagramNode> {

    private SchemaDiagramRoot root;
    private Consumer<GefDiagramNode> selectionListener = n -> {};
    private boolean suppressSelectionEvents;

    SchemaOutlinePane() {
        setShowRoot(false);
        getSelectionModel().selectedItemProperty().addListener((o, old, item) -> {
            if (suppressSelectionEvents || item == null) {
                return;
            }
            GefDiagramNode node = item.getValue();
            if (node != null) {
                selectionListener.accept(node);
            }
        });
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                TreeItem<GefDiagramNode> item = getSelectionModel().getSelectedItem();
                if (item != null && item.getValue() != null) {
                    selectionListener.accept(item.getValue());
                }
            }
        });
    }

    void setRootModel(SchemaDiagramRoot root) {
        this.root = root;
        refreshTree();
    }

    void setSelectionListener(Consumer<GefDiagramNode> listener) {
        this.selectionListener = listener != null ? listener : n -> {};
    }

    void refreshTree() {
        if (root == null) {
            return;
        }
        TreeItem<GefDiagramNode> treeRoot = new TreeItem<>();
        for (GefDiagramNode r : roots()) {
            treeRoot.getChildren().add(buildItem(r));
        }
        setRoot(treeRoot);
        setShowRoot(false);
    }

    void selectNode(GefDiagramNode node) {
        suppressSelectionEvents = true;
        try {
            if (node == null) {
                getSelectionModel().clearSelection();
                return;
            }
            TreeItem<GefDiagramNode> item = findItem(getRoot(), node);
            if (item != null) {
                getSelectionModel().select(item);
            }
        } finally {
            suppressSelectionEvents = false;
        }
    }

    private TreeItem<GefDiagramNode> buildItem(GefDiagramNode node) {
        TreeItem<GefDiagramNode> item = new TreeItem<>(node);
        for (GefDiagramNode child : childrenOf(node.getId())) {
            item.getChildren().add(buildItem(child));
        }
        return item;
    }

    private TreeItem<GefDiagramNode> findItem(TreeItem<GefDiagramNode> parent, GefDiagramNode target) {
        if (parent == null) {
            return null;
        }
        for (TreeItem<GefDiagramNode> child : parent.getChildren()) {
            if (child.getValue() == target) {
                return child;
            }
            TreeItem<GefDiagramNode> nested = findItem(child, target);
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private List<GefDiagramNode> roots() {
        List<GefDiagramNode> nodes = root.diagram().getNodes();
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

    private List<GefDiagramNode> childrenOf(String parentId) {
        List<GefDiagramNode> out = new ArrayList<>();
        for (GefDiagramNode n : root.diagram().getNodes()) {
            if (parentId != null && parentId.equals(n.getParentId())) {
                out.add(n);
            }
        }
        return out;
    }
}
