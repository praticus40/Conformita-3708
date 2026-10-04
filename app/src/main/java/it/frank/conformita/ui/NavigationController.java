package it.frank.conformita.ui;

import it.frank.conformita.AppSection;
import it.frank.conformita.WizardStep;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class NavigationController {

    private final ObjectProperty<AppSection> currentSection =
            new SimpleObjectProperty<>(AppSection.PRATICHE);
    private final ObjectProperty<WizardStep> currentWizardStep =
            new SimpleObjectProperty<>(WizardStep.IMPRESA);
    private final ObjectProperty<PraticheViewMode> praticheViewMode =
            new SimpleObjectProperty<>(PraticheViewMode.LIST);
    private final BooleanProperty validationPanelVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty darkTheme = new SimpleBooleanProperty(false);
    private final ObjectProperty<Long> currentPraticaId = new SimpleObjectProperty<>();

    public ObjectProperty<AppSection> currentSectionProperty() {
        return currentSection;
    }

    public AppSection getCurrentSection() {
        return currentSection.get();
    }

    public void setCurrentSection(AppSection section) {
        currentSection.set(section);
    }

    public ObjectProperty<WizardStep> currentWizardStepProperty() {
        return currentWizardStep;
    }

    public WizardStep getCurrentWizardStep() {
        return currentWizardStep.get();
    }

    public void setCurrentWizardStep(WizardStep step) {
        currentWizardStep.set(step);
    }

    public ObjectProperty<PraticheViewMode> praticheViewModeProperty() {
        return praticheViewMode;
    }

    public PraticheViewMode getPraticheViewMode() {
        return praticheViewMode.get();
    }

    public void setPraticheViewMode(PraticheViewMode mode) {
        praticheViewMode.set(mode);
    }

    public BooleanProperty validationPanelVisibleProperty() {
        return validationPanelVisible;
    }

    public boolean isValidationPanelVisible() {
        return validationPanelVisible.get();
    }

    public void setValidationPanelVisible(boolean visible) {
        validationPanelVisible.set(visible);
    }

    public void toggleValidationPanel() {
        validationPanelVisible.set(!validationPanelVisible.get());
    }

    public BooleanProperty darkThemeProperty() {
        return darkTheme;
    }

    public boolean isDarkTheme() {
        return darkTheme.get();
    }

    public void setDarkTheme(boolean dark) {
        darkTheme.set(dark);
    }

    public void toggleTheme() {
        darkTheme.set(!darkTheme.get());
    }

    public ObjectProperty<Long> currentPraticaIdProperty() {
        return currentPraticaId;
    }

    public Long getCurrentPraticaId() {
        return currentPraticaId.get();
    }

    public void setCurrentPraticaId(Long id) {
        currentPraticaId.set(id);
    }
}
