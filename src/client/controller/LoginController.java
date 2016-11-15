package client.controller;

import client.Client;
import client.Main;
import client.alert.ErrorAlert;
import client.alert.InfoAlert;
import client.controller.Controller;
import common.ClientCredentials;
import common.Server;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController extends Controller {
    private Client client;
    private Server server;

    private Stage loginDialogStage;

    @FXML public TextField usernameTextField;
    @FXML public PasswordField passwordField;
    @FXML public Button loginButton;
    @FXML public Button registerButton;
    @FXML public Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        //Login and register buttons are locked when username form is empty.
        loginButton.setDisable(true);
        registerButton.setDisable(true);
        usernameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loginButton.setDisable(newValue.trim().isEmpty());
                registerButton.setDisable(newValue.trim().isEmpty());
            }
        });

        usernameTextField.requestFocus();
    }

    public void showLoginDialog() {
        loginDialogStage.showAndWait();
    }

    public void closeLoginDialog() {
        passwordField.clear();
        loginDialogStage.close();
    }

    public void loginClient() {
        try {
            ClientCredentials clientCredentials = collectClientCredentials();
            client.setClientCredentials(clientCredentials);
            client.setRemoteSession(server.login(client.getClientCredentials()));

            boolean response = client.getRemoteSession() != null;

            new InfoAlert(processLoginResponse(response));

            if (response){
                client.setLoggedIn(true);
                setUsernameInTitle();
                closeLoginDialog();
            }
        } catch (RemoteException e) {
            new ErrorAlert("Service unreachable.");
        }
    }

    private String processLoginResponse(boolean response) {
        return response ? "Logged in." : "Invalid username/password.";
    }

    public void setUsernameInTitle() {
        if (client.isLoggedIn())
            Main.primaryStage.setTitle("CopyCat (" + client.getClientCredentials().getUsername() + ")");
    }

    public void registerClient() {
        try {
            ClientCredentials clientCredentials = collectClientCredentials();
            client.setClientCredentials(clientCredentials);
            boolean response = server.register(client.getClientCredentials());

            new InfoAlert(processRegisterResponse(response));
        } catch (RemoteException e) {
            new ErrorAlert("Service unreachable.");
        }
    }

    private String processRegisterResponse(boolean response) {
        return response ? "Registration successful." : "Username already taken.";
    }

    private ClientCredentials collectClientCredentials(){
        String username = usernameTextField.getText(),
               password = passwordField.getText();

        return new ClientCredentials(username, password);
    }

    public void signOut() {
        try {
            client.getRemoteSession().signOut();
        } catch (RemoteException e) {
            new ErrorAlert("Service unreachable.");
        }
    }

    public void setLoginDialogStage(Stage loginDialogStage) {
        this.loginDialogStage = loginDialogStage;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
