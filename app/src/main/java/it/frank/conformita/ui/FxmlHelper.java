package it.frank.conformita.ui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public final class FxmlHelper {

    private FxmlHelper() {}

    public static <T> LoadedView<T> load(String resource, UiContext context, Class<T> controllerType) {
        FXMLLoader loader = new FXMLLoader(FxmlHelper.class.getResource(resource));
        loader.setControllerFactory(type -> createController(type, context, controllerType));
        try {
            Parent root = loader.load();
            T controller = controllerType.cast(loader.getController());
            return new LoadedView<>(root, controller);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load FXML " + resource, e);
        }
    }

    private static Object createController(Class<?> type, UiContext context, Class<?> expectedType) {
        if (type == ImpresaPanelController.class) {
            return new ImpresaPanelController(context);
        }
        if (type == PratichePanelController.class) {
            return new PratichePanelController(context);
        }
        if (type == WizardImpresaPanelController.class) {
            return new WizardImpresaPanelController(context);
        }
        if (type == WizardCommittentePanelController.class) {
            return new WizardCommittentePanelController(context);
        }
        if (type == WizardInterventoPanelController.class) {
            return new WizardInterventoPanelController(context);
        }
        if (type == WizardDichiarazionePanelController.class) {
            return new WizardDichiarazionePanelController(context);
        }
        if (type == WizardRelazioneTecnicaPanelController.class) {
            return new WizardRelazioneTecnicaPanelController(context);
        }
        if (type == WizardMaterialiPanelController.class) {
            return new WizardMaterialiPanelController(context);
        }
        if (type == WizardVerifichePanelController.class) {
            return new WizardVerifichePanelController(context);
        }
        if (type == WizardLibrettoPanelController.class) {
            return new WizardLibrettoPanelController(context);
        }
        if (type == WizardRiepilogoPanelController.class) {
            return new WizardRiepilogoPanelController(context);
        }
        if (type == SchemiLibraryPanelController.class) {
            return new SchemiLibraryPanelController(context);
        }
        throw new IllegalStateException("Missing factory for controller " + type.getName());
    }

    public record LoadedView<T>(Parent root, T controller) {}
}
