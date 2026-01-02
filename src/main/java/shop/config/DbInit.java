package shop.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.regex.Pattern;

public class DbInit {

    /**
     * Runs Flyway migrations from classpath:db/migration.
     *
     * Enable/disable:
     * - System property: db.migrate=true|false
     * - Env var: DB_MIGRATE=true|false
     * - app.properties: db.migrate=true|false
     *
     * Default: enabled.
     */
    public static void migrate() {
        boolean shouldMigrate = readBoolean("db.migrate", true)
                || readBoolean("db.init", false);
        if (!shouldMigrate) {
            return;
        }

        String url = DbConfig.getUrl();
        String user = DbConfig.getUsername();
        String pass = DbConfig.getPassword();

        if (url == null || url.isBlank()) {
            System.err.println("DbInit: db.url is missing; skipping migrations.");
            return;
        }

        // NEW: make sure schema exists before Flyway connects to it
        ensureDatabaseExists(url, user, pass);

        try {
            System.out.println("DbInit: running Flyway migrations...");
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, pass)
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
            System.out.println("DbInit: migrations finished.");
        } catch (FlywayException e) {
            throw new RuntimeException("DbInit: Flyway migration failed", e);
        }
    }

    private static boolean readBoolean(String key, boolean defaultValue) {
        String v = System.getProperty(key);
        if (v == null) {
            v = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (v == null) {
            try {
                v = DbConfig.getProperty(key);
            } catch (Exception ignored) {
                v = null;
            }
        }
        if (v == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(v.trim());
    }

    private static final Pattern DB_NAME_SAFE = Pattern.compile("^[A-Za-z0-9_]+$");

    private static void ensureDatabaseExists(String jdbcUrl, String user, String pass) {
        String dbName = extractDbName(jdbcUrl);
        if (dbName == null || dbName.isBlank()) return;

        if (!DB_NAME_SAFE.matcher(dbName).matches()) {
            throw new IllegalArgumentException("DbInit: unsafe database name in db.url: " + dbName);
        }

        System.out.println("DbInit: ensuring database exists: " + dbName); // NEW

        String serverUrl = toServerJdbcUrl(jdbcUrl);

        try (Connection c = DriverManager.getConnection(serverUrl, user, pass);
             Statement st = c.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (Exception e) {
            throw new RuntimeException("DbInit: failed to ensure database exists (" + dbName + ")", e);
        }
    }

    private static String extractDbName(String jdbcUrl) {
        try {
            String u = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring(5) : jdbcUrl; // mysql://...
            URI uri = new URI(u);
            String path = uri.getPath(); // "/electronics_shop"
            if (path == null) return null;
            String name = path.startsWith("/") ? path.substring(1) : path;
            int slash = name.indexOf('/');
            return slash >= 0 ? name.substring(0, slash) : name;
        } catch (Exception ignored) {
            // fallback: ...host:port/DB?params
            int slash = jdbcUrl.indexOf('/', jdbcUrl.indexOf("://") + 3);
            if (slash < 0) return null;
            int q = jdbcUrl.indexOf('?', slash + 1);
            return (q < 0) ? jdbcUrl.substring(slash + 1) : jdbcUrl.substring(slash + 1, q);
        }
    }

    private static String toServerJdbcUrl(String jdbcUrl) {
        try {
            String u = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring(5) : jdbcUrl; // mysql://...
            URI uri = new URI(u);
            String base = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), "/", null, null).toString();
            String q = uri.getQuery();
            return "jdbc:" + base + (q == null || q.isBlank() ? "" : "?" + q);
        } catch (Exception e) {
            // fallback: replace "/db" with "/"
            int slash = jdbcUrl.indexOf('/', jdbcUrl.indexOf("://") + 3);
            if (slash < 0) return jdbcUrl;
            int q = jdbcUrl.indexOf('?', slash);
            return (q < 0) ? (jdbcUrl.substring(0, slash + 1)) : (jdbcUrl.substring(0, slash + 1) + jdbcUrl.substring(q));
        }
    }
}
