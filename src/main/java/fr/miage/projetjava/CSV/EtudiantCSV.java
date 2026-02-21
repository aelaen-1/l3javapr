package fr.miage.projetjava.CSV;

// Import statique pour raccourcir le code
import static java.lang.Integer.parseInt; // Raccourci pour Integer.parseInt()

import java.util.ArrayList; // Liste dynamique pour stocker les étudiants
import java.util.List; // Interface pour les listes

// ==================== IMPORTS POUR LES MODÈLES ====================
import fr.miage.projetjava.model.Etudiant; // Classe représentant un étudiant
import fr.miage.projetjava.model.Mention; // Énumération des mentions (MIASHS, BIOLOGIE, etc.)
import fr.miage.projetjava.model.Parcours; // Classe représentant un parcours d'études

/**
 * Classe EtudiantCSV : Gère la création d'objets Étudiant à partir de données
 * CSV
 * 
 * Flux de traitement :
 * 1. Charger le fichier CSV via ChargementCSV
 * 2. Parser chaque ligne pour extraire : id, prénom, nom, parcours
 * 3. Créer un objet Parcours correspondant au nom donné
 * 4. Créer un objet Étudiant avec ces informations
 * 5. Retourner une liste d'étudiants
 * 
 * Format du fichier CSV attendu :
 * id,prenom,nom,parcours
 * 1,Jean,Dupont,IO
 * 2,Marie,Martin,Biologie
 * 3,Pierre,Bernard,Chimie
 */
public class EtudiantCSV {

    /**
     * Méthode PRIVÉE : Crée un objet Parcours selon le nom fourni
     * 
     * Cette méthode mappe les noms de parcours textes aux enums Mention
     * 
     * @param nomParcours : le nom du parcours (ex: "IO", "Biologie", "Chimie",
     *                    etc.)
     * @return : un nouvel objet Parcours avec le nom et la mention correspondante
     */
    private Parcours creerParcours(String nomParcours) {
        // ========== ÉTAPE 1 : Initialiser la variable ==========
        Parcours parcours;

        // ========== ÉTAPE 2 : Déterminer la mention selon le parcours ==========
        // Chaque branche d'études a une mention associée
        if (nomParcours.equals("IO") || nomParcours.equals("MIAGE")) {
            // Pour les parcours informatiques, la mention est MIASHS
            parcours = new Parcours(nomParcours, Mention.MIASHS);
        } else if (nomParcours.equals("Biologie")) {
            // Pour la biologie
            parcours = new Parcours(nomParcours, Mention.BIOLOGIE);
        } else if (nomParcours.equals("Chimie")) {
            // Pour la chimie
            parcours = new Parcours(nomParcours, Mention.CHIMIE);
        } else if (nomParcours.equals("Informatique")) {
            // Pour l'informatique pure
            parcours = new Parcours(nomParcours, Mention.INFORMATIQUE);
        } else {
            // Par défaut, si le parcours n'est pas reconnu, on met PHYSIQUE
            parcours = new Parcours(nomParcours, Mention.PHYSIQUE);
        }

        // ========== ÉTAPE 3 : Retourner le parcours créé ==========
        return parcours;
    }

    /**
     * Méthode PRIVÉE : Transforme les données CSV en liste d'objets Étudiant
     * 
     * Cette méthode itère sur chaque ligne du CSV (sauf la première = header)
     * et crée un Étudiant pour chaque ligne
     * 
     * @param data : liste de listes contenant les données du CSV
     *             Chaque sous-liste est une ligne, chaque élément est une colonne
     *             Structure : [[id, prenom, nom, parcours], [...], ...]
     * @return : une liste d'objets Étudiant créés à partir des données du CSV
     */
    private List<Etudiant> creationEtudiant(List<List<String>> data) {
        // ========== ÉTAPE 1 : Initialiser la liste d'étudiants ==========
        // Cette liste stockera tous les étudiants créés
        List<Etudiant> listeEtudiants = new ArrayList<>();

        // ========== ÉTAPE 2 : Boucler sur chaque ligne du CSV ==========
        // On commence à i=0 et on s'arrête avant la dernière ligne (i < data.size()-1)
        // Cela permet d'ignorer la première ligne du CSV (l'en-tête :
        // "id,prenom,nom,parcours")
        for (int i = 0; i < data.size() - 1; i++) {

            // ========== ÉTAPE 3 : Extraire les informations de la ligne courante
            // ==========
            // data.get(i+1) récupère la ligne actuelle (on commence à i+1 car on ignore
            // header)
            // .get(0), .get(1), etc. récupèrent chaque colonne de la ligne

            // Colonne 0 : ID (nombre entier)
            int id = parseInt(data.get(i + 1).get(0));

            // Colonne 1 : Prénom (texte)
            String prenomE = data.get(i + 1).get(1);

            // Colonne 2 : Nom (texte)
            String nomE = data.get(i + 1).get(2);

            // Colonne 3 : Nom du parcours (texte)
            String nomParcours = data.get(i + 1).get(3);

            // ========== ÉTAPE 4 : Créer l'objet Parcours ==========
            // Appelle creerParcours() pour obtenir un objet Parcours avec la bonne mention
            Parcours parcours = creerParcours(nomParcours);

            // ========== ÉTAPE 5 : Créer l'objet Étudiant ==========
            // Construit un nouvel Étudiant avec toutes les informations extraites
            Etudiant e = new Etudiant(id, nomE, prenomE, parcours);

            // ========== ÉTAPE 6 : Ajouter l'étudiant à la liste ==========
            // L'étudiant est maintenant stocké dans listeEtudiants
            listeEtudiants.add(e);
        }

        // ========== ÉTAPE 7 : Retourner la liste complète ==========
        // listeEtudiants contient maintenant tous les étudiants du fichier CSV
        return listeEtudiants;
    }

    /**
     * Méthode PUBLIQUE : Point d'entrée pour charger et transformer un fichier CSV
     * en liste d'Étudiants
     * 
     * Cette méthode :
     * 1. Charge le fichier CSV via ChargementCSV
     * 2. Parse les données en (id, prenom, nom, parcours)
     * 3. Crée un objet Étudiant pour chaque ligne
     * 4. Retourne la liste complète d'étudiants
     * 
     * Elle est appellée depuis les controllers pour charger les données
     * 
     * @param cheminFichier : le chemin COMPLET vers le fichier CSV
     *                      Exemple : "data/etudiants.csv"
     * @return : une liste d'objets Étudiant créés à partir du fichier CSV
     *         Retourne une liste vide si le fichier n'existe pas ou erreur
     */
    public List<Etudiant> ChargerEtudiant(String cheminFichier) {
        // ========== ÉTAPE 1 : Initialiser le chargeur CSV ==========
        // ChargementCSV va s'occuper de lire le fichier brut
        ChargementCSV chargerFichierCSV = new ChargementCSV();

        // ========== ÉTAPE 2 : Charger et parser le fichier CSV ==========
        // chargementFichierCSV() retourne une liste de listes de String
        // chaque sous-liste est une ligne, chaque élément est une colonne
        List<List<String>> donneeEtudiantCSV = chargerFichierCSV.chargementFichierCSV(cheminFichier);

        // ========== ÉTAPE 3 : Transformer les données en objets Étudiant ==========
        // On initialise la variable pour stocker les résultats
        List<Etudiant> listeEtudiants;

        // On appelle creationEtudiant() pour transformer les données CSV en objets
        listeEtudiants = creationEtudiant(donneeEtudiantCSV);

        // ========== ÉTAPE 4 : Retourner la liste d'étudiants ==========
        // Cette liste contient maintenant tous les Étudiants du fichier CSV
        // Prêts à être utilisés par l'application
        return listeEtudiants;
    }
}