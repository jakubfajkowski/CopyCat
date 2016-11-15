package client.controller;

import client.Client;
import client.alert.CopyAlert;
import client.alert.ErrorAlert;
import client.alert.InfoAlert;
import common.FileInfo;
import common.PropertiesManager;
import common.Server;
import javafx.concurrent.Task;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainController extends Controller {
    private Client client;
    private Timer timer;
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
            createLoginDialog();
            fileTransferController = new FileTransferController();
            client = new Client();
            loginController.setClient(client);
            setDisableControls(true);
            loadProperties();
            if(autoSyncButton.isSelected()) setTimerTask();
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTimerTask() {
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(syncTimeTextField.getText());
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    syncManually();
                }
            };

            timer = new Timer();
            timer.schedule(timerTask, date, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        } catch (ParseException e) {
            new ErrorAlert("Cannot resolve sync time.");
        }



    }

    private void loadProperties() {
        String username = getClientUsername();

        PropertiesManager pm = PropertiesManager.getInstance();
        pm.setFileName("config");
        pm.load();

        serverIpTextField.setText(pm.getProperty("SERVER_IP"));
        portTextField.setText(pm.getProperty("PORT"));
        syncTimeTextField.setText(pm.getProperty(username + "SYNC_TIME"));
        autoSyncButton.setSelected(Boolean.valueOf(pm.getProperty(username + "AUTO_SYNC")));
    }

    private String getClientUsername() {
        if(client.getClientCredentials() != null) {
            return client.getClientCredentials().getUsername();
        }
        else
            return "";

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
        client.setFileList(records);
        serializeRecords(records);
    }

    public void deleteFileFromTable(){
        try {
            FileInfo fileInfo = tableController.getSelectedFileRecord();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete file");
            alert.setHeaderText("Are you sure you want to delete file: " + fileInfo.getName() + "?");
            alert.setContentText("All backup data will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent())
                if (result.get() == ButtonType.OK) {
                    tableController.popSelectedRecord();
                }

            ArrayList<FileInfo> records = new ArrayList<>(tableController.getRecords());
            client.setFileList(records);
            serializeRecords(records);
        }
        catch (NullPointerException e) {
            new InfoAlert("No selected file.");
        }
    }

    private void loadSerializedRecords() {
        try {
            String fileName = getClientUsername() + "fileinforecords.ser";
            ObjectInputStream in;
            FileInputStream fileIn = null;

            try{
                fileIn = new FileInputStream(fileName);

            } catch (FileNotFoundException fe) {
                serializeRecords(new ArrayList<FileInfo>());
                fileIn = new FileInputStream(fileName);
            }

            in = new ObjectInputStream(fileIn);

            @SuppressWarnings("unchecked")
            List<FileInfo> list = (List<FileInfo>) in.readObject();
            client.setFileList(list);
            fileTransferController.checkIfActualFiles(list);
            tableController.setRecords(list);
            fileIn.close();
            in.close();
        }
        catch (IOException | ClassNotFoundException e) {
            new ErrorAlert(e.getMessage());
        }
    }

    private void serializeRecords(Serializable records){
        try {
            FileOutputStream fileOut = new FileOutputStream(getClientUsername() + "fileinforecords.ser");
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
        startServer();
    }

    public void editPortProperty(){
        PropertiesManager.getInstance().setProperty("PORT", portTextField.getText());
        startServer();
    }

    public void editSyncTimeProperty(){
        PropertiesManager.getInstance().setProperty(getClientUsername() + "SYNC_TIME", syncTimeTextField.getText());
        if(autoSyncButton.isSelected()) setTimerTask();
    }

    public void toggleAutoSyncProperty(){
        PropertiesManager.getInstance().setProperty(getClientUsername() + "AUTO_SYNC", String.valueOf(autoSyncButton.isSelected()));
        if(autoSyncButton.isSelected()) setTimerTask();
    }

    public void syncManually() {
        if (fileTransferController.isCopying()) {
            new InfoAlert("Already syncing.");
        }
        else {
            try {
                fileTransferController.syncFiles(client.getFileList());
            } catch (IOException e) {
                new ErrorAlert(e.getMessage());
            }
        }
    }

    public void retrieveBackup() {
        if (fileTransferController.isCopying()){
            new InfoAlert("Can't retrieve while syncing.");
        }
        else {
            try {
                fileTransferController.retrieveBackup(tableController.getSelectedFileRecord());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showLoginDialog() {
        if(startServer()) {
            loginController.showLoginDialog();
            loadProperties();
            if(client.isLoggedIn()) {
                fileTransferController.setRemoteSession(client.getRemoteSession());
                loadSerializedRecords();
            }
            setDisableControls(!client.isLoggedIn());
        }
    }

    private void initializeServerConnection(String ip, int port) throws RemoteException, NotBoundException {
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\client\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        Registry registry = LocateRegistry.getRegistry(ip, port);//, new SslRMIClientSocketFactory());

        Server server = (Server) registry.lookup("SERVER");

        loginController.setServer(server);
    }

    private boolean startServer() {
        String serverIp = PropertiesManager.getInstance().getProperty("SERVER_IP");
        int port = Integer.valueOf(PropertiesManager.getInstance().getProperty("PORT"));
        try {
            initializeServerConnection(serverIp, port);
            return true;
        } catch (RemoteException | NotBoundException e) {
            new ErrorAlert("Could not establish server connection.");
            return false;
        }
    }

    public void signOut() {
        if (fileTransferController.isCopying()){
            new InfoAlert("Can't sign out while syncing.");
        }
        else {
            loginController.signOut();
            client = new Client();
            loginController.setClient(client);
            loadProperties();
            loadSerializedRecords();
            loginController.setUsernameInTitle();
            setDisableControls(!client.isLoggedIn());
            new InfoAlert("Signed out.");
        }
    }

    public void setDisableControls(boolean disable) {
        serverIpTextField.setDisable(!disable);
        portTextField.setDisable(!disable);
        syncTimeTextField.setDisable(disable);
        autoSyncButton.setDisable(disable);
        manualSyncButton.setDisable(disable);
        addButton.setDisable(disable);
        deleteButton.setDisable(disable);
        retrieveButton.setDisable(disable);
        loginMenuButton.setDisable(!disable);
        signOutButton.setDisable(disable);
    }
}
