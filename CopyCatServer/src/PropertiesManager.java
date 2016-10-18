import javafx.scene.control.Alert;

import java.io.*;
import java.util.Properties;

public class PropertiesManager {
    private Properties properties = new Properties();

    private static PropertiesManager ourInstance = new PropertiesManager();
    public static PropertiesManager getInstance() {
        return ourInstance;
    }
    private PropertiesManager() {
        load();
    }

    public void setProperty(String key, String value){
        properties.setProperty(key, value);
        save();
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    private void save(){
        try{
            File propertiesFile = new File("./credentials.properties");
            FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile, false);
            properties.store(fileOutputStream, "Locally generated credentials file");
        } catch (IOException e) {
            showErrorDialog(e);
        }

    }

    private void load(){
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("./credentials.properties");
        } catch (FileNotFoundException e) {
            inputStream = getClass().getResourceAsStream("credentials.properties");
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
