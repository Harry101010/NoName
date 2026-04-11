package aptech.proj_NN_group2.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
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