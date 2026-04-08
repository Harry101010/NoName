package aptech.proj_NN_group2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException; // Dòng này sẽ sửa lỗi IOException của bạn

public class Database {
	// Trong Database.java
	public static Connection getConnection() throws SQLException {
	    Properties props = new Properties();
	    // Dấu "/" đại diện cho thư mục src/main/resources
	    try (InputStream is = Database.class.getResourceAsStream("/db.properties")) {
	        if (is == null) {
	            throw new SQLException("Khong tim thay file db.properties tai goc resources!");
	        }
	        props.load(is);
	        
	        String url = props.getProperty("db.url");
	        String user = props.getProperty("db.user");
	        String pass = props.getProperty("db.password");

	        return DriverManager.getConnection(url, user, pass);
	    } catch (Exception e) {
	        throw new SQLException("Loi ket noi: " + e.getMessage());
	    }
	}
}


//package aptech.proj_NN_group2.util;
//
//import java.io.InputStream;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//public final class Database {
//    private static HikariDataSource ds;
//
//    static {
//        try {
//            InputStream in = Database.class.getClassLoader().getResourceAsStream("db.properties");
//            Properties props = new Properties();
//            props.load(in);
//
//            HikariConfig cfg = new HikariConfig();
//            cfg.setJdbcUrl(props.getProperty("db.url"));
//            cfg.setUsername(props.getProperty("db.user"));
//            cfg.setPassword(props.getProperty("db.password"));
//            cfg.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.maximumPoolSize")));
//            cfg.setPoolName("MyPool");
//
//            ds = new HikariDataSource(cfg);
//        } catch (Exception e) {
//        	System.out.println("Database Error: " + e.getMessage());
//        }
//    }
//
//    public static DataSource getDataSource() {
//        return ds;
//    }
//}