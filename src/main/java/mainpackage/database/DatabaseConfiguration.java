package mainpackage.database;

import org.jdbi.v3.core.Jdbi;

public class DatabaseConfiguration {
    public static final DatabaseConfiguration DEV_CONFIG = new DatabaseConfiguration("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
    public final String jdbcUrl;
    public final String username;
    public final String password;
    private Jdbi jdbi;

    public DatabaseConfiguration(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public Jdbi getJdbi() {
        if (jdbi == null)
            jdbi = Jdbi.create(jdbcUrl, username, password);
        return jdbi;
    }
}
