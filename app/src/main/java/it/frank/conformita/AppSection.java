package it.frank.conformita;

public enum AppSection {
    PRATICHE("Pratiche"),
    SCHEMI("Schemi"),
    NUOVA_PRATICA("Nuova pratica"),
    IMPRESA("Impresa"),
    IMPOSTAZIONI("Impostazioni");

    private final String label;

    AppSection(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
