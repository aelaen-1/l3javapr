package fr.miage.projetjava.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/*
 * Ce contrôleur gère la fenêtre principale, c'est le point d'entrée du
 * logiciel
 */

public class MainController {

    // C'est la zone vide au centre de la fenêtre où on va charger les différentes
    // pages
    @FXML
    private StackPane contentArea;

    // s'exécute automatiquement au démarrage de la fenêtre.
    @FXML
    public void initialize() {
        // pour l'instant vide
    }

    // méthode qui change le contenu à l'écran à partir du fichier fxml dont on donne le chemin
    // (fxmlPath) en paramètre
    private void loadView(String fxmlPath) {
        try {
            // crée un objet fxml qui nous permettra de convertir les balises du fichier en
            // objets java
            FXMLLoader fxmlObject = new FXMLLoader(getClass().getResource(fxmlPath));
            // load() transforme les balises fxml en objets java et renvoie le conteneur
            // racine correspondant à notre cast (ici Parent)
            Parent root = fxmlObject.load();
            // On vide la zone centrale et on met la nouvelle page à la place
            // contentArea est un objet stackPane
            contentArea.getChildren().setAll();

        } catch (IOException e) {
            System.out.println("Erreur : Impossible de charger la vue " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Onclick bouton "Voir les étudiants".
    @FXML
    private void handleVoirEtudiants() {
        loadView("/EtudiantListView.fxml");
    }

    // Onclick boutton "Ajouter un étudiant".
    @FXML
    private void handleAjouterEtudiant() {
        // On demande de charger le formulaire de création
        // (le formulaire ou l'utilisateur veut ajouter un nouveau etudiant
        loadView("/EtudiantForm.fxml");
    }
    
    // Onclick boutton "Quitter"
    @FXML
    private void handleQuitter() {
        // On ferme proprement toute l'application
        javafx.application.Platform.exit();
    }
}