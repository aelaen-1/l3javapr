package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.*;
import fr.miage.projetjava.metier.ScolariteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.io.IOException;
import java.util.*;

public class EtudiantDetailsController {
    // Identité de l'étudiant
    @FXML private Label lblTitre, lblParcours, lblMention;

    // Badges d'informations
    @FXML private Label lblCreditsValides, lblCreditsEnCours, lblSemestreActuel, lblAnneeActuelle;

    // Listes de suivi
    @FXML private ListView<String> listValidees, listEnCours, listAccessibles;

    private Etudiant etudiant;
    private List<UE> toutesLesUE;
    private final ScolariteService service = new ScolariteService();

    /**
     * Initialise les données de la vue
     */
    public void setEtudiant(Etudiant e, List<UE> ues) {
        this.etudiant = e;
        this.toutesLesUE = ues;
        rafraichir();
    }

    /**
     * Met à jour tous les éléments graphiques
     */
    private void rafraichir() {
        if (etudiant == null) return;

        // 1. Mise à jour de l'identité
        lblTitre.setText(etudiant.getNomE() + " " + etudiant.getPrenomE());

        // Sécurité pour le parcours et la mention
        if (etudiant.getParcours() != null) {
            lblParcours.setText("Parcours :" +etudiant.getParcours().getNom());
            lblMention.setText("Mention : " + etudiant.getParcours().getMention());
        }

        // 2. Mise à jour des badges (Semestre et Année)
        lblSemestreActuel.setText(etudiant.getSemestreCourant().toString());

        // On récupère l'année la plus récente du cursus de l'étudiant
        String anneeRef = etudiant.getResultatsUE().stream()
                .map(ResultatUE::getAnnee)
                .max(Comparator.naturalOrder())
                .orElse("2026");
        lblAnneeActuelle.setText(anneeRef);

        // 3. Calcul des crédits ECTS
        int valides = etudiant.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.VALIDE)
                .mapToInt(r -> r.getUe().getCredit()).sum();

        int enCours = etudiant.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.ENCOURS)
                .mapToInt(r -> r.getUe().getCredit()).sum();

        lblCreditsValides.setText(valides + " ECTS");
        lblCreditsEnCours.setText(enCours + " ECTS");

        // 4. Remplissage des listes
        listValidees.getItems().setAll(etudiant.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.VALIDE)
                .map(r -> r.getUe().getCode() + " - " + r.getUe().getIntitule() + " (" + r.getUe().getCredit() + " pts)")
                .toList());

        listEnCours.getItems().setAll(etudiant.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.ENCOURS)
                .map(r -> r.getUe().getCode() + " - " + r.getUe().getIntitule())
                .toList());

        listAccessibles.getItems().setAll(service.obtenirUEAccessibles(etudiant, new ArrayList<>(toutesLesUE))
                .stream().map(ue -> ue.getCode() + " - " + ue.getIntitule()).toList());
    }

    @FXML
    private void handleVisualiserResultats() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historique Académique");
        dialog.setHeaderText("Détail du cursus de " + etudiant.getNomE());

        // Création de la TableView
        TableView<ResultatUE> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(etudiant.getResultatsUE()));

        // 1. Colonne UE (Code)
        TableColumn<ResultatUE, String> colUE = new TableColumn<>("UE");
        colUE.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUe().getCode()));

        // 2. Colonne Année
        TableColumn<ResultatUE, String> colAnnee = new TableColumn<>("Année");
        // Vérifie que ta classe ResultatUE a bien une méthode getAnnee()
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));

        // 3. Colonne Semestre (AJOUTÉE)
        TableColumn<ResultatUE, String> colSemestre = new TableColumn<>("Semestre");
        colSemestre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSemestre().toString()));

        // 4. Colonne Statut
        TableColumn<ResultatUE, String> colStatut = new TableColumn<>("Résultat");
        colStatut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatut().toString()));

        // Ajout de toutes les colonnes à la table
        table.getColumns().addAll(colUE, colAnnee, colSemestre, colStatut);

        // Ajustement de la taille pour que tout soit visible
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(500);
        table.setPrefHeight(300);

        dialog.getDialogPane().setContent(new VBox(10, table));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Empêcher la fenêtre d'être minuscule
        dialog.getDialogPane().setMinHeight(400);
        dialog.getDialogPane().setMinWidth(550);

        dialog.showAndWait();
    }

    @FXML
    private void handleInscrire() {
        List<UE> accessibles = service.obtenirUEAccessibles(etudiant, new ArrayList<>(toutesLesUE));

        ChoiceDialog<UE> dialog = new ChoiceDialog<>(null, accessibles);
        dialog.setTitle("Inscription");
        dialog.setHeaderText("Inscrire l'étudiant à une UE");
        dialog.setContentText("UE disponibles :");

        dialog.showAndWait().ifPresent(ue -> {
            service.inscrireEtudiant(etudiant, ue, "2026", etudiant.getSemestreCourant());
            sauvegarder();
        });
    }

    @FXML
    private void handleNoter() {
        List<ResultatUE> enCours = etudiant.getResultatsUE().stream()
                .filter(r -> r.getStatut() == StatutUE.ENCOURS).toList();

        if (enCours.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucune UE en cours à noter.").show();
            return;
        }

        ChoiceBox<ResultatUE> cb = new ChoiceBox<>(FXCollections.observableArrayList(enCours));
        cb.setConverter(new StringConverter<>() {
            @Override public String toString(ResultatUE r) { return r == null ? "" : r.getUe().getCode() + " - " + r.getUe().getIntitule(); }
            @Override public ResultatUE fromString(String s) { return null; }
        });
        cb.setValue(enCours.get(0));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Évaluation");
        dialog.getDialogPane().setContent(new VBox(10, new Label("Sélectionnez l'UE :"), cb));

        ButtonType bVal = new ButtonType("Valider ✅", ButtonBar.ButtonData.OK_DONE);
        ButtonType bEch = new ButtonType("Échouer ❌", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().setAll(bVal, bEch, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == bVal) service.validerUE(etudiant, cb.getValue().getUe());
            else if (res == bEch) service.echoueUE(etudiant, cb.getValue().getUe());
            sauvegarder();
        });
    }

    @FXML
    private void handleSemestre() {
        service.passerSemestre(etudiant);
        sauvegarder();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantListView.fxml"));
            Parent root = loader.load();
            StackPane contentArea = (StackPane) lblTitre.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sauvegarder() {
        EtudiantDAO dao = new EtudiantDAO();
        List<Parcours> parcours = new ParcoursDAO().chargerParcours(toutesLesUE);
        List<Etudiant> liste = dao.chargerTout(parcours, toutesLesUE);

        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getNumE() == etudiant.getNumE()) {
                liste.set(i, etudiant);
                break;
            }
        }
        dao.sauvegarderTout(liste);
        rafraichir();
    }
}