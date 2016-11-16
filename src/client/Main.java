package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        showMainWindow(primaryStage);
    }

    private void showMainWindow(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
        primaryStage.setTitle("CopyCat");
        primaryStage.setScene(new Scene(root, 852, 400));
        primaryStage.show();
    }
}
