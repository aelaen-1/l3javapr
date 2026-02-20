package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.List;

public class EtudiantFormController {

    @FXML private TextField txtNom, txtPrenom;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private ComboBox<Semestre> comboSemestre;
    @FXML private Button btnEnregistrer;

    private List<Parcours> parcoursDispos;
    private Etudiant etudiantModif;

    @FXML
    public void initialize() {
        this.parcoursDispos = new ParcoursDAO().chargerParcours(new UEDAO().chargerUEs());
        comboParcours.setItems(FXCollections.observableArrayList(parcoursDispos));
        comboSemestre.setItems(FXCollections.observableArrayList(Semestre.values()));
    }

    public void setEtudiant(Etudiant e) {
        this.etudiantModif = e;
        if (e != null) {
            txtNom.setText(e.getNomE());
            txtPrenom.setText(e.getPrenomE());
            comboParcours.setValue(e.getParcours());
            comboSemestre.setValue(e.getSemestreCourant());
        }
    }

    @FXML
    private void handleEnregistrer() {
        EtudiantDAO dao = new EtudiantDAO();
        List<Etudiant> liste = dao.chargerTout(parcoursDispos);

        if (etudiantModif == null) {
            int nouvelId = liste.stream().mapToInt(Etudiant::getNumE).max().orElse(0) + 1;
            liste.add(new Etudiant(nouvelId, txtNom.getText(), txtPrenom.getText(), comboParcours.getValue(), comboSemestre.getValue()));
        } else {
            for (Etudiant e : liste) {
                if (e.getNumE() == etudiantModif.getNumE()) {
                    e.setNomE(txtNom.getText());
                    e.setPrenomE(txtPrenom.getText());
                    e.setParcours(comboParcours.getValue());
                    e.setSemestreCourant(comboSemestre.getValue());
                }
            }
        }
        dao.sauvegarderTout(liste);
        retournerALaListe(); // Redirection automatique
    }

    @FXML
    private void handleAnnuler() {
        retournerALaListe();
    }

    // Méthode de navigation interne
    private void retournerALaListe() {
        try {
            // On récupère le StackPane (contentArea) depuis la scène actuelle
            StackPane contentArea = (StackPane) btnEnregistrer.getScene().lookup("#contentArea");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantListView.fxml"));
            Parent root = loader.load();

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}