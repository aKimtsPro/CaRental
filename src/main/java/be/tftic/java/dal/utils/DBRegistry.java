package be.tftic.java.dal.utils;

import be.tftic.java.dal.dao.QueryCreator;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBRegistry {

    private static final String CONFIG_FILE_NAME = "db_config.yml";
    private static DBRegistry instance;
    public static DBRegistry getInstance() {
        return instance == null ? instance = new DBRegistry() : instance;
    }

    private final Map<String, Config> registry = new HashMap<>();
    private final Map<String, QueryCreator> lazyQueryCreators = new HashMap<>();

    private DBRegistry(){
        initConfigs();
    }

    private void initConfigs(){
        File file = new File( getClass().getResource(STR."/\{CONFIG_FILE_NAME}").getFile() );
        Yaml yaml = new Yaml();
        try(FileInputStream in = new FileInputStream(file)){
            Map<String, Map<String, Object>> map = yaml.load(in);
            for (String configName : map.keySet()) {
                Map<String, Object> rawData = map.get(configName);
                Config config = new Config(
                        configName,
                        (String) rawData.get("host"),
                        (Integer) rawData.get("port"),
                        (String) rawData.get("db_name"),
                        (String) rawData.get("user"),
                        (String) rawData.get("password")
                );
                registry.put(configName, config);
            }

        }catch (IOException | ClassCastException ex){
            throw new LoadConfigFailedException(ex, CONFIG_FILE_NAME);
        }
    }

    public static Config getConfig(String configName){
        return getInstance().registry.get(configName);
    }

    public static  QueryCreator getQueryCreator(String configName){
        QueryCreator queryCreator = getInstance().lazyQueryCreators.get(configName);

        if( queryCreator == null ){
            queryCreator = new QueryCreator(() -> getConfig(configName).getConnection());
            getInstance().lazyQueryCreators.put(configName,queryCreator);
        }

        return queryCreator;
    }


    public static class Config {
        private final String name;
        private final String host;
        private final int port;
        private final String dbName;
        private final String user;
        private final String password;
        private final String url;

        private Config(String name, String host, int port, String dbName, String user, String password) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.dbName = dbName;
            this.user = user;
            this.password = password;
            this.url = STR."jdbc:postgresql://\{host}:\{port}/\{dbName}";
        }

        public String getName() {
            return name;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDbName() {
            return dbName;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, user, password);
        }


    }

}
