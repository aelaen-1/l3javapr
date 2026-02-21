package fr.miage.projetjava;

// ==================== IMPORTS STANDARD ====================
import java.io.IOException; // Exception pour les erreurs d'accès fichiers
import java.net.URL; // Pour représenter une URL (localisation du fichier FXML)

// ==================== IMPORTS LOG4J ====================
import org.apache.logging.log4j.LogManager; // Gestionnaire des loggers
import org.apache.logging.log4j.Logger; // Classe pour enregistrer des logs

// ==================== IMPORTS JAVAFX ====================
import javafx.application.Application; // Classe de base pour les apps JavaFX
import javafx.fxml.FXMLLoader; // Outil pour charger les fichiers FXML
import javafx.scene.Scene; // Représente le contenu à afficher dans la fenêtre
import javafx.scene.layout.AnchorPane; // Conteneur pour positionner les éléments
import javafx.stage.Stage; // La fenêtre principale de l'application

/**
 * Classe principale de l'application JavaFX
 * Elle étend Application, ce qui l'oblige à implémenter la méthode start()
 * C'est le point d'entrée de l'interface graphique
 */
public class Main extends Application {
    // Logger pour enregistrer les informations, erreurs, etc.
    // static = une instance partagée pour toute la classe
    // final = immuable une fois créé
    private static final Logger log = LogManager.getLogger(Main.class);

    /**
     * Méthode appelée automatiquement par JavaFX quand l'app démarre
     * 
     * @param primaryStage : la fenêtre principale (fournie automatiquement par
     *                     JavaFX)
     */
    @Override
    public void start(final Stage primaryStage) {
        // ÉTAPE 1 : Charger le fichier FXML depuis le classpath
        try {
            // Localisation du fichier FXML qui va être lancé
            // getClass().getResource("/accueil.fxml") cherche le fichier dans les
            // ressources
            // Le "/" au début signifie : cherche à partir de la racine du classpath
            // (target/classes/)
            // Retourne une URL pointant sur le fichier FXML
            final URL url_fichier_fxml = getClass().getResource("/accueil.fxml");

            // ÉTAPE 2 : Créer un objet FXMLLoader
            // qui permettra (grâce à load()) de transformer le fichier FXML (texte XML)
            // en objets Java (Button, Label, VBox, AnchorPane, etc.)
            // On l'initialise juste avec l'URL du fichier à transformer
            // À ce stade, AUCUN objet Java n'a été créé, juste un objet FXMLLOADER
            final FXMLLoader fxmlLoader = new FXMLLoader(url_fichier_fxml);

            // ÉTAPE 3 : EXÉCUTER la transformation FXML → Java
            // C'est fxmlLoader.load() qui CRÉE TOUS LES OBJETS JAVA :
            // 1. Lit le fichier accueil.fxml (du texte XML brut)
            // 2. Parse chaque balise XML (<Button>, <Label>, <VBox>, <AnchorPane>, etc.)
            // 3. CRÉE les objets Java correspondants pour chaque balise
            // 4. Les relie les uns aux autres selon la hiérarchie du FXML
            // 5. Retourne l'élément RACINE (ici : AnchorPane)
            // AnchorPane est l'UN DES OBJETS créés par load() - le conteneur racine
            // les autres élements (VBbox, Button, etc) seront à l'intérieur de cet objet (AnchorPane)
            // (AnchorPane) : cast nécessaire car load() retourne Object
            final AnchorPane root = (AnchorPane) fxmlLoader.load();

            // ÉTAPE 4 : Créer une Scene (le contenu visible dans la fenêtre)
            // Scene = conteneur pour les composants graphiques
            // root = l'élément racine du FXML (notre interface)
            // 1100, 800 = largeur et hauteur de la fenêtre
            final Scene scene = new Scene(root, 1100, 800);

            // ÉTAPE 5 : Attacher la Scene à la fenêtre principale
            // primaryStage = la fenêtre principale de l'application (fournie par JavaFX)
            // setScene() ajoute la Scene à cette fenêtre
            primaryStage.setScene(scene);

        } catch (IOException ex) {
            // Si le fichier FXML n'existe pas ou ne peut pas être lu --> Exception
            // On affiche l'erreur sur la sortie d'erreur standard
            System.err.println("Erreur au chargement: " + ex);
        }

        // ÉTAPE 6 : Configuration finale et affichage
        // Définir le titre de la fenêtre qui apparaît dans la barre du système
        primaryStage.setTitle("Accueil");

        // Afficher la fenêtre sur l'écran
        // Sans show(), la fenêtre reste en mémoire mais invisible
        primaryStage.show();
    }

    /**
     * Méthode main : point d'entrée de l'application JVM
     * 
     * @param arg : arguments passés en ligne de commande (inutilisés ici)
     */
    public static void main(String[] arg) {
        // launch() est une méthode héritée d'Application
        // Elle :
        // 1. Initialise le framework JavaFX
        // 2. Crée la fenêtre principale (primaryStage)
        // 3. Appelle la méthode start() automatiquement
        // 4. Démarre la boucle d'événements de l'interface graphique
        launch(arg);
    }
}