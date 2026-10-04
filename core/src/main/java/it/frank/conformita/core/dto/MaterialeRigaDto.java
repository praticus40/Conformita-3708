package it.frank.conformita.core.dto;

public record MaterialeRigaDto(
        Long id, String descrizione, String marchio, String modello, String posa, String siglaRif) {

    public static MaterialeRigaDto empty() {
        return new MaterialeRigaDto(null, "", null, null, null, null);
    }
}
