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
    // identité de l'étudiant
    @FXML private Label lblTitre, lblParcours, lblMention;
    // affichage sur l'IHM les informations d'un Etudiant
    @FXML private Label lblCreditsValides, lblCreditsEnCours, lblSemestreActuel, lblAnneeActuelle;
    // Listes des Ues  suivies par un Etudiant
    @FXML private ListView<String> listValidees, listEnCours, listAccessibles;
    private Etudiant etudiant;
    private List<UE> toutesLesUE;
    private final ScolariteService service = new ScolariteService();
    /*
     * Initialise les données de la vue quand on arrive sur la fiche
     */
    public void setEtudiant(Etudiant e, List<UE> ues) {
        this.etudiant = e;
        this.toutesLesUE = ues;
        rafraichir();
    }
    /*
     * cette méthode met à jour tous les éléments graphiques de l'écran
     */
    private void rafraichir() {
        if (etudiant == null) return;
        // mise à jour du nom et prénom en haut
        lblTitre.setText(etudiant.getNomE() + " " + etudiant.getPrenomE());
        // Affichage du parcours et de la mention
        if (etudiant.getParcours() != null) {
            lblParcours.setText("Parcours : " + etudiant.getParcours().getNom());
            lblMention.setText("Mention : " + etudiant.getParcours().getMention());
        }
        // Affichage du semestre actuel
        lblSemestreActuel.setText(etudiant.getSemestreCourant().toString());
        // Recherche de l'année la plus récente
        String anneeRef = "2026";
        for (ResultatUE r : etudiant.getResultatsUE()) {
            if (r.getAnnee().compareTo(anneeRef) > 0) {
                anneeRef = r.getAnnee();
            }
        }
        lblAnneeActuelle.setText(anneeRef);
        // Calcul des crédits ECTS de l'etudiant
        int valides = 0;
        int enCours = 0;
        for (ResultatUE r : etudiant.getResultatsUE()) {
            if (r.getStatut() == StatutUE.VALIDE) {
                valides += r.getUe().getCredit();
            } else if (r.getStatut() == StatutUE.ENCOURS) {
                enCours += r.getUe().getCredit();
            }
        }
        lblCreditsValides.setText(valides + " ECTS");
        lblCreditsEnCours.setText(enCours + " ECTS");
        // Remplissage des listes UEs
        ArrayList<String> listeValidees = new ArrayList<>();
        ArrayList<String> listeEnCours = new ArrayList<>();

        for (ResultatUE r : etudiant.getResultatsUE()) {
            if (r.getStatut() == StatutUE.VALIDE) {
                listeValidees.add(r.getUe().getCode() + " - " + r.getUe().getIntitule() + " (" + r.getUe().getCredit() + " pts)");
            } else if (r.getStatut() == StatutUE.ENCOURS) {
                listeEnCours.add(r.getUe().getCode() + " - " + r.getUe().getIntitule());
            }
        }
        listValidees.getItems().setAll(listeValidees);
        listEnCours.getItems().setAll(listeEnCours);
        //cette méthode fait appel au Service pour savoir quelles UEs sont accessibles
        List<UE> accessibles = service.obtenirUEAccessibles(etudiant, new ArrayList<>(toutesLesUE));
        ArrayList<String> listeAcc = new ArrayList<>();
        for (UE ue : accessibles) {
            listeAcc.add(ue.getCode() + " - " + ue.getIntitule());
        }
        listAccessibles.getItems().setAll(listeAcc);
    }
    /*
     * cettte methode affiche une fenêtre surgissante avec tout l'historique de l'étudiant
     */
    @FXML
    private void handleVisualiserResultats() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historique Académique");
        dialog.setHeaderText("Détail du cursus de " + etudiant.getNomE());

        TableView<ResultatUE> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(etudiant.getResultatsUE()));

        TableColumn<ResultatUE, String> colUE = new TableColumn<>("UE");
        colUE.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUe().getCode()));

        TableColumn<ResultatUE, String> colAnnee = new TableColumn<>("Année");
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));

        TableColumn<ResultatUE, String> colSemestre = new TableColumn<>("Semestre");
        colSemestre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSemestre().toString()));

        TableColumn<ResultatUE, String> colStatut = new TableColumn<>("Résultat");
        colStatut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatut().toString()));

        table.getColumns().addAll(colUE, colAnnee, colSemestre, colStatut);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dialog.getDialogPane().setContent(new VBox(10, table));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /*
     * Ouvre une boîte de choix pour inscrire l'étudiant à une nouvelle UE
     */
    @FXML
    private void handleInscrire() {
        List<UE> accessibles = service.obtenirUEAccessibles(etudiant, new ArrayList<>(toutesLesUE));
        ChoiceDialog<UE> dialog = new ChoiceDialog<>(null, accessibles);
        dialog.setTitle("Inscription");
        dialog.setHeaderText("Inscrire l'étudiant à une UE");
        dialog.showAndWait().ifPresent(ue -> {
            service.inscrireEtudiant(etudiant, ue, "2026", etudiant.getSemestreCourant());
            sauvegarder();
        });
    }
    /*
     *cette methode permet de mettre une note (Valider ou Échouer) à une UE en cours
     */
    @FXML
    private void handleNoter() {
        ArrayList<ResultatUE> enCours = new ArrayList<>();
        for (ResultatUE r : etudiant.getResultatsUE()) {
            if (r.getStatut() == StatutUE.ENCOURS) {
                enCours.add(r);
            }
        }

        if (enCours.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucune UE en cours à noter.").show();
            return;
        }
        ChoiceBox<ResultatUE> cb = new ChoiceBox<>(FXCollections.observableArrayList(enCours));
        cb.setConverter(new StringConverter<>() {
            @Override public String toString(ResultatUE r) { return r == null ? "" : r.getUe().getCode(); }
            @Override public ResultatUE fromString(String s) { return null; }
        });

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(new VBox(10, new Label("UE à noter :"), cb));

        ButtonType bVal = new ButtonType("Valider ", ButtonBar.ButtonData.OK_DONE);
        ButtonType bEch = new ButtonType("Échouer", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().setAll(bVal, bEch, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == bVal) service.validerUE(etudiant, cb.getValue().getUe());
            else if (res == bEch) service.echoueUE(etudiant, cb.getValue().getUe());
            sauvegarder();
        });
    }
    /*
     * Change le semestre de l'étudiant (Impair <-> Pair)
     */
    @FXML
    private void handleSemestre() {
        service.passerSemestre(etudiant);
        sauvegarder();
    }
    /*
     * Retourne à la liste globale des étudiants
     */
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
    /*
     * Enregistre les modifications dans le fichier CSV et rafraîchit l'écran
     */
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