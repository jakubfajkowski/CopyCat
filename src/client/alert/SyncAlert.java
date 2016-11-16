package client.alert;

import client.Main;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SyncAlert extends Alert {
    final ProgressBar progressBar = new ProgressBar();
    final ProgressIndicator progressIndicator = new ProgressIndicator();
    public SyncAlert(Task<Void> copyTask){
        super(AlertType.NONE);
        setTitle("CopyCat");
        setHeaderText("Syncing...");

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
        progressBar.progressProperty().bind(copyTask.progressProperty());
        progressIndicator.setProgress(-1.0);
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
        this.initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        getDialogPane().setPrefSize(250, 100);
        setResizable(false);

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.setWidth(200);
        stage.getIcons().setAll(Main.primaryStage.getIcons());
        stage.initStyle(StageStyle.UNIFIED);

        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(progressBar, progressIndicator);
        getDialogPane().setContent(hb);
    }

    public void setDone() {
        ButtonType done = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().setAll(done);
        progressIndicator.setProgress(1);
    }
}