package it.frank.conformita.ui.geo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Province and comuni for autocomplete (comuni from bundled JSON). */
public final class ItalianGeoCatalog {

    private static final Pattern COMUNE_ENTRY =
            Pattern.compile("\\{\"nome\":\"([^\"]+)\".*?\"sigla\":\"([A-Z]{2})\"");
    private static final int DEFAULT_COMUNE_LIMIT = 150;

    private static final List<ProvinciaItem> PROVINCE = List.of(
            new ProvinciaItem("AG", "Agrigento"),
            new ProvinciaItem("AL", "Alessandria"),
            new ProvinciaItem("AN", "Ancona"),
            new ProvinciaItem("AO", "Aosta"),
            new ProvinciaItem("AP", "Ascoli Piceno"),
            new ProvinciaItem("AQ", "L'Aquila"),
            new ProvinciaItem("AR", "Arezzo"),
            new ProvinciaItem("AT", "Asti"),
            new ProvinciaItem("AV", "Avellino"),
            new ProvinciaItem("BA", "Bari"),
            new ProvinciaItem("BG", "Bergamo"),
            new ProvinciaItem("BI", "Biella"),
            new ProvinciaItem("BL", "Belluno"),
            new ProvinciaItem("BN", "Benevento"),
            new ProvinciaItem("BO", "Bologna"),
            new ProvinciaItem("BR", "Brindisi"),
            new ProvinciaItem("BS", "Brescia"),
            new ProvinciaItem("BT", "Barletta-Andria-Trani"),
            new ProvinciaItem("BZ", "Bolzano"),
            new ProvinciaItem("CA", "Cagliari"),
            new ProvinciaItem("CB", "Campobasso"),
            new ProvinciaItem("CE", "Caserta"),
            new ProvinciaItem("CH", "Chieti"),
            new ProvinciaItem("CL", "Caltanissetta"),
            new ProvinciaItem("CN", "Cuneo"),
            new ProvinciaItem("CO", "Como"),
            new ProvinciaItem("CR", "Cremona"),
            new ProvinciaItem("CS", "Cosenza"),
            new ProvinciaItem("CT", "Catania"),
            new ProvinciaItem("CZ", "Catanzaro"),
            new ProvinciaItem("EN", "Enna"),
            new ProvinciaItem("FC", "Forlì-Cesena"),
            new ProvinciaItem("FE", "Ferrara"),
            new ProvinciaItem("FG", "Foggia"),
            new ProvinciaItem("FI", "Firenze"),
            new ProvinciaItem("FM", "Fermo"),
            new ProvinciaItem("FR", "Frosinone"),
            new ProvinciaItem("GE", "Genova"),
            new ProvinciaItem("GO", "Gorizia"),
            new ProvinciaItem("GR", "Grosseto"),
            new ProvinciaItem("IM", "Imperia"),
            new ProvinciaItem("IS", "Isernia"),
            new ProvinciaItem("KR", "Crotone"),
            new ProvinciaItem("LC", "Lecco"),
            new ProvinciaItem("LE", "Lecce"),
            new ProvinciaItem("LI", "Livorno"),
            new ProvinciaItem("LO", "Lodi"),
            new ProvinciaItem("LT", "Latina"),
            new ProvinciaItem("LU", "Lucca"),
            new ProvinciaItem("MB", "Monza e Brianza"),
            new ProvinciaItem("MC", "Macerata"),
            new ProvinciaItem("ME", "Messina"),
            new ProvinciaItem("MI", "Milano"),
            new ProvinciaItem("MN", "Mantova"),
            new ProvinciaItem("MO", "Modena"),
            new ProvinciaItem("MS", "Massa-Carrara"),
            new ProvinciaItem("MT", "Matera"),
            new ProvinciaItem("NA", "Napoli"),
            new ProvinciaItem("NO", "Novara"),
            new ProvinciaItem("NU", "Nuoro"),
            new ProvinciaItem("OR", "Oristano"),
            new ProvinciaItem("PA", "Palermo"),
            new ProvinciaItem("PC", "Piacenza"),
            new ProvinciaItem("PD", "Padova"),
            new ProvinciaItem("PE", "Pescara"),
            new ProvinciaItem("PG", "Perugia"),
            new ProvinciaItem("PI", "Pisa"),
            new ProvinciaItem("PN", "Pordenone"),
            new ProvinciaItem("PO", "Prato"),
            new ProvinciaItem("PR", "Parma"),
            new ProvinciaItem("PT", "Pistoia"),
            new ProvinciaItem("PU", "Pesaro e Urbino"),
            new ProvinciaItem("PV", "Pavia"),
            new ProvinciaItem("PZ", "Potenza"),
            new ProvinciaItem("RA", "Ravenna"),
            new ProvinciaItem("RC", "Reggio Calabria"),
            new ProvinciaItem("RE", "Reggio Emilia"),
            new ProvinciaItem("RG", "Ragusa"),
            new ProvinciaItem("RI", "Rieti"),
            new ProvinciaItem("RM", "Roma"),
            new ProvinciaItem("RN", "Rimini"),
            new ProvinciaItem("RO", "Rovigo"),
            new ProvinciaItem("SA", "Salerno"),
            new ProvinciaItem("SI", "Siena"),
            new ProvinciaItem("SO", "Sondrio"),
            new ProvinciaItem("SP", "La Spezia"),
            new ProvinciaItem("SR", "Siracusa"),
            new ProvinciaItem("SS", "Sassari"),
            new ProvinciaItem("SU", "Sud Sardegna"),
            new ProvinciaItem("SV", "Savona"),
            new ProvinciaItem("TA", "Taranto"),
            new ProvinciaItem("TE", "Teramo"),
            new ProvinciaItem("TN", "Trento"),
            new ProvinciaItem("TO", "Torino"),
            new ProvinciaItem("TP", "Trapani"),
            new ProvinciaItem("TR", "Terni"),
            new ProvinciaItem("TS", "Trieste"),
            new ProvinciaItem("TV", "Treviso"),
            new ProvinciaItem("UD", "Udine"),
            new ProvinciaItem("VA", "Varese"),
            new ProvinciaItem("VB", "Verbano-Cusio-Ossola"),
            new ProvinciaItem("VC", "Vercelli"),
            new ProvinciaItem("VE", "Venezia"),
            new ProvinciaItem("VI", "Vicenza"),
            new ProvinciaItem("VR", "Verona"),
            new ProvinciaItem("VT", "Viterbo"),
            new ProvinciaItem("VV", "Vibo Valentia"));

    private static volatile Map<String, List<String>> comuniByProvincia;

    private ItalianGeoCatalog() {}

    public static List<ProvinciaItem> allProvinces() {
        return PROVINCE;
    }

    public static Optional<ProvinciaItem> findProvince(String siglaOrText) {
        if (siglaOrText == null || siglaOrText.isBlank()) {
            return Optional.empty();
        }
        String trimmed = siglaOrText.trim();
        String upper = trimmed.toUpperCase(Locale.ROOT);
        for (ProvinciaItem p : PROVINCE) {
            if (p.sigla().equalsIgnoreCase(upper)) {
                return Optional.of(p);
            }
        }
        if (trimmed.contains("—")) {
            String sigla = trimmed.substring(0, trimmed.indexOf('—')).trim();
            return findProvince(sigla);
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        for (ProvinciaItem p : PROVINCE) {
            if (p.nome().equalsIgnoreCase(trimmed) || p.nome().toLowerCase(Locale.ROOT).contains(lower)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public static String resolveProvinciaSigla(String editorText, ProvinciaItem selected) {
        if (selected != null) {
            return selected.sigla();
        }
        return findProvince(editorText).map(ProvinciaItem::sigla).orElse(normalizeSigla(editorText));
    }

    public static String normalizeSigla(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String t = text.trim();
        if (t.contains("—")) {
            t = t.substring(0, t.indexOf('—')).trim();
        }
        return t.toUpperCase(Locale.ROOT);
    }

    public static List<String> searchComuni(String query, String provinciaSigla) {
        return searchComuni(query, provinciaSigla, DEFAULT_COMUNE_LIMIT);
    }

    public static List<String> searchComuni(String query, String provinciaSigla, int limit) {
        ensureComuniLoaded();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<String> pool;
        if (provinciaSigla != null && !provinciaSigla.isBlank()) {
            pool = comuniByProvincia.getOrDefault(provinciaSigla.toUpperCase(Locale.ROOT), List.of());
        } else {
            pool = comuniByProvincia.values().stream().flatMap(List::stream).toList();
        }
        if (q.isEmpty()) {
            return pool.size() <= limit ? pool : pool.subList(0, limit);
        }
        List<String> out = new ArrayList<>();
        for (String name : pool) {
            if (name.toLowerCase(Locale.ROOT).contains(q)) {
                out.add(name);
                if (out.size() >= limit) {
                    break;
                }
            }
        }
        return out;
    }

    private static void ensureComuniLoaded() {
        if (comuniByProvincia != null) {
            return;
        }
        synchronized (ItalianGeoCatalog.class) {
            if (comuniByProvincia != null) {
                return;
            }
            comuniByProvincia = loadComuniFromJson();
        }
    }

    private static Map<String, List<String>> loadComuniFromJson() {
        try (InputStream in = ItalianGeoCatalog.class.getResourceAsStream("/geo/comuni.json")) {
            if (in == null) {
                return Map.of();
            }
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, List<String>> map = new HashMap<>();
            Matcher matcher = COMUNE_ENTRY.matcher(json);
            while (matcher.find()) {
                String nome = matcher.group(1);
                String sigla = matcher.group(2);
                map.computeIfAbsent(sigla, k -> new ArrayList<>()).add(nome);
            }
            map.values().forEach(list -> list.sort(String.CASE_INSENSITIVE_ORDER));
            return Collections.unmodifiableMap(map);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load /geo/comuni.json", e);
        }
    }
}
