import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController extends Controller{
    @FXML private LoginController loginController;

    @FXML private TableView table;
    @FXML private TableController tableController;

    @FXML private TextField serverIpTextField;
    @FXML private TextField portTextField;
    @FXML private TextField syncTimeTextField;

    @FXML private ToggleButton autoSyncButton;
    @FXML private Button manualSyncButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button retrieveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        try {
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProperties() throws IOException {
        PropertiesManager pm = PropertiesManager.getInstance();

        serverIpTextField.setText(pm.getProperty("SERVER_IP"));
        portTextField.setText(pm.getProperty("PORT"));
        syncTimeTextField.setText(pm.getProperty("SYNC_TIME"));
        autoSyncButton.setSelected(Boolean.valueOf(pm.getProperty("AUTO_SYNC")));
    }

    public void addFileToTable(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add File to Repository");
        Stage stage = (Stage) addButton.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent(f -> tableController.addRecord(new FileInfo(f)));
    }

    public void deleteFileFromTable(){
        if (tableController.getSelectedFileRecord() != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete file");
            alert.setHeaderText("Are you sure you want to delete file: " + tableController.getSelectedFileRecord().getName() + "?");
            alert.setContentText("All backup data will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent())
                if (result.get() == ButtonType.OK){
                    tableController.popSelectedRecord();
                }
        }
    }

    public void editServerIpProperty(){
        PropertiesManager.getInstance().setProperty("SERVER_IP", serverIpTextField.getText());
    }

    public void editPortProperty(){
        PropertiesManager.getInstance().setProperty("PORT", portTextField.getText());
    }

    public void editSyncTimeProperty(){
        PropertiesManager.getInstance().setProperty("SYNC_TIME", syncTimeTextField.getText());
    }

    public void toggleAutoSyncProperty(){
        PropertiesManager.getInstance().setProperty("AUTO_SYNC", String.valueOf(autoSyncButton.isSelected()));
    }
}
