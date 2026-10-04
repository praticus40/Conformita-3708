package it.frank.conformita.core.dto;

public record VerificaRigaDto(
        Long id, String tipoProva, String valore, String marcaStrumento, String modelloStrumento) {

    public static VerificaRigaDto empty() {
        return new VerificaRigaDto(null, "", null, null, null);
    }
}
