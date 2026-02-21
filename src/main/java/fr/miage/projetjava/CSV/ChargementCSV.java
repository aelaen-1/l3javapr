package fr.miage.projetjava.CSV;

import java.io.BufferedReader; // Lecteur efficace pour lire des fichiers ligne par ligne
import java.io.FileReader; // Lecteur brut de fichiers texte
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe ChargementCSV : Gère la lecture et le parsing de fichiers CSV
 * Une seule méthode : List<List<String>> chargementFichierCSV
 * Cette classe est responsable de :
 * 1. Ouvrir les fichiers CSV
 * 2. Lire chaque ligne du fichier
 * 3. Parser les données (séparées par des virgules)
 * 4. Retourner les données sous forme de liste de listes de String
 * 
 * Les fichiers CSV doivent être au format :
 * colonne1,colonne2,colonne3,colonne4
 * 
 */
public class ChargementCSV {

    /**
     * Méthode appelée depuis les controllers quand on veut charger un fichier CSV
     * 
     * @param fichierCSV : le chemin COMPLET vers le fichier CSV
     *                   Exemple : "data/etudiants.csv" ou "/Users/.../file.csv"
     * @return : Une liste de listes contenant toutes les lignes CSV parsées
     *         Chaque sous-liste représente une ligne (colonne1, colonne2, ...)
     */
    public List<List<String>> chargementFichierCSV(String fichierCSV) {

        List<List<String>> donneeCSV = new ArrayList<>();
        //ouvre le fichier CSV grâce à BufferedReader
        try (BufferedReader br = new BufferedReader(new FileReader(fichierCSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] valeur = line.split(",");
                List<String> lineData = Arrays.asList(valeur);
                donneeCSV.add(lineData);
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        return donneeCSV;
    }
}
