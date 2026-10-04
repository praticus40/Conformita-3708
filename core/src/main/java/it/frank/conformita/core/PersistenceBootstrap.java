package it.frank.conformita.core;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceBootstrap {

    public static final String PERSISTENCE_UNIT = "conformitaPU";

    private PersistenceBootstrap() {}

    public record FileBootstrapResult(EntityManagerFactory entityManagerFactory, DatabaseSchemaLifecycle lifecycle) {}

    public static FileBootstrapResult createFileEntityManagerFactory(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create data directory: " + dataDir, e);
        }
        DatabaseSchemaLifecycle lifecycle = new DatabaseSchemaLifecycle();
        lifecycle.prepareFileDatabase(dataDir);
        Path dbFile = dataDir.resolve("conformita");
        EntityManagerFactory emf = createEntityManagerFactory("jdbc:h2:file:" + dbFile.toAbsolutePath());
        lifecycle.markSchemaApplied(dataDir);
        return new FileBootstrapResult(emf, lifecycle);
    }

    public static EntityManagerFactory createInMemoryEntityManagerFactory(String dbName) {
        return createEntityManagerFactory("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
    }

    private static EntityManagerFactory createEntityManagerFactory(String jdbcUrl) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
    }
}
