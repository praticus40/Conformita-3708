package it.frank.conformita.ui.geo;

import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public final class GeoComboSupport {

    private GeoComboSupport() {}

    public static void setupProvinciaCombo(ComboBox<ProvinciaItem> combo, Runnable onUserChange) {
        combo.setEditable(true);
        combo.getItems().setAll(ItalianGeoCatalog.allProvinces());
        combo.setConverter(provinciaConverter());
        combo.getEditor().textProperty().addListener((obs, oldText, text) -> {
            filterProvinciaItems(combo, text);
            if (onUserChange != null) {
                onUserChange.run();
            }
        });
        combo.valueProperty().addListener((obs, o, n) -> {
            if (n != null && combo.isEditable()) {
                combo.getEditor().setText(n.label());
            }
            if (onUserChange != null) {
                onUserChange.run();
            }
        });
    }

    public static void setProvinciaValue(ComboBox<ProvinciaItem> combo, String siglaOrName) {
        ItalianGeoCatalog.findProvince(siglaOrName).ifPresentOrElse(
                item -> {
                    combo.setValue(item);
                    combo.getEditor().setText(item.label());
                },
                () -> combo.getEditor().setText(siglaOrName != null ? siglaOrName : ""));
    }

    public static String readProvinciaSigla(ComboBox<ProvinciaItem> combo) {
        String raw = combo.isEditable() ? combo.getEditor().getText() : null;
        if (raw == null && combo.getValue() != null) {
            raw = combo.getValue().label();
        }
        return ItalianGeoCatalog.resolveProvinciaSigla(raw, combo.getValue());
    }

    public static void setupComuneCombo(
            ComboBox<String> combo, Supplier<String> provinciaSiglaSupplier, Runnable onUserChange) {
        combo.setEditable(true);
        refreshComuneSuggestions(combo, provinciaSiglaSupplier, "");
        combo.getEditor().textProperty().addListener((obs, oldText, text) -> {
            refreshComuneSuggestions(combo, provinciaSiglaSupplier, text);
            if (onUserChange != null) {
                onUserChange.run();
            }
        });
        combo.valueProperty().addListener((obs, o, n) -> {
            if (onUserChange != null) {
                onUserChange.run();
            }
        });
    }

    public static void setComuneValue(ComboBox<String> combo, String comune, Supplier<String> provinciaSiglaSupplier) {
        if (comune == null || comune.isBlank()) {
            combo.getEditor().clear();
            combo.setValue(null);
            return;
        }
        combo.setValue(comune);
        combo.getEditor().setText(comune);
        refreshComuneSuggestions(combo, provinciaSiglaSupplier, comune);
    }

    public static String readComuneText(ComboBox<String> combo) {
        if (combo.getValue() != null && !combo.getValue().isBlank()) {
            return combo.getValue().trim();
        }
        String editor = combo.getEditor().getText();
        return editor == null ? "" : editor.trim();
    }

    private static void filterProvinciaItems(ComboBox<ProvinciaItem> combo, String text) {
        if (text == null || text.isBlank()) {
            combo.setItems(FXCollections.observableArrayList(ItalianGeoCatalog.allProvinces()));
            return;
        }
        String q = text.toLowerCase();
        var filtered = ItalianGeoCatalog.allProvinces().stream()
                .filter(p -> p.sigla().toLowerCase().startsWith(q)
                        || p.nome().toLowerCase().contains(q)
                        || p.label().toLowerCase().contains(q))
                .toList();
        combo.setItems(FXCollections.observableArrayList(filtered));
        if (!combo.isShowing() && !filtered.isEmpty()) {
            combo.show();
        }
    }

    private static void refreshComuneSuggestions(
            ComboBox<String> combo, Supplier<String> provinciaSiglaSupplier, String text) {
        String sigla = provinciaSiglaSupplier != null ? provinciaSiglaSupplier.get() : null;
        var matches = ItalianGeoCatalog.searchComuni(text, sigla);
        combo.setItems(FXCollections.observableArrayList(matches));
        if (text != null && !text.isBlank() && !matches.isEmpty() && !combo.isShowing()) {
            combo.show();
        }
    }

    private static StringConverter<ProvinciaItem> provinciaConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(ProvinciaItem item) {
                return item == null ? "" : item.label();
            }

            @Override
            public ProvinciaItem fromString(String string) {
                return ItalianGeoCatalog.findProvince(string).orElse(null);
            }
        };
    }
}
