package client;

import javafx.scene.control.Alert;

import java.io.*;
import java.util.Properties;

class PropertiesManager {
    private Properties properties = new Properties();

    private static PropertiesManager ourInstance = new PropertiesManager();
    static PropertiesManager getInstance() {
        return ourInstance;
    }
    private PropertiesManager() {
        load();
    }

    void setProperty(String key, String value){
        properties.setProperty(key, value);
        save();
    }

    String getProperty(String key){
        return properties.getProperty(key);
    }

    private void save(){
        try{
            File propertiesFile = new File("./config.properties");
            FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile, false);
            properties.store(fileOutputStream, "Locally generated config file");
        } catch (IOException e) {
            showErrorDialog(e);
        }

    }

    private void load(){
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("./config.properties");
        } catch (FileNotFoundException e) {
            inputStream = getClass().getResourceAsStream("config.properties");
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            showErrorDialog(e);
        }

    }

    private void showErrorDialog(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Properties manager error.");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
}
