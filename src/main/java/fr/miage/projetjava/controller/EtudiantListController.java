package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Ce contrôleur sert d'intermédiaire pour afficher la liste des étudiants dans l'interface
 */
public class EtudiantListController {
    // On lie ici les éléments visuels (IHM) depuis l'interface
    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, Integer> colId;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, String> colParcours;
    @FXML private TableColumn<Etudiant, String> colSemestre;
    @FXML private TableColumn<Etudiant, Void> colActions;
    @FXML private Label lblTitre;
    // Variables pour stocker les données chargées depuis les fichiers csv
    private ArrayList<UE> toutesLesUE;
    private ArrayList<Parcours> tousLesParcours;
    /*
     * Cette méthode prépare la page dès qu'elle s'affiche à l'écran
     */
    @FXML
    public void initialize() {
        // On demande aux classes DAO de lire les fichiers CSV pour remplir nos listes
        toutesLesUE = new UEDAO().chargerUEs();
        tousLesParcours = new ParcoursDAO().chargerParcours(toutesLesUE);
        EtudiantDAO etuDao = new EtudiantDAO();
        List<Etudiant> etudiants = etuDao.chargerTout(tousLesParcours, toutesLesUE);
        // On indique au tableau quelle variable de la classe Etudiant va dans quelle colonne
        colId.setCellValueFactory(new PropertyValueFactory<>("numE"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomE"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomE"));
        // Pour le parcours on récupère uniquement le nom pour l'afficher en texte
        colParcours.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcours().getNom()));

        // Pareil pour le semestre on transforme l'objet en simple texte
        colSemestre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSemestreCourant().toString()));

        // On injecte la liste finale des étudiants dans le tableau visuel
        tableEtudiants.setItems(FXCollections.observableArrayList(etudiants));

        // On lance la création automatique des boutons d'action sur chaque ligne
        setupActions();
        System.out.println("DEBUG: Chargement terminé. " + etudiants.size() + " étudiants trouvés.");
    }

    /*
     * Cette méthode fabrique un bouton pour chaque ligne du tableau
     */
    private void setupActions() {
        colActions.setCellFactory(p -> new TableCell<>() {
            private final Button btn = new Button("GÉRER DOSSIER");
            {
                // On définit l'apparence du bouton (couleurs et curseur)
                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand;");

                // On définit ce qu'il se passe quand on clique : ouvrir la fiche de l'étudiant choisi
                btn.setOnAction(e -> ouvrirDetails(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Si la ligne du tableau est vide on ne met pas de bouton
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
    }
    /*
     * Cette méthode permet de changer de page pour voir les notes de l'étudiant
     */
    private void ouvrirDetails(Etudiant e) {
        try {
            // On charge le fichier de l'interface des détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantDetailsController.fxml"));
            Parent root = loader.load();
            // On transmet l'objet étudiant cliqué au nouveau contrôleur qui va s'ouvrir
            EtudiantDetailsController controller = loader.getController();
            controller.setEtudiant(e, toutesLesUE);
            // On trouve la zone centrale de l'application pour remplacer la liste par les détails
            StackPane contentArea = (StackPane) tableEtudiants.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
        } catch (IOException ex) {
            System.out.println("Erreur de chargement de la vue : " + ex.getMessage());
        }
    }
    /*
     * Retourne à la liste globale des étudiants
     */
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            // On récupère la zone centrale via le tableau
            StackPane contentArea = (StackPane) tableEtudiants.getScene().lookup("#contentArea");
            // On change la vue
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à l'accueil : " + e.getMessage());
        }
    }
}