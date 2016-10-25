package client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller {
    Client client;

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

    public void loginClient(ActionEvent actionEvent) {
        ClientCredentials clientCredentials = collectClientCredentials();
        client = new Client(clientCredentials);
        client.login();
        Main.loginDialogStage.close();
    }

    public void registerClient(ActionEvent actionEvent) {
        ClientCredentials clientCredentials = collectClientCredentials();
        client = new Client(clientCredentials);
        client.register();
        Main.loginDialogStage.close();
    }

    private ClientCredentials collectClientCredentials(){
        String username = usernameTextField.getText(),
               password = passwordField.getText();

        return new ClientCredentials(username, password);
    }

    public void closeProgram(ActionEvent actionEvent) {
        Platform.exit();
    }
}
