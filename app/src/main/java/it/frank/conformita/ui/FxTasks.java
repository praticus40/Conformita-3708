package it.frank.conformita.ui;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;

public final class FxTasks {

    private FxTasks() {}

    public static <T> void runAsync(Callable<T> callable, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (onError != null) {
                onError.accept(ex);
            }
        });
        Thread thread = new Thread(task, "conformita-bg");
        thread.setDaemon(true);
        thread.start();
    }

    public static void runAsync(Runnable runnable, Runnable onSuccess, Consumer<Throwable> onError) {
        runAsync(
                () -> {
                    runnable.run();
                    return null;
                },
                ignored -> onSuccess.run(),
                onError);
    }

    public static void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
