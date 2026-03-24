package fr.miage.projetjava.vue;
import fr.miage.projetjava.dao.ConnexionBD;
import fr.miage.projetjava.dao.CreationTableBD;
import fr.miage.projetjava.dao.InsertBD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

/**
 * C'est la classe de départ qui lance toute l'interface graphique.
 */
public class MainApp extends Application {
    /*
     * Cette méthode start est le vrai point de départ de l'affichage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // On charge le fichier FXML principal ou acceuil en d'autres termes
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        // On crée une scene "contenu du ficher fxml"
        Scene scene = new Scene(loader.load());
        //On écrit le nom du logiciel en haut de la fenêtre
        primaryStage.setTitle("Scolarité");
        //on installe la scène dans le cadre de la fenêtre
        primaryStage.setScene(scene);
        //On rend la fenêtre visible à l'écran
        primaryStage.show();
    }
    /*
     * C'est la méthode main standard de Java.
     */
    public static void main(String[] args) {

        Connection connexion =  ConnexionBD.connexionBD();
        //création des tables dans la BD
        CreationTableBD.createTable(connexion);

        //insertion des données du fichier csv dans les tables
        InsertBD.insertionDonneeBD(connexion);

        //On appelle launch() pour démarrer le logiciel
        launch(args);
    }
}