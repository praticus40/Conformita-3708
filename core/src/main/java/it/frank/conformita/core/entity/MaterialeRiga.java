package it.frank.conformita.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "materiale_riga")
public class MaterialeRiga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pratica_id", nullable = false)
    private Pratica pratica;

    @Column(nullable = false, length = 512)
    private String descrizione = "";

    @Column(length = 128)
    private String marchio;

    @Column(length = 128)
    private String modello;

    @Column(length = 128)
    private String posa;

    @Column(length = 128)
    private String siglaRif;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pratica getPratica() {
        return pratica;
    }

    public void setPratica(Pratica pratica) {
        this.pratica = pratica;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getMarchio() {
        return marchio;
    }

    public void setMarchio(String marchio) {
        this.marchio = marchio;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public String getPosa() {
        return posa;
    }

    public void setPosa(String posa) {
        this.posa = posa;
    }

    public String getSiglaRif() {
        return siglaRif;
    }

    public void setSiglaRif(String siglaRif) {
        this.siglaRif = siglaRif;
    }
}
