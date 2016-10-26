package client;

import javafx.scene.control.Alert;

public class InfoAlert extends Alert{
    public InfoAlert(String message){
        super(AlertType.INFORMATION);
        setTitle("CopyCat");
        setHeaderText("");
        setContentText(message);
        showAndWait();
    }
}
