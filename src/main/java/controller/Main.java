package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // aqui onde carrega o FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/produto-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 650);

        stage.setTitle("Gerenciamento de Produtos - POO III");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}