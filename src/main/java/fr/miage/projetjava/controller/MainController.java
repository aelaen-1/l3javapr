package fr.miage.projetjava.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // On ne fait rien ici : le StackPane affiche déjà
        // les boutons définis dans le FXML au lancement.
    }

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
            // On remplace les boutons d'accueil par la nouvelle vue (Liste ou Form)
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitter() {
        javafx.application.Platform.exit();
    }
}