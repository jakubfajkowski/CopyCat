package client.controller;

import client.Client;
import client.Main;
import client.Refresh;
import client.alert.ErrorAlert;
import client.alert.InfoAlert;
import common.FileInfo;
import common.PropertiesManager;
import common.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private Timer syncTimer;
    private Timer checkTimer;

    @FXML private LoginController loginController;
    @FXML private FileTransferController fileTransferController;
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
            Refresh.getInstance().setMainController(this);
            Main.primaryStage.setOnCloseRequest(event -> {
                signOut();
                System.exit(0);
            });
            createLoginDialog();
            setDefaultClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelTimers() {
        if (syncTimer != null) syncTimer.cancel();
        if (checkTimer != null) checkTimer.cancel();
    }

    private void createLoginDialog() throws IOException {
        Stage loginDialogStage = new Stage();
        loginDialogStage.initModality(Modality.APPLICATION_MODAL);
        loginDialogStage.initStyle(StageStyle.UTILITY);
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

    private boolean startServer() {
        String serverIp = PropertiesManager.getInstance().getProperty("SERVER_IP");
        int port = Integer.valueOf(PropertiesManager.getInstance().getProperty("PORT"));
        try {
            initializeServerConnection(serverIp, port);
            return true;
        } catch (RemoteException | NotBoundException e) {
            new ErrorAlert("Server " + serverIp + ":" + port + " is unreachable.");
            return false;
        }
    }

    private void initializeServerConnection(String ip, int port) throws RemoteException, NotBoundException {
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\FajQa\\IdeaProjects\\CopyCat\\rsc\\client\\ClientTruststore");
        //System.setProperty("javax.net.ssl.trustStorePassword", "CopyCat");

        Registry registry = LocateRegistry.getRegistry(ip, port);//, new SslRMIClientSocketFactory());

        Server server = (Server) registry.lookup("SERVER");

        loginController.setServer(server);
    }

    private void setSyncTimerTask() {
        syncTimer = new Timer();

        try {
            Date date = firstSyncTime(syncTimeTextField.getText());
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> syncManually());
                }
            };

            syncTimer.schedule(timerTask, date, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        } catch (ParseException e) {
            new ErrorAlert("Cannot resolve sync time.");
            autoSyncButton.setSelected(false);
        } catch (NullPointerException e) {
            new ErrorAlert("You must specify sync time.");
            autoSyncButton.setSelected(false);
        }
    }

    private Date firstSyncTime(String timeString) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar syncTimeCal = Calendar.getInstance();
        syncTimeCal.setTime(sdf.parse(timeString));
        int syncHour = syncTimeCal.get(Calendar.HOUR);
        int syncMinute = syncTimeCal.get(Calendar.MINUTE);

        Calendar todayCal = Calendar.getInstance();
        int todayHour = todayCal.get(Calendar.HOUR);
        int todayMinute = todayCal.get(Calendar.MINUTE);

        if (syncHour <= todayHour && syncMinute <= todayMinute)
            todayCal.add(Calendar.DATE, 1);
        todayCal.set(Calendar.HOUR, syncHour);
        todayCal.set(Calendar.MINUTE, syncMinute);

        return todayCal.getTime();
    }

    private void setCheckTimerTask() {
        checkTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        Refresh.getInstance().refreshAll();
                    } catch (RemoteException e) {
                        Platform.runLater(() -> {new ErrorAlert("Service unreachable.");});
                    }
                });
            }
        };
        checkTimer.schedule(timerTask, new Date(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
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

    public void addFileToTable(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add File to Repository");
        Stage stage = (Stage) addButton.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent(f -> tableController.addRecord(new FileInfo(f)));

        ArrayList<FileInfo> records = new ArrayList<>(tableController.getRecords());
        client.setFileList(records);
        serializeRecords(records);

        try {
            Refresh.getInstance().refreshAll();
        } catch (RemoteException e) {
            Platform.runLater(() -> {new ErrorAlert("Service unreachable.");});
        }
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
                    fileTransferController.deleteFile(fileInfo);
                    tableController.popSelectedRecord();
                }

            ArrayList<FileInfo> records = new ArrayList<>(tableController.getRecords());
            client.setFileList(records);
            serializeRecords(records);

            Refresh.getInstance().refreshAll();
        }
        catch (NullPointerException e) {
            new InfoAlert("No selected file.");
        } catch (RemoteException e) {
            new ErrorAlert("Service unreachable.");
        }
    }

    public void loadSerializedRecords() {
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
            new ErrorAlert("Unable to load serialized records.");
        }
    }

    public void serializeRecords(Serializable records){
        try {
            FileOutputStream fileOut = new FileOutputStream(getClientUsername() + "fileinforecords.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(records);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            new ErrorAlert("Unable to serialize table records.");
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
        if(autoSyncButton.isSelected()) setSyncTimerTask();
    }

    public void toggleAutoSyncProperty(){
        PropertiesManager.getInstance().setProperty(getClientUsername() + "AUTO_SYNC", String.valueOf(autoSyncButton.isSelected()));
        if(autoSyncButton.isSelected()) setSyncTimerTask();
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
                FileInfo selectedFileRecord = tableController.getSelectedFileRecord();
                if (selectedFileRecord != null) {
                    fileTransferController.retrieveBackup(selectedFileRecord);
                }
                else
                    new InfoAlert("Please select a file to retrieve.");
            } catch (IOException e) {
                new ErrorAlert(e.getMessage());
            }
        }
    }

    public void showLoginDialog() {
        if(startServer()) {
            loginController.showLoginDialog();
            if(client.isLoggedIn()) {
                loadProperties();
                fileTransferController.setRemoteSession(client.getRemoteSession());
                loadSerializedRecords();
                if(autoSyncButton.isSelected()) setSyncTimerTask();
                setCheckTimerTask();
            }
            setDisableControls(!client.isLoggedIn());
        }
    }

    public void signOut() {
        if (fileTransferController.isCopying()){
            new InfoAlert("Can't sign out while syncing.");
        }
        else {
            loginController.signOut();
            cancelTimers();
            setDefaultClient();
        }
    }

    private void setDefaultClient() {
        fileTransferController = new FileTransferController();
        client = new Client();
        loginController.setClient(client);
        loadProperties();
        loginController.setUsernameInTitle();
        setDisableControls(!client.isLoggedIn());
        if (syncTimer != null) syncTimer.cancel();
        tableController.setRecords(client.getFileList());
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

    public Client getClient() {
        return client;
    }

    public FileTransferController getFileTransferController() {
        return fileTransferController;
    }

    public TableController getTableController() {
        return tableController;
    }
}
