package aptech.proj_NN_group2.util;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {
    private static HikariDataSource ds;

    static {
        try {
            InputStream in = Database.class.getClassLoader().getResourceAsStream("db.properties");
            Properties props = new Properties();
            props.load(in);

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(props.getProperty("db.url"));
            cfg.setUsername(props.getProperty("db.user"));
            cfg.setPassword(props.getProperty("db.password"));
            cfg.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.maximumPoolSize", "1")));
            cfg.setPoolName("MyPool");

            ds = new HikariDataSource(cfg);
        } catch (Exception e) {
        	System.out.println("Database Error: " + e.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return ds;
    }
}