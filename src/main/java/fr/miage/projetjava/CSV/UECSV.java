package fr.miage.projetjava.CSV;

import java.util.ArrayList; // Liste dynamique pour stocker les UE
import java.util.Arrays; // Utilitaires pour manipuler les tableaux
import java.util.List; // Interface pour les listes

import fr.miage.projetjava.model.Mention;
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

    private Mention stringToMention(String mention) {

        if (mention.equals(Mention.MIASHS.toString()))
            return Mention.MIASHS;
        else if (mention.equals(Mention.BIOLOGIE.toString()))
            return Mention.BIOLOGIE;
        else if (mention.equals(Mention.CHIMIE.toString()))
            return Mention.CHIMIE;
        else if (mention.equals(Mention.INFORMATIQUE.toString()))
            return Mention.INFORMATIQUE;
        else
            return Mention.PHYSIQUE; // si rien d'autre
    }

    /**
     * Ajoute les UE pré-requises à une UE
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
        String[] tabStringUEPrerequis = stringUEprerequis.split(",");
        // ========== ÉTAPE 2 : Convertir le tableau en liste ==========
        List<String> listUEPrerequis = Arrays.asList(tabStringUEPrerequis);

        // ========== ÉTAPE 3 : Boucle double pour trouver et ajouter les pré-requis
        // Pour chaque élément de listUEPrerequis, on vérifie s'il existe et on l'ajoute à l'UE via setUEprerequis
        for (int i = 0; i < listUEPrerequis.size(); i++) {

            for (int j = 0; j < listeUE.size(); j++) {
                if (listeUE.get(j).getIntitule().equals(listUEPrerequis.get(i)))
                    ue.setUEprerequis(listeUE.get(j));
            }
        }
    }

    /**
     * Méthode PUBLIQUE : Point d'entrée pour charger et transformer un fichier CSV
     * en liste d'UE
     * 
     * Cette méthode :
     * 1. Charge le fichier CSV via ChargementCSV
     * 2. Parse les données en (code, intitulé, crédits, mention, pré-requis)
     * même logique que la méthode List<Etudiant>
     * creationEtudiants(List<List<String>> donneesCSV)
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
        ChargementCSV chargerFichierCSV = new ChargementCSV();

        // ========== ÉTAPE 2 : Charger et parser le fichier CSV ==========
        List<List<String>> donneesUECSV = chargerFichierCSV.chargementFichierCSV(cheminFichier);
        List<UE> listeUE = new ArrayList<>();

        for (int i = 1; i < donneesUECSV.size(); i++) {
            String code = donneesUECSV.get(i).get(0);
            String intitule = donneesUECSV.get(i).get(1);
            int ects = Integer.parseInt(donneesUECSV.get(i).get(2));
            Mention mention = stringToMention(donneesUECSV.get(i).get(3));
            String stringUEprerequis = donneesUECSV.get(i).get(4);
            UE ue = new UE(code, intitule, ects, mention);
            if (!stringUEprerequis.equals("Aucun")) // s'il y a des prérequis
                ajoutPreRequisUE(stringUEprerequis, listeUE, ue);
            listeUE.add(ue);
        }

        return listeUE;
    }

    /**
     * Méthode main : Permet de tester la classe UECSV
     * C'est une méthode de test, elle n'est pas utilisée en production
     * 
     * @param arg : arguments en ligne de commande (inutilisés)
     */
    public static void main(String[] arg) {

        UECSV ue = new UECSV();

        // ========== ÉTAPE 2 : Charger les UE du fichier CSV ==========
        List<UE> listeUE = ue.chargerUE("src/main/resources/ue.csv");

        // ========== ÉTAPE 3 : Afficher toutes les UE chargées ==========
        for (int i = 0; i < listeUE.size(); i++) {
            System.out.println(listeUE.get(i));
        }
    }
}
