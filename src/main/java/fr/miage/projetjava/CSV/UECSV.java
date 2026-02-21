package fr.miage.projetjava.CSV;

// Import statique pour raccourcir le code
import static java.lang.Integer.parseInt; // Raccourci pour Integer.parseInt()

import java.util.ArrayList; // Liste dynamique pour stocker les UE
import java.util.Arrays; // Utilitaires pour manipuler les tableaux
import java.util.List; // Interface pour les listes

import fr.miage.projetjava.model.Mention; // Énumération des mentions (MIASHS, BIOLOGIE, etc.)
import fr.miage.projetjava.model.UE; // Classe représentant une Unité d'Enseignement

/**
 * Classe UECSV : Gère la création d'objets UE (Unité d'Enseignement) à partir
 * de données CSV
 * 
 * Flux de traitement :
 * 1. Charger le fichier CSV via ChargementCSV
 * 2. Parser chaque ligne pour extraire : code, intitulé, crédits, mention,
 * pré-requis
 * 3. Convertir la mention de String à enum Mention
 * 4. Créer un objet UE avec ces informations
 * 5. Ajouter les pré-requis (qui doivent déjà exister dans la liste)
 * 6. Retourner la liste complète d'UE
 * 
 * Format du fichier CSV attendu :
 * code,intitule,credit,mention,prerequis
 * UE1,Mathématiques,6,MIASHS,Aucun
 * UE2,Physique,6,PHYSIQUE,UE1
 * UE3,Chimie,6,CHIMIE,UE1,UE2
 */
public class UECSV {

    /**
     * Méthode PRIVÉE : Convertit une chaîne de caractères en enum Mention
     * 
     * Les données CSV contiennent les mentions sous forme de String (ex: "MIASHS")
     * Cette méthode les transforme en enum Mention pour pouvoir créer les UE
     * 
     * @param mention : le nom de la mention sous forme de String
     *                Peut être : "MIASHS", "BIOLOGIE", "CHIMIE", "INFORMATIQUE",
     *                "PHYSIQUE"
     * @return : l'enum Mention correspondant
     *         Par défaut retourne PHYSIQUE si la mention n'est pas reconnue
     */
    private Mention passageStringToMention(String mention) {
        // ========== ÉTAPE 1 : Comparer la String avec chaque enum Mention ==========
        // .toString() convertit l'enum en String pour faire la comparaison

        if (mention.equals(Mention.MIASHS.toString())) {
            // Si c'est MIASHS
            // (Mathématiques-Informatique-Analyse-Statistique-Histoire-Société)
            return Mention.MIASHS;
        } else if (mention.equals(Mention.BIOLOGIE.toString())) {
            // Si c'est BIOLOGIE
            return Mention.BIOLOGIE;
        } else if (mention.equals(Mention.CHIMIE.toString())) {
            // Si c'est CHIMIE
            return Mention.CHIMIE;
        } else if (mention.equals(Mention.INFORMATIQUE.toString())) {
            // Si c'est INFORMATIQUE
            return Mention.INFORMATIQUE;
        } else {
            // Par défaut, si la mention n'est pas reconnue, on retourne PHYSIQUE
            return Mention.PHYSIQUE;
        }
    }

    /**
     * Méthode PRIVÉE : Ajoute les UE pré-requises à une UE
     * 
     * Les données CSV contiennent les pré-requis sous forme de chaîne séparée par
     * des virgules
     * Cette méthode :
     * 1. Parse cette chaîne en liste d'UE
     * 2. Cherche chaque UE dans la liste existante
     * 3. Si trouvée, l'ajoute comme pré-requis de l'UE courante
     * 
     * @param stringUEprerequis : chaîne contenant les codes des UE pré-requises
     *                          séparées par des virgules
     *                          Exemple : "UE1,UE2,UE3" ou "Aucun"
     * @param listeUE           : la liste des UE déjà créées (pour chercher les
     *                          pré-requis)
     * @param ue                : l'UE courante à laquelle on ajoute les pré-requis
     */
    private void ajoutPreRequisUE(String stringUEprerequis, List<UE> listeUE, UE ue) {
        // ========== ÉTAPE 1 : Parser la chaîne des pré-requis ==========
        // Exemple : stringUEprerequis = "UE1,UE2,UE3"
        // split(",") divise à chaque virgule
        // Résultat : ["UE1", "UE2", "UE3"]
        String[] tabStringUEPrerequis = stringUEprerequis.split(",");

        // ========== ÉTAPE 2 : Convertir le tableau en liste ==========
        // Arrays.asList() transforme un tableau en List pour plus de flexibilité
        List<String> listeStringUECSV = Arrays.asList(tabStringUEPrerequis);

        // ========== ÉTAPE 3 : Boucle double pour trouver et ajouter les pré-requis
        // ==========
        // Boucle externe : on itère sur chaque UE pré-requise du CSV
        for (int i = 0; i < listeStringUECSV.size(); i++) {

            // Boucle interne : on itère sur chaque UE déjà existante
            for (int j = 0; j < listeUE.size(); j++) {

                // ========== ÉTAPE 4 : Vérifier si l'UE pré-requise existe ==========
                // On compare les intitulés (identifiants uniques)
                // Si on trouve une correspondance, on ajoute l'UE pré-requise
                if (listeUE.get(j).getIntitule().equals(listeStringUECSV.get(i))) {
                    // Appel de la méthode setUEprerequis() de la classe UE
                    // pour ajouter l'UE pré-requise à l'ArrayList des pré-requis
                    ue.setUEprerequis(listeUE.get(j));
                }
            }
        }
    }

    /**
     * Méthode PRIVÉE : Transforme les données CSV en liste d'objets UE
     * 
     * Cette méthode itère sur chaque ligne du CSV (sauf la première = header)
     * et crée une UE pour chaque ligne
     * 
     * @param data : liste de listes contenant les données du CSV
     *             Chaque sous-liste est une ligne, chaque élément est une colonne
     *             Structure : [[code, intitule, credit, mention, prerequis], [...],
     *             ...]
     * @return : une liste d'objets UE créées à partir des données du CSV
     */
    private List<UE> creationUE(List<List<String>> data) {
        // ========== ÉTAPE 1 : Initialiser la liste d'UE ==========
        // Cette liste stockera toutes les UE créées
        List<UE> listeUE = new ArrayList<>();

        // ========== ÉTAPE 2 : Boucler sur chaque ligne du CSV ==========
        // On commence à i=0 et on s'arrête avant la dernière ligne (i < data.size()-1)
        // Cela permet d'ignorer la première ligne du CSV (l'en-tête)
        for (int i = 0; i < data.size() - 1; i++) {

            // ========== ÉTAPE 3 : Extraire les informations de la ligne courante
            // ==========
            // data.get(i+1) récupère la ligne actuelle
            // .get(0), .get(1), etc. récupèrent chaque colonne

            // Colonne 0 : Code de l'UE (sigle unique, ex: "UE1")
            String code = data.get(i + 1).get(0);

            // Colonne 1 : Intitulé ou nom complet de l'UE (ex: "Mathématiques")
            String intitule = data.get(i + 1).get(1);

            // Colonne 2 : Nombre de crédits ECTS (nombre entier)
            int credit = parseInt(data.get(i + 1).get(2));

            // ========== ÉTAPE 4 : Convertir la mention de String à enum ==========
            // On récupère la mention qui vient sous forme de String dans le CSV
            // et on la convertit en enum Mention pour la stocker correctement
            Mention mention = passageStringToMention(data.get(i + 1).get(3));

            // ========== ÉTAPE 5 : Récupérer la chaîne des pré-requis ==========
            // Colonne 4 : les pré-requis sous forme de chaîne (ex: "UE1,UE2" ou "Aucun")
            String stringUEprerequis = data.get(i + 1).get(4);

            // ========== ÉTAPE 6 : Créer l'objet UE ==========
            // Construction d'une nouvelle UE avec code, intitulé, crédits et mention
            UE ue = new UE(code, intitule, credit, mention);

            // ========== ÉTAPE 7 : Ajouter les pré-requis si nécessaire ==========
            // Si la colonne des pré-requis n'est pas "Aucun", on ajoute les dépendances
            if (!stringUEprerequis.equals("Aucun")) {
                // Appelle la méthode pour parser et ajouter les pré-requis
                ajoutPreRequisUE(stringUEprerequis, listeUE, ue);
            }

            // ========== ÉTAPE 8 : Ajouter l'UE à la liste ==========
            // L'UE est maintenant complète (avec ses pré-requis si applicable)
            // et est stockée dans listeUE
            listeUE.add(ue);
        }

        // ========== ÉTAPE 9 : Retourner la liste complète ==========
        return listeUE;
    }

    /**
     * Méthode PUBLIQUE : Point d'entrée pour charger et transformer un fichier CSV
     * en liste d'UE
     * 
     * Cette méthode :
     * 1. Charge le fichier CSV via ChargementCSV
     * 2. Parse les données en (code, intitulé, crédits, mention, pré-requis)
     * 3. Crée un objet UE pour chaque ligne
     * 4. Gère les dépendances entre UE
     * 5. Retourne la liste complète d'UE
     * 
     * Elle est appelée depuis les controllers pour charger les données
     * 
     * @param cheminFichier : le chemin COMPLET vers le fichier CSV
     *                      Exemple : "data/ue.csv"
     * @return : une liste d'objets UE créées à partir du fichier CSV
     *         Retourne une liste vide si le fichier n'existe pas ou erreur
     */
    public List<UE> chargerUE(String cheminFichier) {
        // ========== ÉTAPE 1 : Initialiser le chargeur CSV ==========
        // ChargementCSV va s'occuper de lire le fichier brut
        ChargementCSV chargerFichierCSV = new ChargementCSV();

        // ========== ÉTAPE 2 : Charger et parser le fichier CSV ==========
        // chargementFichierCSV() retourne une liste de listes de String
        // chaque sous-liste est une ligne, chaque élément est une colonne
        List<List<String>> donneeUECSV = chargerFichierCSV.chargementFichierCSV(cheminFichier);

        // ========== ÉTAPE 3 : Transformer les données en objets UE ==========
        // Initialiser la variable pour stocker les résultats
        List<UE> listeUE;

        // Appeler creationUE() pour transformer les données CSV en objets
        listeUE = creationUE(donneeUECSV);

        // ========== ÉTAPE 4 : Retourner la liste d'UE ==========
        // Cette liste contient maintenant toutes les UE du fichier CSV
        // avec leurs dépendances établies
        return listeUE;
    }

    /**
     * Méthode main : Permet de tester la classe UECSV
     * 
     * Cette méthode charge les UE depuis le fichier CSV et les affiche
     * C'est une méthode de test, elle n'est pas utilisée en production
     * 
     * @param agr : arguments en ligne de commande (inutilisés)
     */
    public static void main(String[] agr) {
        // ========== ÉTAPE 1 : Créer une instance d'UECSV ==========
        UECSV ue = new UECSV();

        // ========== ÉTAPE 2 : Charger les UE du fichier CSV ==========
        // Appelle chargerUE() avec le chemin du fichier CSV
        List<UE> listeUE = ue.chargerUE("src/main/resources/ue.csv");

        // ========== ÉTAPE 3 : Afficher toutes les UE chargées ==========
        // Boucle sur chaque UE et l'affiche
        // Cela permet de vérifier que les données ont été bien chargées
        for (int i = 0; i < listeUE.size(); i++) {
            System.out.println(listeUE.get(i));
        }
    }
}
