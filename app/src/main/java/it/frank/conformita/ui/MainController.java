package it.frank.conformita.ui;

import it.frank.conformita.AppSection;
import it.frank.conformita.WizardStep;
import it.frank.conformita.core.ApplicationFacade;
import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.validation.PersistValidationException;
import it.frank.conformita.core.validation.ValidationContext;
import it.frank.conformita.core.validation.ValidationIssue;
import it.frank.conformita.core.validation.ValidationResult;
import java.awt.Desktop;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable, MainCallbacks {

    private final NavigationController navigation;
    private final ApplicationFacade facade;

    private UiContext uiContext;
    private Stage stage;

    private ImpresaPanelController impresaPanel;
    private PratichePanelController pratichePanel;
    private WizardImpresaPanelController wizardImpresaPanel;
    private WizardCommittentePanelController wizardCommittentePanel;
    private WizardInterventoPanelController wizardInterventoPanel;
    private WizardDichiarazionePanelController wizardDichiarazionePanel;
    private WizardRelazioneTecnicaPanelController wizardRelazionePanel;
    private WizardMaterialiPanelController wizardMaterialiPanel;
    private WizardVerifichePanelController wizardVerifichePanel;
    private WizardLibrettoPanelController wizardLibrettoPanel;
    private WizardRiepilogoPanelController wizardRiepilogoPanel;

    private Parent impresaRoot;
    private Parent praticheRoot;
    private Parent schemiRoot;
    private SchemiLibraryPanelController schemiPanel;

    private PraticaDto currentPratica;

    @FXML
    private MenuItem menuFileNew;

    @FXML
    private MenuItem menuFileOpen;

    @FXML
    private MenuItem menuFileExport;

    @FXML
    private MenuItem menuFileExit;

    @FXML
    private MenuItem menuPracticeDuplicate;

    @FXML
    private MenuItem menuPracticeArchive;

    @FXML
    private MenuItem menuViewValidation;

    @FXML
    private MenuItem menuViewTheme;

    @FXML
    private MenuItem menuHelpAbout;

    @FXML
    private Button toolbarSave;

    @FXML
    private Button toolbarValidate;

    @FXML
    private Button toolbarPreview;

    @FXML
    private Button toolbarExport;

    @FXML
    private ListView<AppSection> sidebarList;

    @FXML
    private VBox wizardContainer;

    @FXML
    private Button wizardBackButton;

    @FXML
    private StackPane sectionContentPane;

    @FXML
    private StackPane wizardStepContentPane;

    @FXML
    private VBox validationPanel;

    @FXML
    private Label validationEmptyLabel;

    @FXML
    private ListView<String> validationIssuesList;

    @FXML
    private Label statusReady;

    @FXML
    private Label statusApp;

    @FXML
    private Label statusDataPath;

    @FXML
    private ToggleButton step1;

    @FXML
    private ToggleButton step2;

    @FXML
    private ToggleButton step3;

    @FXML
    private ToggleButton step4;

    @FXML
    private ToggleButton step5;

    @FXML
    private ToggleButton step6;

    @FXML
    private ToggleButton step7;

    @FXML
    private ToggleButton step8;

    @FXML
    private ToggleButton step9;

    public MainController(NavigationController navigation, ApplicationFacade facade) {
        this.navigation = navigation;
        this.facade = facade;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiContext = new UiContext(facade, navigation, this);

        var impresaLoaded = FxmlHelper.load("/fxml/impresa-form.fxml", uiContext, ImpresaPanelController.class);
        impresaRoot = impresaLoaded.root();
        impresaPanel = impresaLoaded.controller();

        var praticheLoaded = FxmlHelper.load("/fxml/pratiche-list.fxml", uiContext, PratichePanelController.class);
        praticheRoot = praticheLoaded.root();
        pratichePanel = praticheLoaded.controller();

        var schemiLoaded = FxmlHelper.load("/fxml/schemi-library.fxml", uiContext, SchemiLibraryPanelController.class);
        schemiRoot = schemiLoaded.root();
        schemiPanel = schemiLoaded.controller();

        wizardImpresaPanel = FxmlHelper.load("/fxml/wizard-impresa-summary.fxml", uiContext, WizardImpresaPanelController.class)
                .controller();
        wizardCommittentePanel = FxmlHelper.load("/fxml/wizard-committente.fxml", uiContext, WizardCommittentePanelController.class)
                .controller();
        wizardInterventoPanel = FxmlHelper.load("/fxml/wizard-intervento.fxml", uiContext, WizardInterventoPanelController.class)
                .controller();

        sidebarList.setItems(FXCollections.observableArrayList(
                Arrays.stream(AppSection.values())
                        .filter(s -> s != AppSection.NUOVA_PRATICA)
                        .toList()));
        sidebarList.getSelectionModel().select(AppSection.PRATICHE);
        sidebarList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSection, section) -> {
                    if (section != null) {
                        if (section == AppSection.PRATICHE) {
                            navigation.setPraticheViewMode(PraticheViewMode.LIST);
                        }
                        navigation.setCurrentSection(section);
                    }
                });

        navigation.currentSectionProperty().addListener((obs, oldSection, section) -> refreshCenterContent());
        navigation.praticheViewModeProperty().addListener((obs, oldMode, mode) -> refreshCenterContent());
        navigation.currentWizardStepProperty().addListener((obs, oldStep, step) -> {
            if (oldStep != null && oldStep != step) {
                persistWizardStep(oldStep);
            }
            refreshWizardStepContent();
        });
        navigation.currentPraticaIdProperty().addListener((obs, oldId, id) -> updateToolbarState());

        navigation.validationPanelVisibleProperty().addListener((obs, wasVisible, visible) -> {
            validationPanel.setVisible(visible);
            validationPanel.setManaged(visible);
            menuViewValidation.setText(visible ? "Nascondi pannello validazione" : "Mostra pannello validazione");
        });

        navigation.darkThemeProperty().addListener((obs, wasDark, dark) ->
                menuViewTheme.setText(dark ? "Tema chiaro" : "Tema scuro"));

        wizardBackButton.setOnAction(e -> backToPraticheList());

        configureWizardStepper();
        configureMenus();
        configureToolbar();

        statusApp.setText("conformita-3708 MVP");
        if (facade.getDataDirectory() != null) {
            statusDataPath.setText(facade.getDataDirectory().toString());
        }
        validationIssuesList.setOnMouseClicked(e -> {
            String selected = validationIssuesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                navigateToIssue(selected);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initializeAfterShow() {
        navigation.setCurrentSection(AppSection.PRATICHE);
        sidebarList.getSelectionModel().select(AppSection.PRATICHE);
        refreshCenterContent();
        pratichePanel.refresh();
        updateToolbarState();
        if (facade.wasDatabaseReset()) {
            showInfo(
                    "Database aggiornato",
                    "Il database locale è stato ricreato per l'aggiornamento dello schema. "
                            + "I dati precedenti non sono stati migrati.");
        }
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void openPratica(long id) {
        FxTasks.runAsync(
                () -> facade.loadPratica(id),
                dto -> {
                    currentPratica = dto;
                    navigation.setCurrentPraticaId(id);
                    navigation.setCurrentSection(AppSection.PRATICHE);
                    navigation.setPraticheViewMode(PraticheViewMode.WIZARD);
                    sidebarList.getSelectionModel().select(AppSection.PRATICHE);
                    navigation.setCurrentWizardStep(WizardStep.IMPRESA);
                    updateStatusMessage("Pratica #" + id + " aperta");
                },
                ex -> showError("Apri pratica", ex));
    }

    @Override
    public void createNewPraticaFromHub() {
        createNewPratica();
    }

    @Override
    public void deletePratica(PraticaSummary summary) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Elimina pratica");
        confirm.setHeaderText("Eliminare la pratica #" + summary.id() + "?");
        confirm.setContentText("Operazione irreversibile.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) {
                return;
            }
            final long id = summary.id();
            FxTasks.runAsync(
                    () -> {
                        facade.deletePratica(id);
                        return id;
                    },
                    deletedId -> {
                        if (navigation.getCurrentPraticaId() != null
                                && navigation.getCurrentPraticaId() == deletedId) {
                            navigation.setCurrentPraticaId(null);
                            currentPratica = null;
                            navigation.setPraticheViewMode(PraticheViewMode.LIST);
                        }
                        pratichePanel.refresh();
                        updateStatusMessage("Pratica #" + deletedId + " eliminata");
                    },
                    ex -> showError("Elimina pratica", ex));
        });
    }

    @Override
    public void exportPraticaFromHub(PraticaSummary summary) {
        final long id = summary.id();
        FxTasks.runAsync(
                () -> {
                    PraticaDto dto = facade.loadPratica(id);
                    facade.savePratica(dto);
                    return facade.validatePratica(id);
                },
                validation -> {
                    if (validation.hasErrors()) {
                        showValidationResult(validation);
                        showInfo("Esporta PDF", "Correggi gli errori di validazione prima dell'export.");
                        return;
                    }
                    exportPdfForPraticaId(id);
                },
                ex -> showError("Esporta pratica", ex));
    }

    @Override
    public void backToPraticheList() {
        persistWizardStep(navigation.getCurrentWizardStep());
        if (currentPratica != null) {
            PraticaDto snapshot = currentPratica;
            FxTasks.runAsync(
                    () -> facade.savePratica(snapshot),
                    saved -> {
                        currentPratica = saved;
                        navigation.setPraticheViewMode(PraticheViewMode.LIST);
                        pratichePanel.refresh();
                        updateStatusMessage("Torna all'elenco pratiche");
                    },
                    ex -> handlePersistSaveError(ex));
        } else {
            navigation.setPraticheViewMode(PraticheViewMode.LIST);
        }
    }

    @Override
    public void previewPdfForCurrentPratica() {
        Long id = navigation.getCurrentPraticaId();
        if (id == null) {
            showInfo("Anteprima PDF", "Apri una pratica prima dell'anteprima.");
            return;
        }
        persistWizardStep(navigation.getCurrentWizardStep());
        exportToTempAndOpen(id, currentPratica);
    }

    @Override
    public void runValidationForCurrentPratica() {
        runValidation();
    }

    @Override
    public void exportPdfForCurrentPratica() {
        exportPdf();
    }

    @Override
    public void openImpresaSection() {
        openSection(AppSection.IMPRESA);
    }

    @Override
    public void onPraticaUpdated(PraticaDto dto) {
        if (dto.id() == null) {
            return;
        }
        currentPratica = dto;
        FxTasks.runAsync(
                () -> facade.savePratica(dto),
                saved -> {
                    currentPratica = saved;
                    updateStatusMessage("Pratica salvata (autosalvataggio)");
                    pratichePanel.refresh();
                },
                ex -> handlePersistSaveError(ex));
    }

    @Override
    public void refreshPraticheList() {
        pratichePanel.refresh();
    }

    @Override
    public void updateStatusMessage(String message) {
        FxTasks.runOnFxThread(() -> statusReady.setText(message));
    }

    private void configureWizardStepper() {
        ToggleGroup group = new ToggleGroup();
        ToggleButton[] steps = {step1, step2, step3, step4, step5, step6, step7, step8, step9};
        WizardStep[] values = WizardStep.values();
        for (int i = 0; i < steps.length; i++) {
            ToggleButton button = steps[i];
            WizardStep wizardStep = values[i];
            button.setToggleGroup(group);
            button.setText(String.valueOf(wizardStep.getNumber()));
            button.setTooltip(new Tooltip(wizardStep.getTitle()));
            button.selectedProperty().addListener((obs, wasSelected, selected) -> {
                if (selected) {
                    navigation.setCurrentWizardStep(wizardStep);
                }
            });
        }
    }

    private void configureMenus() {
        menuPracticeDuplicate.setDisable(true);
        menuPracticeArchive.setDisable(true);

        menuViewValidation.setOnAction(e -> navigation.toggleValidationPanel());
        menuViewTheme.setOnAction(e -> navigation.toggleTheme());
        menuFileExit.setOnAction(e -> {
            facade.shutdown();
            Platform.exit();
        });
        menuHelpAbout.setOnAction(e -> showAbout());

        menuFileNew.setOnAction(e -> createNewPratica());
        menuFileOpen.setOnAction(e -> openPraticaDialog());
        menuFileExport.setOnAction(e -> exportPdf());
    }

    private void configureToolbar() {
        toolbarSave.setOnAction(e -> saveCurrent());
        toolbarValidate.setOnAction(e -> runValidation());
        toolbarExport.setOnAction(e -> exportPdf());
        toolbarPreview.setOnAction(e -> previewPdfForCurrentPratica());
        toolbarPreview.setTooltip(new Tooltip("Genera PDF temporaneo e apri con il visualizzatore di sistema"));
        updateToolbarState();
    }

    private void updateToolbarState() {
        boolean wizardOpen = navigation.getCurrentSection() == AppSection.PRATICHE
                && navigation.getPraticheViewMode() == PraticheViewMode.WIZARD;
        boolean hasPratica = wizardOpen && navigation.getCurrentPraticaId() != null;
        toolbarValidate.setDisable(!hasPratica);
        toolbarExport.setDisable(!hasPratica);
        toolbarPreview.setDisable(!hasPratica);
        boolean impresaSection = navigation.getCurrentSection() == AppSection.IMPRESA;
        toolbarSave.setDisable(!impresaSection && !hasPratica);
    }

    private void saveCurrent() {
        if (navigation.getCurrentSection() == AppSection.IMPRESA) {
            impresaPanel.save();
            return;
        }
        if (navigation.getCurrentPraticaId() == null) {
            return;
        }
        persistWizardStep(navigation.getCurrentWizardStep());
        if (currentPratica != null) {
            FxTasks.runAsync(
                    () -> facade.savePratica(currentPratica),
                    saved -> {
                        currentPratica = saved;
                        updateStatusMessage("Pratica #" + saved.id() + " salvata");
                        pratichePanel.refresh();
                    },
                    ex -> handlePersistSaveError(ex));
        }
    }

    private void handlePersistSaveError(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        if (root instanceof PersistValidationException pve) {
            FxTasks.runOnFxThread(() -> {
                showValidationResult(pve.getResult());
                updateStatusMessage("Salvataggio annullato: correggere i campi evidenziati");
            });
            return;
        }
        FxTasks.runOnFxThread(() -> updateStatusMessage("Errore salvataggio: " + ex.getMessage()));
    }

    private void createNewPratica() {
        FxTasks.runAsync(
                () -> facade.createPratica(),
                dto -> {
                    currentPratica = dto;
                    navigation.setCurrentPraticaId(dto.id());
                    navigation.setCurrentSection(AppSection.PRATICHE);
                    navigation.setPraticheViewMode(PraticheViewMode.WIZARD);
                    sidebarList.getSelectionModel().select(AppSection.PRATICHE);
                    navigation.setCurrentWizardStep(WizardStep.IMPRESA);
                    pratichePanel.refresh();
                    updateStatusMessage("Nuova pratica #" + dto.id());
                },
                ex -> showError("Nuova pratica", ex));
    }

    private void openPraticaDialog() {
        FxTasks.runAsync(
                () -> facade.listPratiche(),
                list -> {
                    if (list.isEmpty()) {
                        showInfo("Apri pratica", "Nessuna pratica presente. Creane una nuova.");
                        return;
                    }
                    ChoiceDialog<PraticaSummary> dialog = new ChoiceDialog<>(list.getFirst(), list);
                    dialog.setTitle("Apri pratica");
                    dialog.setHeaderText("Seleziona pratica");
                    dialog.setContentText("Pratica:");
                    dialog.showAndWait().ifPresent(summary -> openPratica(summary.id()));
                },
                ex -> showError("Apri pratica", ex));
    }

    private void runValidation() {
        Long id = navigation.getCurrentPraticaId();
        if (id == null) {
            return;
        }
        persistWizardStep(navigation.getCurrentWizardStep());
        PraticaDto snapshot = currentPratica;
        FxTasks.runAsync(
                () -> {
                    if (snapshot != null) {
                        facade.savePratica(snapshot);
                    }
                    return facade.validatePratica(id);
                },
                this::showValidationResult,
                ex -> showError("Validazione", ex));
    }

    private void exportPdf() {
        Long id = navigation.getCurrentPraticaId();
        if (id == null) {
            showInfo("Esporta PDF", "Apri o crea una pratica prima di esportare.");
            return;
        }
        persistWizardStep(navigation.getCurrentWizardStep());
        PraticaDto snapshot = currentPratica;
        FxTasks.runAsync(
                () -> {
                    if (snapshot != null) {
                        facade.savePratica(snapshot);
                    }
                    return facade.validatePratica(id);
                },
                validation -> {
                    if (validation.hasErrors()) {
                        showValidationResult(validation);
                        showInfo("Esporta PDF", "Correggi gli errori di validazione prima dell'export.");
                        return;
                    }
                    FileChooser chooser = new FileChooser();
                    exportPdfForPraticaId(id);
                },
                ex -> showError("Validazione", ex));
    }

    private void exportPdfForPraticaId(long id) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salva pacchetto PDF certificato");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        chooser.setInitialFileName("certificato-pratica-" + id + ".pdf");
        var file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        Path target = file.toPath();
        FxTasks.runAsync(
                () -> {
                    facade.exportPdfPacchetto(id, target);
                    return target;
                },
                path -> updateStatusMessage("PDF esportato: " + path),
                ex -> showError("Esporta PDF", ex));
    }

    private void exportToTempAndOpen(long id, PraticaDto snapshot) {
        FxTasks.runAsync(
                () -> {
                    if (snapshot != null) {
                        facade.savePratica(snapshot);
                    }
                    ValidationResult validation = facade.validatePratica(id);
                    if (validation.hasErrors()) {
                        throw new IllegalStateException("Validazione non superata");
                    }
                    Path temp = Files.createTempFile("conformita-preview-" + id + "-", ".pdf");
                    facade.exportPdfPacchetto(id, temp);
                    return temp;
                },
                path -> {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(path.toFile());
                        }
                        updateStatusMessage("Anteprima PDF: " + path);
                    } catch (Exception e) {
                        showError("Anteprima PDF", e);
                    }
                },
                ex -> {
                    if (ex.getMessage() != null && ex.getMessage().contains("Validazione")) {
                        FxTasks.runAsync(
                                () -> facade.validatePratica(id),
                                this::showValidationResult,
                                e -> showError("Validazione", e));
                        showInfo("Anteprima PDF", "Correggi gli errori di validazione prima dell'anteprima.");
                    } else {
                        showError("Anteprima PDF", ex);
                    }
                });
    }

    private void showValidationResult(ValidationResult result) {
        validationIssuesList.getItems().clear();
        if (result.issues().isEmpty()) {
            validationEmptyLabel.setText("Nessun errore.");
            validationEmptyLabel.setManaged(true);
            validationEmptyLabel.setVisible(true);
            updateStatusMessage("Validazione superata");
            return;
        }
        validationEmptyLabel.setText("Errori rilevati:");
        validationIssuesList.setItems(FXCollections.observableArrayList(
                result.issues().stream().map(i -> i.context().name() + ": " + i.message()).toList()));
        updateStatusMessage("Validazione: " + result.issues().size() + " errori");
    }

    private void navigateToIssue(String issueLine) {
        if (issueLine.startsWith(ValidationContext.IMPRESA.name())) {
            navigation.setCurrentWizardStep(WizardStep.IMPRESA);
        } else if (issueLine.startsWith(ValidationContext.COMMITTENTE.name())) {
            navigation.setCurrentWizardStep(WizardStep.COMMITTENTE);
        } else if (issueLine.startsWith(ValidationContext.INTERVENTO.name())) {
            navigation.setCurrentWizardStep(WizardStep.INTERVENTO);
        } else if (issueLine.startsWith(ValidationContext.DICHIARAZIONE.name())) {
            navigation.setCurrentWizardStep(WizardStep.DICHIARAZIONE);
        } else if (issueLine.startsWith(ValidationContext.RELAZIONE_TECNICA.name())) {
            navigation.setCurrentWizardStep(WizardStep.RELAZIONE_TECNICA);
        } else if (issueLine.startsWith(ValidationContext.MATERIALI.name())) {
            navigation.setCurrentWizardStep(WizardStep.MATERIALI);
        } else if (issueLine.startsWith(ValidationContext.VERIFICHE.name())) {
            navigation.setCurrentWizardStep(WizardStep.VERIFICHE);
        } else if (issueLine.startsWith(ValidationContext.LIBRETTO.name())) {
            navigation.setCurrentWizardStep(WizardStep.LIBRETTO_ALLEGATI);
        }
    }

    private void openSection(AppSection section) {
        sidebarList.getSelectionModel().select(section);
        navigation.setCurrentSection(section);
        updateToolbarState();
    }

    private void refreshCenterContent() {
        AppSection section = navigation.getCurrentSection();
        boolean wizard = section == AppSection.PRATICHE
                && navigation.getPraticheViewMode() == PraticheViewMode.WIZARD;
        wizardContainer.setVisible(wizard);
        wizardContainer.setManaged(wizard);
        sectionContentPane.setVisible(!wizard);
        sectionContentPane.setManaged(!wizard);

        if (wizard) {
            if (navigation.getCurrentPraticaId() == null) {
                wizardStepContentPane.getChildren()
                        .setAll(PlaceholderViewFactory.forSection(AppSection.PRATICHE));
            } else {
                refreshWizardStepContent();
            }
        } else if (section == AppSection.IMPRESA) {
            sectionContentPane.getChildren().setAll(impresaRoot);
            impresaPanel.loadFromDatabase();
        } else if (section == AppSection.PRATICHE) {
            sectionContentPane.getChildren().setAll(praticheRoot);
            pratichePanel.refresh();
        } else if (section == AppSection.SCHEMI) {
            sectionContentPane.getChildren().setAll(schemiRoot);
            schemiPanel.refresh();
        } else {
            sectionContentPane.getChildren().setAll(PlaceholderViewFactory.forSection(section));
        }
        updateToolbarState();
        updateStatusBarPratica();
    }

    private void refreshWizardStepContent() {
        if (navigation.getCurrentSection() != AppSection.PRATICHE
                || navigation.getPraticheViewMode() != PraticheViewMode.WIZARD) {
            return;
        }
        Long id = navigation.getCurrentPraticaId();
        if (id == null) {
            wizardStepContentPane.getChildren()
                    .setAll(PlaceholderViewFactory.forSection(AppSection.PRATICHE));
            return;
        }

        Runnable showStep = () -> {
            WizardStep step = navigation.getCurrentWizardStep();
            if (currentPratica == null || !id.equals(currentPratica.id())) {
                FxTasks.runAsync(
                        () -> facade.loadPratica(id),
                        dto -> {
                            currentPratica = dto;
                            displayWizardStep(step);
                        },
                        ex -> showError("Carica pratica", ex));
            } else {
                displayWizardStep(step);
            }
        };
        showStep.run();
    }

    private void displayWizardStep(WizardStep step) {
        selectStepToggle(step);
        switch (step) {
            case IMPRESA -> {
                var loaded = FxmlHelper.load("/fxml/wizard-impresa-summary.fxml", uiContext, WizardImpresaPanelController.class);
                wizardImpresaPanel = loaded.controller();
                wizardImpresaPanel.loadPratica(currentPratica);
                wizardImpresaPanel.refreshImpresaSummary();
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case COMMITTENTE -> {
                var loaded = FxmlHelper.load("/fxml/wizard-committente.fxml", uiContext, WizardCommittentePanelController.class);
                wizardCommittentePanel = loaded.controller();
                wizardCommittentePanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case INTERVENTO -> {
                var loaded = FxmlHelper.load("/fxml/wizard-intervento.fxml", uiContext, WizardInterventoPanelController.class);
                wizardInterventoPanel = loaded.controller();
                wizardInterventoPanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case DICHIARAZIONE -> {
                var loaded = FxmlHelper.load("/fxml/wizard-dichiarazione.fxml", uiContext, WizardDichiarazionePanelController.class);
                wizardDichiarazionePanel = loaded.controller();
                wizardDichiarazionePanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case RELAZIONE_TECNICA -> {
                var loaded =
                        FxmlHelper.load("/fxml/wizard-relazione-tecnica.fxml", uiContext, WizardRelazioneTecnicaPanelController.class);
                wizardRelazionePanel = loaded.controller();
                wizardRelazionePanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case MATERIALI -> {
                var loaded = FxmlHelper.load("/fxml/wizard-materiali.fxml", uiContext, WizardMaterialiPanelController.class);
                wizardMaterialiPanel = loaded.controller();
                wizardMaterialiPanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case VERIFICHE -> {
                var loaded = FxmlHelper.load("/fxml/wizard-verifiche.fxml", uiContext, WizardVerifichePanelController.class);
                wizardVerifichePanel = loaded.controller();
                wizardVerifichePanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case LIBRETTO_ALLEGATI -> {
                var loaded = FxmlHelper.load("/fxml/wizard-allegati.fxml", uiContext, WizardLibrettoPanelController.class);
                wizardLibrettoPanel = loaded.controller();
                wizardLibrettoPanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
            case RIEPILOGO -> {
                var loaded = FxmlHelper.load("/fxml/wizard-riepilogo.fxml", uiContext, WizardRiepilogoPanelController.class);
                wizardRiepilogoPanel = loaded.controller();
                wizardRiepilogoPanel.loadPratica(currentPratica);
                wizardStepContentPane.getChildren().setAll(loaded.root());
            }
        }
        updateStatusBarPratica();
    }

    private void persistWizardStep(WizardStep step) {
        if (currentPratica == null || step == null) {
            return;
        }
        PraticaDto merged = switch (step) {
            case COMMITTENTE -> wizardCommittentePanel != null
                    ? wizardCommittentePanel.collectPratica(currentPratica)
                    : currentPratica;
            case INTERVENTO -> wizardInterventoPanel != null
                    ? wizardInterventoPanel.collectPratica(currentPratica)
                    : currentPratica;
            case DICHIARAZIONE -> wizardDichiarazionePanel != null
                    ? wizardDichiarazionePanel.collectPratica(currentPratica)
                    : currentPratica;
            case RELAZIONE_TECNICA -> wizardRelazionePanel != null
                    ? wizardRelazionePanel.collectPratica(currentPratica)
                    : currentPratica;
            case MATERIALI -> wizardMaterialiPanel != null
                    ? wizardMaterialiPanel.collectPratica(currentPratica)
                    : currentPratica;
            case VERIFICHE -> wizardVerifichePanel != null
                    ? wizardVerifichePanel.collectPratica(currentPratica)
                    : currentPratica;
            case LIBRETTO_ALLEGATI -> wizardLibrettoPanel != null
                    ? wizardLibrettoPanel.collectPratica(currentPratica)
                    : currentPratica;
            default -> currentPratica;
        };
        currentPratica = merged;
    }

    private void selectStepToggle(WizardStep step) {
        ToggleButton target = switch (step) {
            case IMPRESA -> step1;
            case COMMITTENTE -> step2;
            case INTERVENTO -> step3;
            case DICHIARAZIONE -> step4;
            case RELAZIONE_TECNICA -> step5;
            case MATERIALI -> step6;
            case VERIFICHE -> step7;
            case LIBRETTO_ALLEGATI -> step8;
            case RIEPILOGO -> step9;
        };
        if (!target.isSelected()) {
            target.setSelected(true);
        }
    }

    private void updateStatusBarPratica() {
        Long id = navigation.getCurrentPraticaId();
        if (id != null && navigation.getPraticheViewMode() == PraticheViewMode.WIZARD) {
            statusReady.setText("Pratica #" + id + " — wizard");
        } else if (navigation.getCurrentSection() != null) {
            statusReady.setText(navigation.getCurrentSection().getLabel());
        }
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni");
        alert.setHeaderText("Conformità 37/08 — MVP");
        alert.setContentText(
                "Dichiarazione di conformità impianti elettrici (DM 37/08).\n"
                        + "Persistenza H2 in cartella data, export pacchetto PDF certificato.");
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        alert.setTitle("Conformità 37/08");
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    private void showError(String header, Throwable ex) {
        FxTasks.runOnFxThread(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            alert.setTitle("Conformità 37/08");
            alert.setHeaderText(header);
            alert.showAndWait();
            updateStatusMessage("Errore: " + ex.getMessage());
        });
    }
}
