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
import java.util.List;

public class EtudiantListController {
    // Ces noms DOIVENT correspondre aux fx:id de ton fichier FXML
    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, Integer> colId;
    @FXML private TableColumn<Etudiant, String> colNom;
    @FXML private TableColumn<Etudiant, String> colPrenom;
    @FXML private TableColumn<Etudiant, String> colParcours;
    @FXML private TableColumn<Etudiant, String> colSemestre;
    @FXML private TableColumn<Etudiant, Void> colActions;

    private List<UE> toutesLesUE;
    private List<Parcours> tousLesParcours;

    @FXML
    public void initialize() {
        // 1. Charger les données de référence
        toutesLesUE = new UEDAO().chargerUEs();
        tousLesParcours = new ParcoursDAO().chargerParcours(toutesLesUE);

        // 2. Charger les étudiants (Passage des 2 arguments requis par ton DAO)
        EtudiantDAO etuDao = new EtudiantDAO();
        List<Etudiant> etudiants = etuDao.chargerTout(tousLesParcours, toutesLesUE);

        // 3. Lier les colonnes aux attributs de l'objet Etudiant
        // "numE", "nomE", "prenomE" sont les noms des champs/getters dans ta classe Etudiant
        colId.setCellValueFactory(new PropertyValueFactory<>("numE"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomE"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomE"));

        // Pour le parcours (Objet) et le semestre (Enum), on utilise un SimpleStringProperty
        colParcours.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParcours().getNom()));

        colSemestre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSemestreCourant().toString()));

        // 4. Injecter la liste dans le tableau
        tableEtudiants.setItems(FXCollections.observableArrayList(etudiants));

        // 5. Ajouter le bouton d'action
        setupActions();

        System.out.println("DEBUG: Chargement terminé. " + etudiants.size() + " étudiants trouvés.");
    }

    private void setupActions() {
        colActions.setCellFactory(p -> new TableCell<>() {
            private final Button btn = new Button("GÉRER DOSSIER");
            {
                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> ouvrirDetails(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
    }

    private void ouvrirDetails(Etudiant e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantDetailsController.fxml"));
            Parent root = loader.load();

            EtudiantDetailsController controller = loader.getController();
            controller.setEtudiant(e, toutesLesUE);

            StackPane contentArea = (StackPane) tableEtudiants.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
        } catch (IOException ex) {
            System.err.println("Erreur FXML : " + ex.getMessage());
        }
    }
}