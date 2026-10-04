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
@Table(name = "verifica_riga")
public class VerificaRiga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pratica_id", nullable = false)
    private Pratica pratica;

    @Column(nullable = false, length = 512)
    private String tipoProva = "";

    @Column(length = 128)
    private String valore;

    @Column(length = 128)
    private String marcaStrumento;

    @Column(length = 128)
    private String modelloStrumento;

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

    public String getTipoProva() {
        return tipoProva;
    }

    public void setTipoProva(String tipoProva) {
        this.tipoProva = tipoProva;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public String getMarcaStrumento() {
        return marcaStrumento;
    }

    public void setMarcaStrumento(String marcaStrumento) {
        this.marcaStrumento = marcaStrumento;
    }

    public String getModelloStrumento() {
        return modelloStrumento;
    }

    public void setModelloStrumento(String modelloStrumento) {
        this.modelloStrumento = modelloStrumento;
    }
}
