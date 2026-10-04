package it.frank.conformita.ui.geo;

public record ProvinciaItem(String sigla, String nome) {

    public String label() {
        return sigla + " — " + nome;
    }

    @Override
    public String toString() {
        return label();
    }
}
