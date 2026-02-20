package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.metier.ScolariteService;
import fr.miage.projetjava.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EtudiantListController {

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, Integer> colId;
    @FXML private TableColumn<Etudiant, String> colNom, colPrenom, colParcours, colSemestre;
    @FXML private TableColumn<Etudiant, Void> colActions;

    private final ObservableList<Etudiant> masterData = FXCollections.observableArrayList();
    private final ScolariteService service = new ScolariteService();
    private List<UE> toutesLesUE;

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
        setupActionsColumn();
        appliquerStyleLignes();
    }

    private void configurerColonnes() {
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("numE"));
        colNom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomE"));
        colPrenom.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("prenomE"));
        colParcours.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getParcours().getNom()));
        colSemestre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSemestreCourant().toString()));
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnInsc = new Button("➕");
            private final Button btnRes = new Button("📝");
            private final Button btnNext = new Button("➔");
            private final HBox pane = new HBox(5, btnEdit, btnInsc, btnRes, btnNext);

            {
                btnEdit.setOnAction(event -> ouvrirFormulaire(getTableView().getItems().get(getIndex())));
                btnInsc.setOnAction(event -> handleInscrire(getTableView().getItems().get(getIndex())));
                btnRes.setOnAction(event -> handleResultat(getTableView().getItems().get(getIndex())));
                btnNext.setOnAction(event -> {
                    Etudiant e = getTableView().getItems().get(getIndex());
                    service.passerSemestre(e);
                    refreshAndSave();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // RÉSOLUTION DE L'ERREUR OPTIONAL : Utilisation d'un Dialog<ButtonType>
    private void handleResultat(Etudiant e) {
        List<ResultatUE> enCours = e.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.ENCOURS).toList();

        if (enCours.isEmpty()) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Résultat UE");
        dialog.setHeaderText("Gérer l'UE pour " + e.getNomE());

        ChoiceBox<ResultatUE> cb = new ChoiceBox<>(FXCollections.observableArrayList(enCours));
        cb.setValue(enCours.get(0));

        ButtonType btnValider = new ButtonType("Valider ✅", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnEchouer = new ButtonType("Échouer ❌", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().setAll(btnValider, btnEchouer, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(new HBox(10, new Label("Choisir l'UE :"), cb));

        // Ici showAndWait() renvoie Optional<ButtonType>, plus d'erreur !
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnValider) service.validerUE(e, cb.getValue().getUe());
            else if (result.get() == btnEchouer) service.echoueUE(e, cb.getValue().getUe());
            refreshAndSave();
        }
    }

    private void handleInscrire(Etudiant e) {
        List<UE> accessibles = service.obtenirUEAccessibles(e, new ArrayList<>(toutesLesUE));
        ChoiceDialog<UE> dialog = new ChoiceDialog<>(null, accessibles);
        dialog.setTitle("Inscription");
        dialog.showAndWait().ifPresent(ue -> {
            service.inscrireEtudiant(e, ue, "2025-2026", e.getSemestreCourant());
            refreshAndSave();
        });
    }

    private void ouvrirFormulaire(Etudiant e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantForm.fxml"));
            Parent root = loader.load();
            EtudiantFormController controller = loader.getController();
            controller.setEtudiant(e);

            Stage stage = new Stage();
            stage.setTitle(e == null ? "Nouveau" : "Modification");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            chargerDonnees();
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void chargerDonnees() {
        this.toutesLesUE = new UEDAO().chargerUEs();
        List<Parcours> parcours = new ParcoursDAO().chargerParcours(toutesLesUE);
        masterData.setAll(new EtudiantDAO().chargerTout(parcours));
        tableEtudiants.setItems(masterData);
    }

    private void refreshAndSave() {
        tableEtudiants.refresh();
        new EtudiantDAO().sauvegarderTout(new ArrayList<>(masterData));
    }

    private void appliquerStyleLignes() {
        tableEtudiants.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Etudiant e, boolean empty) {
                super.updateItem(e, empty);
                if (e != null && !empty && e.getResultatsUE().stream().anyMatch(r -> r.getStatut() == StatutUE.VALIDE)) {
                    setStyle("-fx-background-color: #e8f5e9;");
                } else {
                    setStyle("");
                }
            }
        });
    }
}