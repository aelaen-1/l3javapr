package fr.miage.projetjava.CSV;

import java.util.ArrayList;
import java.util.List;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;

/**
 * Classe EtudiantCSV : Gère la création d'objets Étudiant à partir de données CSV
 * 
 * Flux de traitement :
 * 1. Charger le fichier CSV via ChargementCSV
 * 2. Parser chaque ligne pour extraire : id, prénom, nom, parcours
 * 3. Créer un objet Parcours correspondant au nom donné
 * 4. Créer un objet Étudiant avec ces informations
 * 5. Retourner une liste d'étudiants
 * 
 */
public class EtudiantCSV {

    // Méthode PRIVÉE : Crée un objet Parcours selon le nom fourni
    private Parcours creerParcours(String nomParcours) {
        Parcours parcours;

        if (nomParcours.equals("IO") || nomParcours.equals("MIAGE")) {
            parcours = new Parcours(nomParcours, Mention.MIASHS);
        } else if (nomParcours.equals("Biologie")) {
            parcours = new Parcours(nomParcours, Mention.BIOLOGIE);
        } else if (nomParcours.equals("Chimie")) {
            parcours = new Parcours(nomParcours, Mention.CHIMIE);
        } else if (nomParcours.equals("Informatique")) {
            parcours = new Parcours(nomParcours, Mention.INFORMATIQUE);
        } else {
            parcours = new Parcours(nomParcours, Mention.PHYSIQUE);
        }
        return parcours;
    }

    /**
     * Méthode PRIVÉE : Transforme les données CSV en liste d'objets Étudiant
     * 
     * Cette méthode itère sur chaque ligne du CSV (sauf la première = header)
     * et crée un Étudiant pour chaque ligne
     * 
     * @param donnees CSV : liste de listes contenant les données du CSV
     * @return : une liste d'objets Étudiant créés à partir des données du CSV
     */
    private List<Etudiant> creationEtudiants(List<List<String>> donneesCSV) {
        List<Etudiant> listeEtudiants = new ArrayList<>();

        // On saute l'index 0 car c'est la première du CSV (l'en-tête :
        // "id,prenom,nom,parcours")
        for (int i = 1; i < donneesCSV.size(); i++) {
            // .get(0), .get(1), etc. récupèrent chaque colonne de la ligne
            int id = Integer.parseInt(donneesCSV.get(i).get(0));
            String prenomE = donneesCSV.get(i).get(1);
            String nomE = donneesCSV.get(i).get(2);
            String nomParcours = donneesCSV.get(i).get(3);
            Parcours parcours = creerParcours(nomParcours);
            Etudiant e = new Etudiant(id, nomE, prenomE, parcours);
            listeEtudiants.add(e);
        }
        return listeEtudiants;
    }

    /**
     * Point d'entrée pour charger et transformer un fichier CSV
     * en liste d'Étudiants
     * Elle est appellée depuis les controllers pour charger les données
     * 
     * @param cheminFichier : le chemin COMPLET vers le fichier CSV
     *                      Exemple : "data/etudiants.csv"
     * @return : une liste d'objets Étudiant créés à partir du fichier CSV
     *         Retourne une liste vide si le fichier n'existe pas ou erreur
     */
    public List<Etudiant> ChargerEtudiant(String cheminFichier) {

        ChargementCSV chargerFichierCSV = new ChargementCSV();
        List<List<String>> donneesCSV = chargerFichierCSV.chargementFichierCSV(cheminFichier);
        return creationEtudiants(donneesCSV);
    }
}