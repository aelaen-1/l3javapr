package fr.miage.projetjava.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea; // Le conteneur central de ta MainView.fxml

    @FXML
    private void handleVoirEtudiants() {
        loadView("/EtudiantListView.fxml");
    }

    @FXML
    private void handleAjouterEtudiant() {
        loadView("/EtudiantForm.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                // Si tu n'as pas encore de contentArea, on ouvre une nouvelle fenêtre (fallback)
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitter() {
        Platform.exit();
    }
}