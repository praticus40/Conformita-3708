package it.frank.conformita.core.mapper;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.dto.MaterialeRigaDto;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.dto.VerificaRigaDto;
import it.frank.conformita.core.entity.Impresa;
import it.frank.conformita.core.entity.MaterialeRiga;
import it.frank.conformita.core.entity.Pratica;
import it.frank.conformita.core.entity.VerificaRiga;
import java.util.ArrayList;
import java.util.List;

public final class EntityMapper {

    private EntityMapper() {}

    public static ImpresaDto toDto(Impresa entity) {
        if (entity == null) {
            return ImpresaDto.empty();
        }
        return new ImpresaDto(
                entity.getRagioneSociale(),
                entity.getPartitaIva(),
                entity.getSedeLegale(),
                entity.getRea(),
                entity.getResponsabileTecnico(),
                entity.getLegaleRappresentante(),
                entity.getViaSede(),
                entity.getCap(),
                entity.getComune(),
                entity.getProvincia(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getCciaaNumero(),
                entity.getAlboArtigianiNumero(),
                entity.getSettore());
    }

    public static void apply(Impresa entity, ImpresaDto dto) {
        entity.setRagioneSociale(nullToEmpty(dto.ragioneSociale()));
        entity.setPartitaIva(nullToEmpty(dto.partitaIva()));
        entity.setSedeLegale(nullToEmpty(dto.sedeLegale()));
        entity.setRea(emptyToNull(dto.rea()));
        entity.setResponsabileTecnico(emptyToNull(dto.responsabileTecnico()));
        entity.setLegaleRappresentante(emptyToNull(dto.legaleRappresentante()));
        entity.setViaSede(emptyToNull(dto.viaSede()));
        entity.setCap(emptyToNull(dto.cap()));
        entity.setComune(emptyToNull(dto.comune()));
        entity.setProvincia(emptyToNull(dto.provincia()));
        entity.setTelefono(emptyToNull(dto.telefono()));
        entity.setEmail(emptyToNull(dto.email()));
        entity.setCciaaNumero(emptyToNull(dto.cciaaNumero()));
        entity.setAlboArtigianiNumero(emptyToNull(dto.alboArtigianiNumero()));
        entity.setSettore(dto.settore() != null ? dto.settore() : "Impianti Elettrici");
    }

    public static PraticaDto toDto(Pratica entity) {
        return new PraticaDto(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getStato(),
                entity.getComuneImpianto(),
                entity.getProvincia(),
                entity.getProtocollo(),
                entity.getDataProtocollo(),
                entity.getDescrizioneImpianto(),
                entity.getCommittenteNome(),
                entity.getCommittenteCodiceFiscale(),
                entity.getIndirizzoImpianto(),
                entity.getIndirizzoCommittente(),
                entity.getProprietarioDescrizione(),
                entity.getComune(),
                entity.getCap(),
                entity.getTipoIntervento(),
                entity.getDestinazioneUso(),
                entity.getAltriUsi(),
                entity.getDataInizio(),
                entity.getPotenzaKw(),
                entity.getPotenzaImpegnabileKw(),
                entity.getPotenzaInstallataKw(),
                entity.isFotovoltaico(),
                entity.isAltroIntervento(),
                entity.getTensione(),
                entity.getTensioneNominale(),
                entity.getSistemaDistribuzione(),
                entity.getNormaTecnica(),
                entity.isDichiaraProgetto(),
                entity.isDichiaraNorma(),
                entity.isDichiaraMateriali(),
                entity.isDichiaraControlli(),
                entity.isAllegatoProgetto(),
                entity.isAllegatoMateriali(),
                entity.isAllegatoSchema(),
                entity.getRiferimentiPrecedenti(),
                entity.getDataDichiarazione(),
                entity.getResponsabileTecnicoNome(),
                entity.getDescrizioneOpere(),
                entity.isProvaVistaSezioni(),
                entity.isProvaVistaBagno(),
                entity.isProvaVistaInterruttori(),
                entity.getTelefonoAssistenza(),
                entity.getSchemaAllegatoPath(),
                entity.getSchemaUnifilareJsonPath(),
                entity.getUnifilareSchemaId(),
                entity.getMateriali().stream().map(EntityMapper::toDto).toList(),
                entity.getVerifiche().stream().map(EntityMapper::toDto).toList());
    }

    public static MaterialeRigaDto toDto(MaterialeRiga entity) {
        return new MaterialeRigaDto(
                entity.getId(),
                entity.getDescrizione(),
                entity.getMarchio(),
                entity.getModello(),
                entity.getPosa(),
                entity.getSiglaRif());
    }

    public static VerificaRigaDto toDto(VerificaRiga entity) {
        return new VerificaRigaDto(
                entity.getId(),
                entity.getTipoProva(),
                entity.getValore(),
                entity.getMarcaStrumento(),
                entity.getModelloStrumento());
    }

    public static PraticaSummary toSummary(Pratica entity) {
        return new PraticaSummary(
                entity.getId(),
                entity.getCommittenteNome(),
                entity.getIndirizzoImpianto(),
                entity.getTipoIntervento(),
                entity.getUpdatedAt());
    }

    public static void apply(Pratica entity, PraticaDto dto) {
        entity.setComuneImpianto(emptyToNull(dto.comuneImpianto()));
        entity.setProvincia(emptyToNull(dto.provincia()));
        entity.setProtocollo(emptyToNull(dto.protocollo()));
        entity.setDataProtocollo(dto.dataProtocollo());
        entity.setDescrizioneImpianto(emptyToNull(dto.descrizioneImpianto()));
        entity.setCommittenteNome(nullToEmpty(dto.committenteNome()));
        entity.setCommittenteCodiceFiscale(emptyToNull(dto.committenteCodiceFiscale()));
        entity.setIndirizzoImpianto(nullToEmpty(dto.indirizzoImpianto()));
        entity.setIndirizzoCommittente(emptyToNull(dto.indirizzoCommittente()));
        entity.setProprietarioDescrizione(emptyToNull(dto.proprietarioDescrizione()));
        entity.setComune(nullToEmpty(dto.comune()));
        entity.setCap(emptyToNull(dto.cap()));
        entity.setTipoIntervento(dto.tipoIntervento());
        entity.setDestinazioneUso(dto.destinazioneUso());
        entity.setAltriUsi(emptyToNull(dto.altriUsi()));
        entity.setDataInizio(dto.dataInizio());
        entity.setPotenzaKw(dto.potenzaKw());
        entity.setPotenzaImpegnabileKw(dto.potenzaImpegnabileKw());
        entity.setPotenzaInstallataKw(dto.potenzaInstallataKw());
        entity.setFotovoltaico(dto.fotovoltaico());
        entity.setAltroIntervento(dto.altroIntervento());
        entity.setTensione(dto.tensione());
        entity.setTensioneNominale(emptyToNull(dto.tensioneNominale()));
        entity.setSistemaDistribuzione(dto.sistemaDistribuzione());
        entity.setNormaTecnica(dto.normaTecnica() != null ? dto.normaTecnica() : "CEI 64-8");
        entity.setDichiaraProgetto(dto.dichiaraProgetto());
        entity.setDichiaraNorma(dto.dichiaraNorma());
        entity.setDichiaraMateriali(dto.dichiaraMateriali());
        entity.setDichiaraControlli(dto.dichiaraControlli());
        entity.setAllegatoProgetto(dto.allegatoProgetto());
        entity.setAllegatoMateriali(dto.allegatoMateriali());
        entity.setAllegatoSchema(dto.allegatoSchema());
        entity.setRiferimentiPrecedenti(emptyToNull(dto.riferimentiPrecedenti()));
        entity.setDataDichiarazione(dto.dataDichiarazione());
        entity.setResponsabileTecnicoNome(emptyToNull(dto.responsabileTecnicoNome()));
        entity.setDescrizioneOpere(emptyToNull(dto.descrizioneOpere()));
        entity.setProvaVistaSezioni(dto.provaVistaSezioni());
        entity.setProvaVistaBagno(dto.provaVistaBagno());
        entity.setProvaVistaInterruttori(dto.provaVistaInterruttori());
        entity.setTelefonoAssistenza(emptyToNull(dto.telefonoAssistenza()));
        entity.setSchemaAllegatoPath(emptyToNull(dto.schemaAllegatoPath()));
        entity.setSchemaUnifilareJsonPath(emptyToNull(dto.schemaUnifilareJsonPath()));
        entity.setUnifilareSchemaId(dto.unifilareSchemaId());
        syncMateriali(entity, dto.materiali());
        syncVerifiche(entity, dto.verifiche());
    }

    private static void syncMateriali(Pratica entity, List<MaterialeRigaDto> rows) {
        if (rows == null) {
            return;
        }
        entity.getMateriali().clear();
        for (MaterialeRigaDto row : rows) {
            if (row.descrizione() == null || row.descrizione().isBlank()) {
                continue;
            }
            MaterialeRiga materiale = new MaterialeRiga();
            materiale.setPratica(entity);
            materiale.setDescrizione(row.descrizione().trim());
            materiale.setMarchio(emptyToNull(row.marchio()));
            materiale.setModello(emptyToNull(row.modello()));
            materiale.setPosa(emptyToNull(row.posa()));
            materiale.setSiglaRif(emptyToNull(row.siglaRif()));
            entity.getMateriali().add(materiale);
        }
    }

    private static void syncVerifiche(Pratica entity, List<VerificaRigaDto> rows) {
        if (rows == null) {
            return;
        }
        entity.getVerifiche().clear();
        for (VerificaRigaDto row : rows) {
            if (row.tipoProva() == null || row.tipoProva().isBlank()) {
                continue;
            }
            VerificaRiga verifica = new VerificaRiga();
            verifica.setPratica(entity);
            verifica.setTipoProva(row.tipoProva().trim());
            verifica.setValore(emptyToNull(row.valore()));
            verifica.setMarcaStrumento(emptyToNull(row.marcaStrumento()));
            verifica.setModelloStrumento(emptyToNull(row.modelloStrumento()));
            entity.getVerifiche().add(verifica);
        }
    }

    public static List<MaterialeRigaDto> emptyMateriali() {
        return new ArrayList<>();
    }

    public static List<VerificaRigaDto> emptyVerifiche() {
        return new ArrayList<>();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
