package it.frank.conformita.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "impresa")
public class Impresa {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 255)
    private String ragioneSociale = "";

    @Column(nullable = false, length = 16)
    private String partitaIva = "";

    @Column(nullable = false, length = 512)
    private String sedeLegale = "";

    @Column(length = 64)
    private String rea;

    @Column(length = 255)
    private String responsabileTecnico;

    @Column(length = 255)
    private String legaleRappresentante;

    @Column(length = 255)
    private String viaSede;

    @Column(length = 10)
    private String cap;

    @Column(length = 128)
    private String comune;

    @Column(length = 4)
    private String provincia;

    @Column(length = 32)
    private String telefono;

    @Column(length = 128)
    private String email;

    @Column(length = 64)
    private String cciaaNumero;

    @Column(length = 64)
    private String alboArtigianiNumero;

    @Column(length = 128)
    private String settore = "Impianti Elettrici";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getSedeLegale() {
        return sedeLegale;
    }

    public void setSedeLegale(String sedeLegale) {
        this.sedeLegale = sedeLegale;
    }

    public String getRea() {
        return rea;
    }

    public void setRea(String rea) {
        this.rea = rea;
    }

    public String getResponsabileTecnico() {
        return responsabileTecnico;
    }

    public void setResponsabileTecnico(String responsabileTecnico) {
        this.responsabileTecnico = responsabileTecnico;
    }

    public String getLegaleRappresentante() {
        return legaleRappresentante;
    }

    public void setLegaleRappresentante(String legaleRappresentante) {
        this.legaleRappresentante = legaleRappresentante;
    }

    public String getViaSede() {
        return viaSede;
    }

    public void setViaSede(String viaSede) {
        this.viaSede = viaSede;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCciaaNumero() {
        return cciaaNumero;
    }

    public void setCciaaNumero(String cciaaNumero) {
        this.cciaaNumero = cciaaNumero;
    }

    public String getAlboArtigianiNumero() {
        return alboArtigianiNumero;
    }

    public void setAlboArtigianiNumero(String alboArtigianiNumero) {
        this.alboArtigianiNumero = alboArtigianiNumero;
    }

    public String getSettore() {
        return settore;
    }

    public void setSettore(String settore) {
        this.settore = settore;
    }
}
