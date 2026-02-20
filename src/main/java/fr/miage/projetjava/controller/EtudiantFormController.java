package fr.miage.projetjava.controller;

import fr.miage.projetjava.dao.*;
import fr.miage.projetjava.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EtudiantFormController {

    @FXML private TextField txtNom, txtPrenom;
    @FXML private ComboBox<Mention> comboMention;
    @FXML private ComboBox<Parcours> comboParcours;
    @FXML private ComboBox<Semestre> comboSemestre;
    @FXML private Button btnEnregistrer;

    private List<Parcours> tousLesParcours;
    private List<UE> toutesLesUE;
    private Etudiant etudiantModif;

    @FXML
    public void initialize() {
        // 1. Chargement des données
        this.toutesLesUE = new UEDAO().chargerUEs();
        this.tousLesParcours = new ParcoursDAO().chargerParcours(toutesLesUE);

        // 2. Configuration des listes de base
        comboMention.setItems(FXCollections.observableArrayList(Mention.values()));
        comboSemestre.setItems(FXCollections.observableArrayList(Semestre.values()));

        // 3. CONVERTISSEUR POUR MENTION (Évite les adresses mémoire)
        comboMention.setConverter(new StringConverter<Mention>() {
            @Override
            public String toString(Mention m) {
                return (m == null) ? "" : m.toString(); // Utilise le toString() de ton Enum Mention
            }
            @Override
            public Mention fromString(String s) { return null; }
        });

        // 4. CONVERTISSEUR POUR PARCOURS (Affiche le nom du parcours)
        comboParcours.setConverter(new StringConverter<Parcours>() {
            @Override
            public String toString(Parcours p) {
                return (p == null) ? "" : p.getNom(); // Affiche uniquement le nom
            }
            @Override
            public Parcours fromString(String s) { return null; }
        });

        // 5. Logique de filtrage Mention -> Parcours
        comboMention.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<Parcours> filtres = tousLesParcours.stream()
                        .filter(p -> p.getMention() == newVal)
                        .collect(Collectors.toList());

                comboParcours.setItems(FXCollections.observableArrayList(filtres));
                comboParcours.setDisable(false);
                comboParcours.setPromptText("Choisir un parcours");
            } else {
                comboParcours.setDisable(true);
                comboParcours.getItems().clear();
            }
        });
    }

    @FXML
    private void handleEnregistrer() {
        try {
            // Vérification de sécurité
            if (comboParcours.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez choisir un parcours !").show();
                return;
            }

            EtudiantDAO dao = new EtudiantDAO();
            List<Etudiant> liste = dao.chargerTout(tousLesParcours, toutesLesUE);

            if (etudiantModif == null) {
                // Mode Création
                int nouvelId = liste.stream().mapToInt(Etudiant::getNumE).max().orElse(1000) + 1;
                Etudiant nouvelEtu = new Etudiant(nouvelId, txtNom.getText(), txtPrenom.getText(),
                        comboParcours.getValue(), comboSemestre.getValue());
                liste.add(nouvelEtu);
            } else {
                // Mode Modification
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
            retournerALaListe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        retournerALaListe();
    }

    private void retournerALaListe() {
        try {
            StackPane contentArea = (StackPane) btnEnregistrer.getScene().lookup("#contentArea");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EtudiantListView.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEtudiant(Etudiant e) {
        this.etudiantModif = e;
        if (e != null) {
            txtNom.setText(e.getNomE());
            txtPrenom.setText(e.getPrenomE());

            // On sélectionne la mention, ce qui va peupler la liste des parcours via le Listener
            comboMention.setValue(e.getParcours().getMention());
            // Puis on sélectionne le parcours
            comboParcours.setValue(e.getParcours());

            comboSemestre.setValue(e.getSemestreCourant());
        }
    }
}