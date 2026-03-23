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

    /*
    * Conteneur vide, centré par défaut, où on va charger les différentes pages
    * sans que le BorderPane (header) ne bouge
    * Il modifie le module <StackPane fx:id="contentArea"> dans le MainView.fxml
    */
    @FXML
    private StackPane contentArea;

    /** s'exécute automatiquement au démarrage de la fenêtre.*/
    @FXML
    public void initialize() {
    }

    /** méthode qui change le contenu à l'écran à partir d'un fichier fxml (chemin en paramètre)*/
    private void loadView(String fxmlPath) {
        try {
            // crée un objet FXMLLoader
            FXMLLoader fxmlObject = new FXMLLoader(getClass().getResource(fxmlPath));
            // load() est une méthode de la classe FXMLLoader qui transforme les balises fxml
            // en objets java et renvoie le conteneur racine correspondant à notre cast (ici Parent)
            Parent root = fxmlObject.load();
            // Vide la zone centrale et on met la nouvelle page à la place
            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.out.println("Erreur : Impossible de charger la vue " + fxmlPath);
            e.printStackTrace();
        }
    }

    /** Onclick bouton "Voir les étudiants".*/
    @FXML
    private void handleVoirEtudiants() {
        loadView("/EtudiantListView.fxml");
    }

    /** Onclick boutton "Ajouter un étudiant".*/
    @FXML
    private void handleAjouterEtudiant() {
        loadView("/EtudiantForm.fxml");
    }

    /** Onclick boutton "Quitter"*/
    @FXML
    private void handleQuitter() {
        javafx.application.Platform.exit();
    }
}