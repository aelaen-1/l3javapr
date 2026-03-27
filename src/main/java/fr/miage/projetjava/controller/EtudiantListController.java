package fr.miage.projetjava.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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
    private EtudiantDAO etuDao = new EtudiantDAO();
    /**
     * Cette méthode prépare la page dès qu'elle s'affiche à l'écran
     */
    @FXML
    public void initialize() {
        // On demande aux classes DAO de lire les fichiers CSV pour remplir nos listes
        toutesLesUE = new UEDAO().chargerUEs();
        tousLesParcours = new ParcoursDAO().chargerParcours(toutesLesUE);
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
        System.out.println("Chargement terminé. " + etudiants.size() + " étudiants trouvés.");
    }

    /**
     * Cette méthode fabrique un bouton pour chaque ligne du tableau
     */
    private void setupActions() {
        colActions.setCellFactory(p -> new TableCell<>() {
            private final Button btnGerer = new Button("GÉRER");
            private final Button btnSuppr = new Button("SUPPRIMER");
            private final HBox container = new HBox(10, btnGerer, btnSuppr); // On met les deux dans un conteneur
            {
                btnGerer.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");
                btnSuppr.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                // Action du bouton Gérer
                btnGerer.setOnAction(e -> ouvrirDetails(getTableView().getItems().get(getIndex())));

                // Action du bouton Supprimer ,on appelle la méthode handleSupprimer
                btnSuppr.setOnAction(e -> {
                    tableEtudiants.getSelectionModel().select(getIndex()); // On force la sélection
                    handleSupprimer();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(container); // On affiche le groupe de boutons
            }
        });
    }
    /**
     * Cette méthode permet de changer de page pour voir les notes de l'étudiant
     */
    private void ouvrirDetails(Etudiant e) {
        try {
            // On charge le fichier de l'interface des détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantDetails.fxml"));
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

    /**
     * Methode pour supprimer un etudiant
     */
    @FXML
    private void handleSupprimer() {
        //On récupère l'étudiant sélectionné dans le tableau
        Etudiant etudiantSelectionne = tableEtudiants.getSelectionModel().getSelectedItem();

        if (etudiantSelectionne != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer " + etudiantSelectionne.getNomE() + " " + etudiantSelectionne.getPrenomE() + " ?");
            alert.setContentText("Action irréversible!");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                //on le supprime de la BD
                Connection connexion = ConnexionBD.connexionBD();
                EtudiantBD.supprimerEtudiant(connexion, etudiantSelectionne.getNumE());

                //On le retire du tableau visuel
                tableEtudiants.getItems().remove(etudiantSelectionne);
                // On demande au DAO de mettre à jour le fichier CSV
                // On récupère la liste complète actuelle du tableau pour la sauvegarder
                List<Etudiant> listeAMettreAJour = new ArrayList<>(tableEtudiants.getItems());
                // On utilise la méthode de sauvegarde du DAO
                etuDao.sauvegarderTout(listeAMettreAJour);
                System.out.println("Étudiant supprimé et CSV mis à jour.");
            }
        } else {
            // Alerte si rien n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Aucune sélection");
            alert.setContentText("Sélectionnez un étudiant dans le tableau d'abord.");
            alert.showAndWait();
        }
    }
    /**
     * Retourne à la liste globale des étudiants
     */
    @FXML
    private void handleRetour() {
        try {
            // Pour éviter de dupliquer le Header, on recharge le MainView
            // et on l'installe comme nouvelle racine (Root) de la fenêtre.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();

            // On remplace TOUTE la scène actuelle par une nouvelle instance propre de MainView
            tableEtudiants.getScene().setRoot(root);

            System.out.println("Retour à l'accueil : interface réinitialisée.");
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à l'accueil : " + e.getMessage());
        }
    }
}