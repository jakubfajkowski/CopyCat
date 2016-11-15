package client.alert;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ErrorAlert extends Alert {
    public ErrorAlert(String message){
        super(Alert.AlertType.ERROR);
        setTitle("CopyCat");
        setHeaderText("");
        setContentText(message);
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UTILITY);
        showAndWait();
    }
}