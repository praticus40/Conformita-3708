package it.frank.conformita.core.unifilare.schema;

public class UnifilareSchemaDocumentV2 {

    public static final String FORMAT_ID = "conformita-unifilare";
    public static final int FORMAT_VERSION = 2;

    private String format = FORMAT_ID;
    private int version = FORMAT_VERSION;
    private GefDiagramModel diagram = new GefDiagramModel();

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GefDiagramModel getDiagram() {
        return diagram;
    }

    public void setDiagram(GefDiagramModel diagram) {
        this.diagram = diagram != null ? diagram : new GefDiagramModel();
    }
}
