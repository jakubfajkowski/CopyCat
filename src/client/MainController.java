package client;

import common.FileInfo;
import common.PropertiesManager;
import common.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController extends Controller{
    private Client client;
    @FXML private LoginController loginController;
    @FXML private FileTransferController fileTransferController;

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

    @FXML private Button loginMenuButton;
    @FXML private Button signOutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        try {
            loadProperties();
            createLoginDialog();
            fileTransferController = new FileTransferController();
            client = new Client();
            fileTransferController.setClient(client);
            loginController.setClient(client);
            loadSerializedRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProperties() throws IOException {
        PropertiesManager pm = PropertiesManager.getInstance();
        pm.setFileName("config");
        pm.load();

        serverIpTextField.setText(pm.getProperty("SERVER_IP"));
        portTextField.setText(pm.getProperty("PORT"));
        syncTimeTextField.setText(pm.getProperty("SYNC_TIME"));
        autoSyncButton.setSelected(Boolean.valueOf(pm.getProperty("AUTO_SYNC")));
    }

    private void createLoginDialog() throws IOException {
        Stage loginDialogStage = new Stage();
        loginDialogStage.initModality(Modality.WINDOW_MODAL);
        loginDialogStage.setTitle("Login");
        loginDialogStage.setResizable(false);
        loginDialogStage.setOnCloseRequest(event -> loginDialogStage.close());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Parent loginParent = loader.load();
        loginController = loader.getController();
        Scene scene = new Scene(loginParent, 250, 110);
        loginDialogStage.setScene(scene);
        loginController.setLoginDialogStage(loginDialogStage);
    }

    public void addFileToTable(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add File to Repository");
        Stage stage = (Stage) addButton.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent(f -> tableController.addRecord(new FileInfo(f)));

        ArrayList<FileInfo> records = new ArrayList<>(tableController.getRecords());
        fileTransferController.getClient().setFileList(records);
        serializeRecords(records);
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

        ArrayList<FileInfo> records = new ArrayList<>(tableController.getRecords());
        fileTransferController.getClient().setFileList(records);
        serializeRecords(records);
    }

    private void loadSerializedRecords(){
        try{
            FileInputStream fileIn = new FileInputStream("fileinforecords.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            @SuppressWarnings("unchecked")
            List<FileInfo> list = (List<FileInfo>) in.readObject();
            fileTransferController.getClient().setFileList(list);
            tableController.setRecords(list);

        } catch (FileNotFoundException fe) {
            return;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void serializeRecords(Serializable records){
        try {
            FileOutputStream fileOut = new FileOutputStream("fileinforecords.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(records);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public void syncFiles() {
        //tableController.syncFiles();
    }

    public void showLoginDialog() {
        try {
            String serverIp = PropertiesManager.getInstance().getProperty("SERVER_IP");
            int port = Integer.valueOf(PropertiesManager.getInstance().getProperty("PORT"));
            initializeServerConnection(serverIp, port);
            loginController.showLoginDialog();
        } catch (NotBoundException | RemoteException e) {
            new ErrorAlert(e.getMessage());
        }
    }

    private void initializeServerConnection(String ip, int port) throws RemoteException, NotBoundException {
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\client\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        Registry registry = LocateRegistry.getRegistry(ip, port);//, new SslRMIClientSocketFactory());

        Server server = (Server) registry.lookup("SERVER");

        fileTransferController.setServer(server);
        loginController.setServer(server);
    }

    public void signOut() {
        client = new Client();
        fileTransferController.setClient(client);
        loginController.setClient(client);
        new InfoAlert("Signed out.");
    }
}
