package fr.miage.projetjava.controller;

// ==================== IMPORTS POUR LES COLLECTIONS STANDARD ====================
import java.util.List; // Interface List du JDK standard

// ==================== IMPORTS POUR LES MODÈLES ====================
import fr.miage.projetjava.CSV.EtudiantCSV; // Classe pour charger les étudiants depuis un CSV
import fr.miage.projetjava.model.Etudiant; // Classe représentant un étudiant
// ==================== IMPORTS POUR JAVAFX ====================
import javafx.collections.FXCollections; // Utilitaires pour créer des ObservableList
import javafx.collections.ObservableList; // Liste observable (détecte les changements)
import javafx.fxml.FXML; // Annotation pour lier les éléments FXML au controller
import javafx.scene.control.ListView; // Composant affichant une liste d'éléments

/**
 * ControllerVisualisationDonneeCSV : Contrôleur pour afficher la liste des
 * étudiants
 * 
 * Son rôle :
 * - Charger les données des étudiants depuis un fichier CSV
 * - Les afficher dans une ListView (composant d'affichage de liste)
 * - Permettre à l'utilisateur de consulter la liste des étudiants
 * 
 * Liaison avec FXML :
 * - Ce controller est lié au fichier visualisationDonneeCSV.fxml
 * - La ListView du FXML a l'attribut fx:id="listeEtudiants"
 * - Elle est liée à la variable @FXML private ListView<Etudiant> listeEtudiants
 * 
 * Flux d'exécution :
 * 1. L'utilisateur clique un bouton qui change la scène vers
 * visualisationDonneeCSV.fxml
 * 2. Le fichier FXML est chargé et ce controller est instancié
 * 3. initialize() est appelée automatiquement
 * 4. initialize() charge les étudiants et les affiche dans la ListView
 * 5. L'utilisateur voit la liste des étudiants à l'écran
 */
public class ControllerVisualisationDonneeCSV {

    // ==================== VARIABLES FXML ====================
    /**
     * ListView : composant JavaFX pour afficher une liste d'éléments
     * 
     * Propriétés :
     * - @FXML : indique que c'est lié à un élément du fichier FXML
     * - private : visible uniquement dans cette classe
     * - ListView<Etudiant> : une liste affichable contenant des objets Etudiant
     * 
     * Lien FXML :
     * - Cette variable correspond au composant FXML avec fx:id="listeEtudiants"
     * - Le nom DOIT être exactement le même (case-sensitive)
     * 
     * Utilité :
     * - Affiche les étudiants à l'écran sous forme de liste scrollable
     * - Chaque élément de la liste est un objet Etudiant
     */
    @FXML
    private ListView<Etudiant> listeEtudiants;

    // ==================== MÉTHODE DE CYCLE DE VIE ====================
    /**
     * Méthode initialize() : Appelée automatiquement par JavaFX après le chargement
     * du FXML
     * 
     * Quand est-elle appelée ?
     * - Après que le fichier FXML a été chargé
     * - Après que tous les éléments du FXML ont été créés
     * - Après que toutes les variables @FXML ont été initialisées
     * - AVANT que la fenêtre n'apparaisse à l'écran
     * 
     * Qu'est-ce qu'elle fait ?
     * - Initialise les composants GUI (couleurs, styles, valeurs, etc.)
     * - Charge les données (depuis fichiers, base de données, CSV, etc.)
     * - Configure les écouteurs d'événements (listeners)
     * - Prépare l'interface avant affichage
     * 
     * Ici, elle :
     * 1. Charge les étudiants depuis un fichier CSV
     * 2. Convertit la liste en ObservableList
     * 3. Affiche les étudiants dans la ListView
     */
    @FXML
    public void initialize() {
        // ========== ÉTAPE 1 : Créer une instance de la classe de chargement CSV
        // ==========
        // EtudiantCSV : classe qui gère le chargement des étudiants depuis un CSV
        // new EtudiantCSV() : crée une nouvelle instance prête à charger les données
        EtudiantCSV donneCSV = new EtudiantCSV();

        // ========== ÉTAPE 2 : Charger les étudiants depuis le fichier CSV ==========
        // donneCSV.ChargerEtudiant() : méthode qui :
        // 1. Ouvre le fichier CSV
        // 2. Parse chaque ligne
        // 3. Crée un objet Etudiant pour chaque ligne
        // 4. Retourne une List<Etudiant> contenant tous les étudiants
        // "src/main/resources/etudiant.csv" : chemin du fichier à charger
        // Résultat : une liste de tous les étudiants lus depuis le CSV
        List<Etudiant> listeEtudiant = donneCSV.ChargerEtudiant("src/main/resources/etudiant.csv");

        // ========== ÉTAPE 3 : Convertir la List en ObservableList ==========
        // Pourquoi ObservableList ?
        // - List : collection standard Java, statique (pas d'observation des
        // changements)
        // - ObservableList : collection JavaFX qui NOTIFIE les changements
        // - ListView : composant JavaFX qui peut ÉCOUTER les changements
        // d'ObservableList
        // - Avantage : si on ajoute/supprime un Etudiant de l'ObservableList,
        // la ListView se met à jour AUTOMATIQUEMENT à l'écran
        // FXCollections.observableList() : méthode utilitaire qui crée une
        // ObservableList
        // depuis une List standard
        // Résultat : une liste observable contenant les mêmes étudiants
        ObservableList<Etudiant> listeObservableEtudiant = FXCollections.observableList(listeEtudiant);

        // ========== ÉTAPE 4 : Afficher les étudiants dans la ListView ==========
        // listeEtudiants.setItems() : méthode de ListView qui définit le contenu à
        // afficher
        // listeObservableEtudiant : l'ObservableList contenant les étudiants
        // Résultat : chaque Etudiant apparaît comme une ligne dans la ListView à
        // l'écran
        // Chaque ligne affichera le résultat de Etudiant.toString()
        listeEtudiants.setItems(listeObservableEtudiant);
    }
}
