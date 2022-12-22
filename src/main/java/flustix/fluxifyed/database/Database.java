package flustix.fluxifyed.database;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import flustix.fluxifyed.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class Database {
    static HikariDataSource dataSource;

    public static void initializeDataSource() {
        JsonObject config = Main.getConfig().getAsJsonObject("database");

        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariConfig.setJdbcUrl(config.get("url").getAsString());
            hikariConfig.setUsername(config.get("user").getAsString());
            hikariConfig.setPassword(config.get("pass").getAsString());
            dataSource = new HikariDataSource(hikariConfig);
        } catch (Exception ex) {
            Main.LOGGER.error("Error while initializing the database", ex);
        }
    }

    public static ResultSet executeQuery(String query) {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            connection.close(); // no, hikari does not auto close them, i tested it
            return rs;
        } catch (Exception ex) {
            Main.LOGGER.error("Failed to execute query: " + query, ex);
            return null;
        }
    }

    public static ResultSet executeQuery(String query, List<Object> replaceables) {
        for (Object replaceable : replaceables) {
            query = query.replaceFirst("\\?", replaceable.toString());
        }

        return executeQuery(query);
    }

    public static ResultSet executeQuery(String query, Object... replaceables) {
        return executeQuery(query, Arrays.stream(replaceables).toList());
    }

    public static String escape(String str) {
        return str.replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("`", "\\`")
                .replace("=", "\\=");
    }

    public static int connectionCount() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }
}
