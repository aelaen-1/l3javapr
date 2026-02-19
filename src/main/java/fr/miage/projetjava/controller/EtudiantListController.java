package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.metier.ScolariteService;
import fr.miage.projetjava.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;

public class EtudiantListController {

    @FXML private TableView<Etudiant> tableEtudiants;
    @FXML private TableColumn<Etudiant, Integer> colId;
    @FXML private TableColumn<Etudiant, String> colNom, colPrenom, colParcours, colSemestre;
    @FXML private ComboBox<UE> comboUEAccessibles;

    private final ObservableList<Etudiant> masterData = FXCollections.observableArrayList();
    private final ScolariteService service = new ScolariteService();
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private List<UE> toutesLesUE;

    @FXML
    public void initialize() {
        // Liaison TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("numE"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomE"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomE"));
        colParcours.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getParcours().getNom()));
        colSemestre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSemestreCourant().toString()));

        // Chargement des données
        UEDAO ueDao = new UEDAO();
        this.toutesLesUE = ueDao.chargerUEs();
        List<Parcours> parcoursDispos = new ParcoursDAO().chargerParcours(toutesLesUE);
        List<Etudiant> etudiants = etudiantDAO.chargerTout(parcoursDispos);
        new ResultatUEDAO().chargerCursus(etudiants, toutesLesUE);
        masterData.setAll(etudiants);
        tableEtudiants.setItems(masterData);

        // Mise à jour de la combo au clic sur un étudiant
        tableEtudiants.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                comboUEAccessibles.setItems(FXCollections.observableArrayList(service.obtenirUEAccessibles(newVal, new ArrayList<>(toutesLesUE))));
            }
        });
    }

    @FXML
    private void handleInscrireSelection() {
        Etudiant e = tableEtudiants.getSelectionModel().getSelectedItem();
        UE selectedUE = comboUEAccessibles.getValue();
        if (e != null && selectedUE != null) {
            service.inscrireEtudiant(e, selectedUE, "2025-2026", e.getSemestreCourant());
            refreshData(e);
        }
    }

    @FXML private void handlePasserSemestre() {
        Etudiant e = tableEtudiants.getSelectionModel().getSelectedItem();
        if (e != null) { service.passerSemestre(e); refreshData(e); }
    }

    @FXML private void handleValiderUE() { modifierStatut(StatutUE.VALIDE); }
    @FXML private void handleEchouerUE() { modifierStatut(StatutUE.ECHOUE); }

    private void modifierStatut(StatutUE statut) {
        Etudiant e = tableEtudiants.getSelectionModel().getSelectedItem();
        if (e == null) return;
        List<ResultatUE> enCours = e.getResultatsUE().stream().filter(r -> r.getStatut() == StatutUE.ENCOURS).toList();

        ChoiceDialog<ResultatUE> d = new ChoiceDialog<>(null, enCours);
        d.showAndWait().ifPresent(res -> {
            if (statut == StatutUE.VALIDE) service.validerUE(e, res.getUe());
            else service.echoueUE(e, res.getUe());
            refreshData(e);
        });
    }

    // Rafraîchit l'interface, affiche le cursus en console et sauvegarde dans le CSV
    private void refreshData(Etudiant e) {
        tableEtudiants.refresh();
        e.afficherCusrus(); // Affichage console
        etudiantDAO.sauvegarderTout(new ArrayList<>(masterData)); // Sauvegarde CSV
    }
}