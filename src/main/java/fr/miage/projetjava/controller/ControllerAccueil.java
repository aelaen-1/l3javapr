package fr.miage.projetjava.controller;

// ==================== IMPORTS POUR LES UTILITAIRES ====================
import java.io.IOException; // Exception levée si erreur d'accès au fichier FXML
import java.net.URL; // Classe pour représenter une URL (localisation du fichier)

import javafx.event.ActionEvent; // Événement déclenché quand un bouton est cliqué
// ==================== IMPORTS POUR JAVAFX ====================
import javafx.fxml.FXML; // Annotation pour lier les éléments FXML au controller
import javafx.fxml.FXMLLoader; // Outil pour charger les fichiers FXML
import javafx.scene.Node; // Classe parent pour tous les éléments de l'interface
import javafx.scene.Scene; // Conteneur pour le contenu affichable dans une fenêtre
import javafx.scene.control.Button; // Composant bouton interactif
import javafx.scene.layout.AnchorPane; // Conteneur pour positionner les éléments
import javafx.stage.Stage; // La fenêtre principale ou secondaire

/**
 * ControllerAccueil : Contrôleur pour l'écran d'accueil de l'application
 * 
 * Son rôle :
 * - Gérer les événements sur l'écran d'accueil (clics boutons, etc.)
 * - Permettre l'interaction avec l'utilisateur
 * - Charger et afficher d'autres écrans quand l'utilisateur clique sur un
 * bouton
 * 
 * Liaison avec FXML :
 * - Ce controller est lié au fichier accueil.fxml via l'attribut fx:controller
 * - Les méthodes avec @FXML communiquent avec les éléments du FXML
 * - Les variables avec @FXML sont les référence aux composants du FXML
 * 
 * Flux d'exécution :
 * 1. initialize() est appelée automatiquement après le chargement du FXML
 * 2. changementFenetre() est appelée quand l'utilisateur clique sur un bouton
 */
public class ControllerAccueil {

    // ==================== VARIABLES FXML ====================
    /**
     * Variable liée au bouton du fichier FXML via fx:id="monBouton"
     * 
     * @FXML : annotation indiquant que cette variable est liée à un élément FXML
     *       private : visible uniquement dans cette classe
     *       final : la référence à l'objet Button ne change pas (mais le contenu du
     *       bouton peut changer)
     */
    @FXML
    private Button monBouton;

    // ==================== MÉTHODE DE CYCLE DE VIE ====================
    /**
     * Méthode initialize() : Appelée automatiquement par JavaFX après le chargement
     * du FXML
     * 
     * Cette méthode s'exécute UNE SEULE FOIS quand :
     * 1. Le fichier FXML a été chargé
     * 2. Tous les éléments du FXML ont été créés
     * 3. Toutes les variables @FXML ont été initialisées
     * 
     * Utilisation : Configurer les éléments GUI (style, valeurs, listeners, etc.)
     * 
     * Dans ce cas, on modifie le style du bouton pour qu'il soit violet (#bb38ff)
     */
    @FXML
    private void initialize() {
        // ========== ÉTAPE 1 : Personnaliser le style du bouton ==========
        // -fx-background-color : propriété CSS JavaFX pour la couleur de fond
        // #bb38ff : code hexadécimal de la couleur violet
        monBouton.setStyle("-fx-background-color: #bb38ff;");
    }

    // ==================== MÉTHODE DE GESTION D'ÉVÉNEMENTS ====================
    /**
     * Méthode changementFenetre() : Appelée quand l'utilisateur clique sur le
     * bouton
     * 
     * Cette méthode :
     * 1. Récupère la fenêtre actuelle
     * 2. Charge un nouveau fichier FXML
     * 3. Crée une nouvelle scène avec le nouveau contenu
     * 4. Remplace le contenu de la fenêtre
     * 5. Met à jour le titre et affiche la fenêtre
     * 
     * @param event : l'événement ActionEvent déclenché par le clic du bouton
     *              Contient des informations sur la source du clic
     */
    @FXML
    private void changementFenetre(ActionEvent event) {
        // ========== ÉTAPE 1 : Obtenir la fenêtre actuelle ==========
        // event.getSource() : récupère le composant qui a déclenché l'événement (le
        // bouton)
        // ((Node) ...) : cast du bouton en Node (classe parent de tous les composants)
        // .getScene() : obtient la Scene (le conteneur du bouton)
        // .getWindow() : obtient la Stage (la fenêtre qui contient la Scene)
        // ((Stage) ...) : cast vers Stage (la fenêtre principale)
        // Au final, 'stage' référence la fenêtre principale de l'application
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            // ========== ÉTAPE 2 : Localiser le nouveau fichier FXML ==========
            // getClass().getResource() : cherche le fichier dans le classpath
            // "/visualisationDonneeCSV.fxml" : chemin du fichier FXML à charger
            // "/" au début : cherche à partir de la racine du classpath (target/classes/)
            // Retourne une URL pointant vers ce fichier
            final URL url = getClass().getResource("/visualisationDonneeCSV.fxml");

            // ========== ÉTAPE 3 : Créer et configurer le FXMLLoader ==========
            // FXMLLoader : outil JavaFX qui transforme du XML en composants Java
            // Constructor : on lui passe l'URL du fichier FXML à charger
            final FXMLLoader fxmlLoader = new FXMLLoader(url);

            // ========== ÉTAPE 4 : Charger le FXML et créer l'interface graphique
            // ==========
            // fxmlLoader.load() :
            // - Lit le fichier FXML
            // - Parse chaque balise XML
            // - Crée les objets Java correspondants (Button, VBox, etc.)
            // - Retourne l'élément racine du FXML (ici : AnchorPane)
            // ((AnchorPane) ...) : cast du retour en AnchorPane (l'élément racine)
            final AnchorPane root = (AnchorPane) fxmlLoader.load();

            // ========== ÉTAPE 5 : Créer une nouvelle Scene ==========
            // Scene : conteneur qui va afficher les composants graphiques
            // Constructor parameters :
            // - root : l'élément racine du FXML (ce qu'on veut afficher)
            // - 1100, 800 : largeur et hauteur de la fenêtre
            final Scene scene = new Scene(root, 1100, 800);

            // ========== ÉTAPE 6 : Remplacer le contenu de la fenêtre ==========
            // stage.setScene() : remplace la Scene actuelle par la nouvelle Scene
            // Tout l'ancien contenu est remplacé par le nouveau
            stage.setScene(scene);

        } catch (IOException e) {
            // Si le fichier FXML n'existe pas, n'est pas accessible, ou erreur de parsing
            // --> IOException est levée et on l'attrape ici
            // e.printStackTrace() : affiche l'erreur dans la console (utile pour déboguer)
            e.printStackTrace();
        }

        // ========== ÉTAPE 7 : Mettre à jour le titre de la fenêtre ==========
        // Le titre s'affiche dans la barre du système (en haut de la fenêtre)
        stage.setTitle("Liste Étudiants");

        // ========== ÉTAPE 8 : Afficher la fenêtre ==========
        // stage.show() : affiche la fenêtre sur l'écran
        // Si on avait changé la Scene mais pas appelé show(), la fenêtre resterait
        // invisible
        stage.show();
    }
}
