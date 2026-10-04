package it.frank.conformita.core.dto;

public record ImpresaDto(
        String ragioneSociale,
        String partitaIva,
        String sedeLegale,
        String rea,
        String responsabileTecnico,
        String legaleRappresentante,
        String viaSede,
        String cap,
        String comune,
        String provincia,
        String telefono,
        String email,
        String cciaaNumero,
        String alboArtigianiNumero,
        String settore) {

    public static ImpresaDto empty() {
        return new ImpresaDto("", "", "", null, null, null, null, null, null, null, null, null, null, null, "Impianti Elettrici");
    }
}
