package client.alert;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {
    public ErrorAlert(String message){
        super(Alert.AlertType.ERROR);
        setTitle("CopyCat");
        setHeaderText("");
        setContentText(message);
        showAndWait();
    }
}