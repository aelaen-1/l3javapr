package fr.miage.projetjava.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // Au lancement de l'application, on affiche la liste par défaut
        handleVoirEtudiants();
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

            // On remplace le contenu actuel par le nouveau
            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitter() {
        Platform.exit();
    }
}