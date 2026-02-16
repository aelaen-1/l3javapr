package fr.miage.projetjava.controller;

import java.io.IOException;
import java.net.URL;

import org.w3c.dom.Node;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ControllerAccueil {

    @FXML
    private Button monBouton;

    @FXML
    private void initialize() {
        monBouton.setStyle("-fx-background-color: #bb38ff;");
    }

    @FXML
    private void changementFenetre(ActionEvent event) {
        // Récupère la fenetre actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {

            // Localisation du nouveau fichier fxml qui va être afficher
            final URL url = getClass().getResource("/visualisationDonneeCSV.fxml");

            // Charge le nouveau FXML
            final FXMLLoader fxmlLoader = new FXMLLoader(url);
            final AnchorPane root = (AnchorPane) fxmlLoader.load();

            // Nouvelle scène
            final Scene scene = new Scene(root, 1100, 800);
            // remplacement de l'ancien contenu de la fenetre par le nouveau
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Liste Étudiants");
        stage.show();
    }
}
