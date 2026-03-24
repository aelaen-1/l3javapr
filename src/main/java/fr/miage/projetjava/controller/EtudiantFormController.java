package fr.miage.projetjava.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.Semestre;
import fr.miage.projetjava.model.UE;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

/**
 * Ce contrôleur gère la création et la modification d'un étudiant.
 */
public class EtudiantFormController {

    @FXML
    private TextField txtNom, txtPrenom;
    @FXML
    private ComboBox<Mention> comboMention;
    @FXML
    private ComboBox<Parcours> comboParcours;
    @FXML
    private ComboBox<Semestre> comboSemestre;
    @FXML
    private Button btnEnregistrer;

    private List<Parcours> tousLesParcours;
    private List<UE> toutesLesUE;
    private Etudiant etudiantModif;

    /**
     * cette methode prépare le formulaire et les listes déroulantes au démarrage de
     */
    @FXML
    public void initialize() {

        toutesLesUE = new UEDAO().chargerUEs();
        tousLesParcours = new ParcoursDAO().chargerParcours(toutesLesUE);
        // On remplit les listes pour les mentions et les semestres
        comboMention.setItems(FXCollections.observableArrayList(Mention.values()));
        comboSemestre.setItems(FXCollections.observableArrayList(Semestre.values()));
        // On configure l'affichage de la Mention pour ne pas voir des codes bizarres
        comboMention.setConverter(new StringConverter<Mention>() {
            @Override
            public String toString(Mention m) {
                return (m == null) ? "" : m.toString();
            }
            @Override
            public Mention fromString(String s) {
                return null;
            }
        });
        // l'affichage du Parcours pour ne voir que son nom
        comboParcours.setConverter(new StringConverter<Parcours>() {
            @Override
            public String toString(Parcours p) {
                return (p == null) ? "" : p.getNom();
            }

            @Override
            public Parcours fromString(String s) {
                return null;
            }
        });

        // Quand on choisit une Mention, on filtre les Parcours
        comboMention.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ArrayList<Parcours> filtres = new ArrayList<>();
                // On boucle sur tous les parcours pour ne garder que ceux de la mention choisie
                for (Parcours p : tousLesParcours) {
                    if (p.getMention() == newVal) {
                        filtres.add(p);
                    }
                }
                comboParcours.setItems(FXCollections.observableArrayList(filtres));
                comboParcours.setDisable(false);
            } else {
                comboParcours.setDisable(true);
                comboParcours.getItems().clear();
            }
        });
    }


    /**
     * Enregistre l'étudiant (nouveau ou modifié) dans le fichier CSV.
     */
    @FXML
    private void handleEnregistrer() {
        try {
            // on vérifie que le parcours est bien sélectionné
            if (comboParcours.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez choisir un parcours !").show();
                return;
            }
            EtudiantDAO dao = new EtudiantDAO();
            ArrayList<Etudiant> listeEtudiants = dao.chargerTout(tousLesParcours, toutesLesUE);

            //pour ajouter l'étudiant dans la bd
            Connection connexion = ConnexionBD.connexionBD();

            if (etudiantModif == null) {
                // création d'un nouvel étudiant
                // On cherche le NumE le plus élevé pour calculer le suivant
                int maxId = 1000;
                for (Etudiant e : listeEtudiants) {
                    if (e.getNumE() > maxId) {
                        maxId = e.getNumE();
                    }
                }
                Etudiant nouvelEtu = new Etudiant(maxId + 1, txtNom.getText(), txtPrenom.getText(),
                        comboParcours.getValue(), comboSemestre.getValue());
                listeEtudiants.add(nouvelEtu);
                //on insert tous le nouveau étudiant dans la bd
                EtudiantBD.insertEtudiant(connexion, maxId+1, txtNom.getText(), txtPrenom.getText(), comboParcours.getValue().getNom(), comboSemestre.getValue().toString());


            } else {
                // modification d'un étudiant existant
                for (Etudiant e : listeEtudiants) {
                    if (e.getNumE() == etudiantModif.getNumE()) {
                        e.setNomE(txtNom.getText());
                        e.setPrenomE(txtPrenom.getText());
                        e.setParcours(comboParcours.getValue());
                        e.setSemestreCourant(comboSemestre.getValue());
                    }
                }
            }
            // On écrase le fichier CSV avec la nouvelle liste mise à jour
            dao.sauvegarderTout(listeEtudiants);
            // On quitte le formulaire pour revenir à la liste
            retournerALaListe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette methode retourne à  la page "EtudiantListController"
     */
    @FXML
    private void handleAnnuler() {
        retournerALaListe();
    }

    /**
     * Onclick boutton "Retour". Recharge la vue de la liste des étudiants.
     */
    private void retournerALaListe() {
        try {
            StackPane contentArea = (StackPane) btnEnregistrer.getScene().lookup("#contentArea");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantListView.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    /**
     * Remplit le formulaire si on veut modifier un étudiant existant.
     */
    public void setEtudiant(Etudiant e) {
        etudiantModif = e;
        if (e != null) {
            txtNom.setText(e.getNomE());
            txtPrenom.setText(e.getPrenomE());
            // en mettant la mention, le Listener va automatiquement filtrer les parcours
            comboMention.setValue(e.getParcours().getMention());
            comboParcours.setValue(e.getParcours());
            comboSemestre.setValue(e.getSemestreCourant());
        }
    }
}