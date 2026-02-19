package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class EtudiantFormController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private ComboBox<Semestre> comboSemestre;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnAnnuler;

    private List<Parcours> parcoursDispos;

    @FXML
    public void initialize() {
        // Charger les données pour remplir les ComboBox
        UEDAO ueDao = new UEDAO();
        ParcoursDAO pDao = new ParcoursDAO();
        this.parcoursDispos = pDao.chargerParcours(ueDao.chargerUEs());

        comboParcours.setItems(FXCollections.observableArrayList(parcoursDispos));
        comboSemestre.setItems(FXCollections.observableArrayList(Semestre.values()));

        // Personnalisation de l'affichage du parcours dans la combo
        comboParcours.setCellFactory(lv -> new ListCell<Parcours>() {
            @Override protected void updateItem(Parcours p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty ? "" : p.getNom());
            }
        });
        comboParcours.setButtonCell(comboParcours.getCellFactory().call(null));
    }

    @FXML
    private void handleEnregistrer() {
        if (txtNom.getText().isEmpty() || comboParcours.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le nom et le parcours sont obligatoires.");
            alert.showAndWait();
            return;
        }

        EtudiantDAO dao = new EtudiantDAO();
        List<Etudiant> liste = dao.chargerTout(parcoursDispos);

        // Calcul d'un nouvel ID simple
        int nouvelId = liste.stream().mapToInt(Etudiant::getNumE).max().orElse(0) + 1;

        Etudiant nouv = new Etudiant(
                nouvelId,
                txtNom.getText(),
                txtPrenom.getText(),
                comboParcours.getValue(),
                comboSemestre.getValue()
        );

        liste.add(nouv);
        dao.sauvegarderTout(liste);

        fermerFenetre();
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
        stage.close();
    }
}