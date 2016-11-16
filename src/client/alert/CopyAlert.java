package client.alert;

import client.Main;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CopyAlert extends Alert {
    public CopyAlert(Task<Void> copyTask){
        super(AlertType.NONE);
        setTitle("CopyCat");
        setHeaderText("Copying...");

        bindProgressBar(copyTask);
        setCancelButton(copyTask);
        decorateWindow();
        setOnCloseRequest(event -> {
            copyTask.cancel();
            this.close();
        });

        show();
    }

    private void bindProgressBar(Task<Void> copyTask) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(copyTask.progressProperty());
        getDialogPane().setContent(progressBar);
    }

    private void setCancelButton(Task<Void> copyTask) {
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getButtonTypes().setAll(cancel);

        setResultConverter(dialogButton -> {
            if (dialogButton == cancel) {
                copyTask.cancel();
                this.close();
            }
            return null;
        });
    }

    private void decorateWindow() {
        this.initModality(Modality.NONE);
        setResizable(true);
        getDialogPane().setPrefSize(250, 100);
        setResizable(false);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.setWidth(200);
        stage.getIcons().setAll(Main.primaryStage.getIcons());
        stage.initStyle(StageStyle.UNIFIED);
        stage.setAlwaysOnTop(true);
    }

    public void setDone() {
        ButtonType done = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().setAll(done);
    }
}