package fr.miage.projetjava.controller;
import fr.miage.projetjava.CSV.ChargementCSV;
import fr.miage.projetjava.model.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.util.List;


public class ControllerVisualisationDonneeCSV {
    //le nom (listeEtudiants) doit être le même que le fx:id de ListView<Etudiant> du fichier fxml (visualisationDonneeCSV)
    @FXML private ListView<Etudiant> listeEtudiants;

    //executé avant que la fenêtre ne soit affiché à l'écran mais après que les composants existent en mémoire, elle sert à préparer l'interface
    //sert à initialiser les composants (avec des new), ajouter des listeners, configurer l'état intial de certains
    // éléments, configrer certains éléments de TableView ou ListView etc....
    @FXML
    public void initialize()
    {

        // Charge et affiche les données CSV
        ChargementCSV donneCSV = new ChargementCSV();

        //on va appeler la méthode chargementFichierCSV qui va appeler les deux méthodes pour lire et récuperer les données du fichier csv
        // et qui va créer les objets étudiants pour les mettre dans une liste
        List<Etudiant> listeEtudiant = donneCSV.chargementFichierCSV("src/main/resources/etudiant.csv");

        //ObservableList est une interface de javafx, du même style que List mais ListView (ou TableView) peut voir si ya des
        // changements et les prendre en compte
        ObservableList<Etudiant> listeObservableEtudiant = FXCollections.observableList(listeEtudiant);

        //va mettre les informations de listeEtudiants (transformer en liste observable) dans la ListView du fichier fxml
        listeEtudiants.setItems(listeObservableEtudiant);
    }
}
