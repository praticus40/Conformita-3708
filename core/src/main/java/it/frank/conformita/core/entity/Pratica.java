package it.frank.conformita.core.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.annotations.ColumnDefault;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pratica")
public class Pratica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StatoPratica stato = StatoPratica.BOZZA;

    @Column(length = 128)
    private String comuneImpianto;

    @Column(length = 4)
    private String provincia;

    @Column(length = 64)
    private String protocollo;

    private LocalDate dataProtocollo;

    @Lob
    private String descrizioneImpianto;

    @Column(length = 255)
    private String committenteNome = "";

    @Column(length = 16)
    private String committenteCodiceFiscale;

    @Column(length = 512)
    private String indirizzoImpianto = "";

    @Column(length = 512)
    private String indirizzoCommittente;

    @Column(length = 512)
    private String proprietarioDescrizione;

    @Column(length = 128)
    private String comune = "";

    @Column(length = 10)
    private String cap;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TipoIntervento tipoIntervento;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private DestinazioneUso destinazioneUso;

    @Column(length = 128)
    private String altriUsi;

    private LocalDate dataInizio;

    private Double potenzaKw;

    private Double potenzaImpegnabileKw;

    private Double potenzaInstallataKw;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean fotovoltaico;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean altroIntervento;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private TensioneImpianto tensione;

    @Column(length = 64)
    private String tensioneNominale;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private SistemaDistribuzione sistemaDistribuzione;

    @Column(length = 255)
    private String normaTecnica = "CEI 64-8";

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean dichiaraProgetto;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean dichiaraNorma;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean dichiaraMateriali;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean dichiaraControlli;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean allegatoProgetto;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean allegatoMateriali;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean allegatoSchema;

    @Column(length = 512)
    private String riferimentiPrecedenti;

    private LocalDate dataDichiarazione;

    @Column(length = 255)
    private String responsabileTecnicoNome;

    @Lob
    private String descrizioneOpere;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean provaVistaSezioni;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean provaVistaBagno;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean provaVistaInterruttori;

    @Column(length = 32)
    private String telefonoAssistenza;

    @Column(length = 1024)
    private String schemaAllegatoPath;

    @Column(length = 512)
    private String schemaUnifilareJsonPath;

    private Long unifilareSchemaId;

    @OneToMany(mappedBy = "pratica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<MaterialeRiga> materiali = new ArrayList<>();

    @OneToMany(mappedBy = "pratica", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<VerificaRiga> verifiche = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StatoPratica getStato() {
        return stato;
    }

    public void setStato(StatoPratica stato) {
        this.stato = stato;
    }

    public String getComuneImpianto() {
        return comuneImpianto;
    }

    public void setComuneImpianto(String comuneImpianto) {
        this.comuneImpianto = comuneImpianto;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getProtocollo() {
        return protocollo;
    }

    public void setProtocollo(String protocollo) {
        this.protocollo = protocollo;
    }

    public LocalDate getDataProtocollo() {
        return dataProtocollo;
    }

    public void setDataProtocollo(LocalDate dataProtocollo) {
        this.dataProtocollo = dataProtocollo;
    }

    public String getDescrizioneImpianto() {
        return descrizioneImpianto;
    }

    public void setDescrizioneImpianto(String descrizioneImpianto) {
        this.descrizioneImpianto = descrizioneImpianto;
    }

    public String getCommittenteNome() {
        return committenteNome;
    }

    public void setCommittenteNome(String committenteNome) {
        this.committenteNome = committenteNome;
    }

    public String getCommittenteCodiceFiscale() {
        return committenteCodiceFiscale;
    }

    public void setCommittenteCodiceFiscale(String committenteCodiceFiscale) {
        this.committenteCodiceFiscale = committenteCodiceFiscale;
    }

    public String getIndirizzoImpianto() {
        return indirizzoImpianto;
    }

    public void setIndirizzoImpianto(String indirizzoImpianto) {
        this.indirizzoImpianto = indirizzoImpianto;
    }

    public String getIndirizzoCommittente() {
        return indirizzoCommittente;
    }

    public void setIndirizzoCommittente(String indirizzoCommittente) {
        this.indirizzoCommittente = indirizzoCommittente;
    }

    public String getProprietarioDescrizione() {
        return proprietarioDescrizione;
    }

    public void setProprietarioDescrizione(String proprietarioDescrizione) {
        this.proprietarioDescrizione = proprietarioDescrizione;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public TipoIntervento getTipoIntervento() {
        return tipoIntervento;
    }

    public void setTipoIntervento(TipoIntervento tipoIntervento) {
        this.tipoIntervento = tipoIntervento;
    }

    public DestinazioneUso getDestinazioneUso() {
        return destinazioneUso;
    }

    public void setDestinazioneUso(DestinazioneUso destinazioneUso) {
        this.destinazioneUso = destinazioneUso;
    }

    public String getAltriUsi() {
        return altriUsi;
    }

    public void setAltriUsi(String altriUsi) {
        this.altriUsi = altriUsi;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Double getPotenzaKw() {
        return potenzaKw;
    }

    public void setPotenzaKw(Double potenzaKw) {
        this.potenzaKw = potenzaKw;
    }

    public Double getPotenzaImpegnabileKw() {
        return potenzaImpegnabileKw;
    }

    public void setPotenzaImpegnabileKw(Double potenzaImpegnabileKw) {
        this.potenzaImpegnabileKw = potenzaImpegnabileKw;
    }

    public Double getPotenzaInstallataKw() {
        return potenzaInstallataKw;
    }

    public void setPotenzaInstallataKw(Double potenzaInstallataKw) {
        this.potenzaInstallataKw = potenzaInstallataKw;
    }

    public boolean isFotovoltaico() {
        return fotovoltaico;
    }

    public void setFotovoltaico(boolean fotovoltaico) {
        this.fotovoltaico = fotovoltaico;
    }

    public boolean isAltroIntervento() {
        return altroIntervento;
    }

    public void setAltroIntervento(boolean altroIntervento) {
        this.altroIntervento = altroIntervento;
    }

    public TensioneImpianto getTensione() {
        return tensione;
    }

    public void setTensione(TensioneImpianto tensione) {
        this.tensione = tensione;
    }

    public String getTensioneNominale() {
        return tensioneNominale;
    }

    public void setTensioneNominale(String tensioneNominale) {
        this.tensioneNominale = tensioneNominale;
    }

    public SistemaDistribuzione getSistemaDistribuzione() {
        return sistemaDistribuzione;
    }

    public void setSistemaDistribuzione(SistemaDistribuzione sistemaDistribuzione) {
        this.sistemaDistribuzione = sistemaDistribuzione;
    }

    public String getNormaTecnica() {
        return normaTecnica;
    }

    public void setNormaTecnica(String normaTecnica) {
        this.normaTecnica = normaTecnica;
    }

    public boolean isDichiaraProgetto() {
        return dichiaraProgetto;
    }

    public void setDichiaraProgetto(boolean dichiaraProgetto) {
        this.dichiaraProgetto = dichiaraProgetto;
    }

    public boolean isDichiaraNorma() {
        return dichiaraNorma;
    }

    public void setDichiaraNorma(boolean dichiaraNorma) {
        this.dichiaraNorma = dichiaraNorma;
    }

    public boolean isDichiaraMateriali() {
        return dichiaraMateriali;
    }

    public void setDichiaraMateriali(boolean dichiaraMateriali) {
        this.dichiaraMateriali = dichiaraMateriali;
    }

    public boolean isDichiaraControlli() {
        return dichiaraControlli;
    }

    public void setDichiaraControlli(boolean dichiaraControlli) {
        this.dichiaraControlli = dichiaraControlli;
    }

    public boolean isAllegatoProgetto() {
        return allegatoProgetto;
    }

    public void setAllegatoProgetto(boolean allegatoProgetto) {
        this.allegatoProgetto = allegatoProgetto;
    }

    public boolean isAllegatoMateriali() {
        return allegatoMateriali;
    }

    public void setAllegatoMateriali(boolean allegatoMateriali) {
        this.allegatoMateriali = allegatoMateriali;
    }

    public boolean isAllegatoSchema() {
        return allegatoSchema;
    }

    public void setAllegatoSchema(boolean allegatoSchema) {
        this.allegatoSchema = allegatoSchema;
    }

    public String getRiferimentiPrecedenti() {
        return riferimentiPrecedenti;
    }

    public void setRiferimentiPrecedenti(String riferimentiPrecedenti) {
        this.riferimentiPrecedenti = riferimentiPrecedenti;
    }

    public LocalDate getDataDichiarazione() {
        return dataDichiarazione;
    }

    public void setDataDichiarazione(LocalDate dataDichiarazione) {
        this.dataDichiarazione = dataDichiarazione;
    }

    public String getResponsabileTecnicoNome() {
        return responsabileTecnicoNome;
    }

    public void setResponsabileTecnicoNome(String responsabileTecnicoNome) {
        this.responsabileTecnicoNome = responsabileTecnicoNome;
    }

    public String getDescrizioneOpere() {
        return descrizioneOpere;
    }

    public void setDescrizioneOpere(String descrizioneOpere) {
        this.descrizioneOpere = descrizioneOpere;
    }

    public boolean isProvaVistaSezioni() {
        return provaVistaSezioni;
    }

    public void setProvaVistaSezioni(boolean provaVistaSezioni) {
        this.provaVistaSezioni = provaVistaSezioni;
    }

    public boolean isProvaVistaBagno() {
        return provaVistaBagno;
    }

    public void setProvaVistaBagno(boolean provaVistaBagno) {
        this.provaVistaBagno = provaVistaBagno;
    }

    public boolean isProvaVistaInterruttori() {
        return provaVistaInterruttori;
    }

    public void setProvaVistaInterruttori(boolean provaVistaInterruttori) {
        this.provaVistaInterruttori = provaVistaInterruttori;
    }

    public String getTelefonoAssistenza() {
        return telefonoAssistenza;
    }

    public void setTelefonoAssistenza(String telefonoAssistenza) {
        this.telefonoAssistenza = telefonoAssistenza;
    }

    public String getSchemaAllegatoPath() {
        return schemaAllegatoPath;
    }

    public void setSchemaAllegatoPath(String schemaAllegatoPath) {
        this.schemaAllegatoPath = schemaAllegatoPath;
    }

    public String getSchemaUnifilareJsonPath() {
        return schemaUnifilareJsonPath;
    }

    public void setSchemaUnifilareJsonPath(String schemaUnifilareJsonPath) {
        this.schemaUnifilareJsonPath = schemaUnifilareJsonPath;
    }

    public Long getUnifilareSchemaId() {
        return unifilareSchemaId;
    }

    public void setUnifilareSchemaId(Long unifilareSchemaId) {
        this.unifilareSchemaId = unifilareSchemaId;
    }

    public List<MaterialeRiga> getMateriali() {
        return materiali;
    }

    public void setMateriali(List<MaterialeRiga> materiali) {
        this.materiali = materiali;
    }

    public List<VerificaRiga> getVerifiche() {
        return verifiche;
    }

    public void setVerifiche(List<VerificaRiga> verifiche) {
        this.verifiche = verifiche;
    }
}
