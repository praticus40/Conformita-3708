package it.frank.conformita.core.unifilare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnifilareDocument {

    public static final String FORMAT_ID = "conformita-unifilare";
    public static final int FORMAT_VERSION = 1;

    private String format = FORMAT_ID;
    private int version = FORMAT_VERSION;
    private Layout layout = new Layout();
    private GeneralSwitch general = new GeneralSwitch();
    private Busbar busbar = new Busbar();
    private List<Circuit> circuits = new ArrayList<>();

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

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout != null ? layout : new Layout();
    }

    public GeneralSwitch getGeneral() {
        return general;
    }

    public void setGeneral(GeneralSwitch general) {
        this.general = general != null ? general : new GeneralSwitch();
    }

    public Busbar getBusbar() {
        return busbar;
    }

    public void setBusbar(Busbar busbar) {
        this.busbar = busbar != null ? busbar : new Busbar();
    }

    public List<Circuit> getCircuits() {
        return circuits;
    }

    public void setCircuits(List<Circuit> circuits) {
        this.circuits = circuits != null ? new ArrayList<>(circuits) : new ArrayList<>();
    }

    public static class Layout {
        private double width = 800;
        private double height = 520;

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    public static class GeneralSwitch {
        private String label = "Generale";
        private int inAmps = 32;
        private double x = 400;
        private double y = 40;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getInAmps() {
            return inAmps;
        }

        public void setInAmps(int inAmps) {
            this.inAmps = inAmps;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public static class Busbar {
        private double y = 120;
        private double x1 = 80;
        private double x2 = 720;

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getX1() {
            return x1;
        }

        public void setX1(double x1) {
            this.x1 = x1;
        }

        public double getX2() {
            return x2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }
    }

    public static class Circuit {
        private String id;
        private String label;
        private int inAmps;
        private Integer differentialMa;
        private double anchorX;
        private int order;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getInAmps() {
            return inAmps;
        }

        public void setInAmps(int inAmps) {
            this.inAmps = inAmps;
        }

        public Integer getDifferentialMa() {
            return differentialMa;
        }

        public void setDifferentialMa(Integer differentialMa) {
            this.differentialMa = differentialMa;
        }

        public double getAnchorX() {
            return anchorX;
        }

        public void setAnchorX(double anchorX) {
            this.anchorX = anchorX;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}
