package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Main extends Application {
    public static Stage primaryStage;
    public static Stage loginDialogStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        showMainWindow(primaryStage);
        showLoginDialog(primaryStage);

    }

    private void showMainWindow(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.setTitle("CopyCat");
        primaryStage.setScene(new Scene(root, 852, 400));
        primaryStage.show();
    }

    private void showLoginDialog(Stage primaryStage) throws IOException {

        Main.loginDialogStage = new Stage();
        loginDialogStage.initModality(Modality.WINDOW_MODAL);
        loginDialogStage.initOwner(primaryStage);
        loginDialogStage.setTitle("Login");
        loginDialogStage.setResizable(false);
        loginDialogStage.setOnCloseRequest(event -> {Platform.exit();});
        Parent loginParent = FXMLLoader.load(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(loginParent, 250, 110);
        loginDialogStage.setScene(scene);
        loginDialogStage.show();
    }
}
