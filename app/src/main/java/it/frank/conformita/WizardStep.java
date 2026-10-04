package it.frank.conformita;

public enum WizardStep {
    IMPRESA(1, "Anagrafica impresa"),
    COMMITTENTE(2, "Committente e ubicazione"),
    INTERVENTO(3, "Intervento e impianto"),
    DICHIARAZIONE(4, "Dichiarazione Di.Co"),
    RELAZIONE_TECNICA(5, "Relazione tecnica"),
    MATERIALI(6, "Materiali"),
    VERIFICHE(7, "Verifiche e prove"),
    LIBRETTO_ALLEGATI(8, "Libretto e schemi"),
    RIEPILOGO(9, "Riepilogo ed export");

    private final int number;
    private final String title;

    WizardStep(int number, String title) {
        this.number = number;
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String shortLabel() {
        return number + ". " + title;
    }
}
