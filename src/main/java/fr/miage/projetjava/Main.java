package fr.miage.projetjava;

import fr.miage.projetjava.database.CreationTableBD;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;

import javafx.scene.control.*;




public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);


    @Override
    public void start(final Stage primaryStage)
    {
        try
        {
            // Localisation du fichier FXML qui va être lance, on va dans la classe courante et on va chercher le fichier accueil
            //renvoie une URL pointant sur le fichier
            final URL url_fichier_fxml = getClass().getResource("/accueil.fxml");

            // FXMLLoader permet de charger les fichiers fxml
            //Loader va lire le fichier fxml et créer des composants graphiques qui sont décrit dedans
            final FXMLLoader fxmlLoader = new FXMLLoader(url_fichier_fxml);

            // fxmlLoader.load lit le fichier accueil, créé les objets graphiques et retourne le noeud principale du fichier
            //(AnchorPane) -> conversion car c'est l'élément racine du fichier fxml
            //l'interface graphique est créé en mémoire ici
            final AnchorPane root = (AnchorPane) fxmlLoader.load();


            // Scene esreprésente le contenu afficher dans la fenêtre
            // on lui donne root qui est le coeur principale et la largeur et la hauteur
            final Scene scene = new Scene(root, 1100, 800);

            //primaryStage est la fenêtre principale de l'application
            //permet d'aficher l'interface dans la fenêtre
            primaryStage.setScene(scene);
        }
        catch (IOException ex)
        {
            System.err.println("Erreur au chargement: " + ex);
        }

        primaryStage.setTitle("Accueil");
        //affichage de la scene
        primaryStage.show();
    }


    public static void main(String[] arg)
    {
        //creation bd et des tables
        CreationTableBD.init();
        //lancement de l'interface
        launch(arg);
    }

}