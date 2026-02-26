package fr.miage.projetjava.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/*
 * Ce contrôleur gère la fenêtre principale, en gros c est le point d'entrée du logiciel
 */
public class MainController {
    // C'est la zone vide au centre de la fenêtre où on va charger les différentes pages
    @FXML private StackPane contentArea;
    /*
     * Cette méthode s'exécute automatiquement au démarrage de la fenêtre.
     */
    @FXML
    public void initialize() {
        // Pour l'instant, elle est vide, mais on pourrait y charger une page d'accueil
    }
    /**
     * Elle se déclenche quand on clique sur le bouton "Voir les étudiants".
     */
    @FXML
    private void handleVoirEtudiants() {
        // On demande de charger la page qui liste tous les étudiants existants dans la BB
        loadView("/EtudiantListView.fxml");
    }
    /*
     * cette methode se déclenche quand on clique sur le bouton "Ajouter un étudiant".
     */
    @FXML
    private void handleAjouterEtudiant() {
        // On demande de charger le formulaire de création
        // (le formulaire ou l'utilisateur veut ajouter un nouveau etudiant
        loadView("/EtudiantForm.fxml");
    }
    /*
     * C'est la méthode "outil" qui s'occupe de changer le contenu de l'écran.
     * @param fxmlPath Le chemin vers le fichier de la vue qu'on veut afficher.
     */
    private void loadView(String fxmlPath) {
        try {
            //On prépare le chargeur de fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            //on transforme le fichier FXML en un objet Java
            Parent root = loader.load();
            //On vide la zone centrale et on met la nouvelle page à la place
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            // Si le fichier .fxml n'est pas trouvé ou est mal écrit
            System.out.println("Erreur : Impossible de charger la vue " + fxmlPath);
            e.printStackTrace();
        }
    }
    /*
     * cette methode se déclenche quand on clique sur le bouton "Quitter".
     */
    @FXML
    private void handleQuitter() {
        //On ferme proprement toute l'application
        javafx.application.Platform.exit();
    }
}