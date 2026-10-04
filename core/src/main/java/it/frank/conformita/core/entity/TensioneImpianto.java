package it.frank.conformita.core.entity;

public enum TensioneImpianto {
    V230("230V"),
    V400("400V");

    private final String label;

    TensioneImpianto(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
