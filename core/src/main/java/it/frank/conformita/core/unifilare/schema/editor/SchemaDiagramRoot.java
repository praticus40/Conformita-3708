package it.frank.conformita.core.unifilare.schema.editor;

import it.frank.conformita.core.unifilare.schema.GefDiagramModel;
import it.frank.conformita.core.unifilare.schema.UnifilareSchemaDocumentV2;

public final class SchemaDiagramRoot {

    private final UnifilareSchemaDocumentV2 document;

    public SchemaDiagramRoot(UnifilareSchemaDocumentV2 document) {
        this.document = document;
    }

    public UnifilareSchemaDocumentV2 document() {
        return document;
    }

    public GefDiagramModel diagram() {
        return document.getDiagram();
    }
}
