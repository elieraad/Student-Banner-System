package me.elieraad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("res/layout/login_page.fxml"));
        primaryStage.setTitle("MSPS Banner System");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void changeScene(String fxml) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.primaryStage.getScene().setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
