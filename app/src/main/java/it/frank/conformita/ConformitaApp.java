package it.frank.conformita;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import it.frank.conformita.core.ApplicationFacade;
import it.frank.conformita.ui.MainController;
import it.frank.conformita.ui.NavigationController;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConformitaApp extends Application {

    private final NavigationController navigationController = new NavigationController();
    private final ApplicationFacade applicationFacade = new ApplicationFacade();

    @Override
    public void init() {
        applyTheme(navigationController.isDarkTheme());
        navigationController.darkThemeProperty().addListener((obs, wasDark, isDark) -> applyTheme(isDark));
    }

    @Override
    public void start(Stage stage) throws IOException {
        Path installHome = Paths.get("app", "build", "install", "conformita-3708").toAbsolutePath().normalize();
        if (java.nio.file.Files.isDirectory(installHome)) {
            System.setProperty("app.home", installHome.toString());
        }
        applicationFacade.startup(Paths.get("data"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-shell.fxml"));
        loader.setControllerFactory(type -> {
            if (type == MainController.class) {
                return new MainController(navigationController, applicationFacade);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot create controller " + type.getName(), e);
            }
        });

        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/css/layout.css"))
                        .toExternalForm());

        stage.setTitle("Conformità 37/08");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            applicationFacade.shutdown();
            Platform.exit();
        });
        stage.show();

        controller.initializeAfterShow();
    }

    @Override
    public void stop() {
        applicationFacade.shutdown();
    }

    private static void applyTheme(boolean dark) {
        if (dark) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
    }
}
