package fr.miage.projetjava.CSV;

// ==================== IMPORTS POUR MANIPULER LES FICHIERS ====================
import java.io.BufferedReader; // Lecteur efficace pour lire des fichiers ligne par ligne
import java.io.FileReader; // Lecteur brut de fichiers texte
import java.io.IOException; // Exception levée si erreur d'accès au fichier
// ==================== IMPORTS POUR MANIPULER LES COLLECTIONS ====================
import java.util.ArrayList; // Liste dynamique (flexible et redimensionnable)
import java.util.Arrays; // Utilitaires pour manipuler les tableaux
import java.util.List; // Interface pour les listes (ArrayList l'implémente)

/**
 * Classe ChargementCSV : Gère la lecture et le parsing de fichiers CSV
 * 
 * Cette classe est responsable de :
 * 1. Ouvrir les fichiers CSV
 * 2. Lire chaque ligne du fichier
 * 3. Parser les données (séparées par des virgules)
 * 4. Retourner les données sous forme de liste de listes de String
 * 
 * Les fichiers CSV doivent être au format :
 * colonne1,colonne2,colonne3,colonne4
 * 
 * Exemple :
 * 1,Prise,Bracelet,IO
 * 2,Jean,Dupont,B
 */
public class ChargementCSV {

    /**
     * Méthode PRIVÉE : Lit un fichier CSV ligne par ligne et stocke les données
     * 
     * Cette méthode est appelée UNIQUEMENT par chargementFichierCSV()
     * Elle extrait chaque ligne et la divise en colonnes (séparées par des
     * virgules)
     * 
     * @param br : BufferedReader pointant vers le fichier CSV ouvert
     * @return : Une liste de listes : chaque ligne du CSV est une liste de String
     *         Exemple : [[1, Prise, Bracelet, IO], [2, Jean, Dupont, B], ...]
     */
    private List<List<String>> lectureStockInfoCSV(BufferedReader br) {
        // ========== ÉTAPE 1 : Initialiser la structure de données ==========
        // donneeCSV va stocker toutes les lignes du fichier CSV
        // Chaque élément est une ligne, chaque ligne est une liste de colonnes
        List<List<String>> donneeCSV = new ArrayList<>();

        // Déclaration de la variable qui contiendra chaque ligne lue du fichier
        String line;

        try {
            // ========== ÉTAPE 2 : Lire le fichier ligne par ligne ==========
            // br.readLine() lit une ligne complète du fichier, puis la stocke dans 'line'
            // Quand il n'y a plus de lignes, readLine() retourne null => boucle s'arrête
            while ((line = br.readLine()) != null) {

                // ========== ÉTAPE 3 : Parser la ligne CSV ==========
                // line exemple : "1,Prise,Bracelet,IO"
                // split(",") divise la chaîne à chaque virgule
                // Résultat : ["1", "Prise", "Bracelet", "IO"]
                String[] valeur = line.split(",");

                // ========== ÉTAPE 4 : Convertir le tableau en liste ==========
                // Arrays.asList() transforme un tableau de String en List de String
                // Plus facile à manipuler qu'un tableau brut
                List<String> lineData = Arrays.asList(valeur);

                // ========== ÉTAPE 5 : Ajouter la ligne aux données globales ==========
                // Chaque ligneData (liste) est ajoutée à donneeCSV (liste de listes)
                donneeCSV.add(lineData);
            }
        } catch (IOException e) {
            // Si un problème survient lors de la lecture (fichier corrompu, etc.)
            System.err.println("Error reading the CSV file: " + e.getMessage());
            e.printStackTrace();
        }

        // ========== ÉTAPE 6 : Retourner les données ==========
        // donneeCSV contient maintenant toutes les lignes du fichier CSV
        // Chaque ligne est parsée et prête à être utilisée
        return donneeCSV;
    }

    /**
     * Méthode PUBLIQUE : Point d'entrée pour charger un fichier CSV complet
     * 
     * Cette méthode :
     * 1. Ouvre le fichier CSV (via BufferedReader)
     * 2. Appelle lectureStockInfoCSV() pour lire et parser
     * 3. Retourne les données parsées
     * 
     * Elle est appelée depuis les controllers quand on veut charger un fichier CSV
     * 
     * @param fichierCSV : le chemin COMPLET vers le fichier CSV
     *                   Exemple : "data/etudiants.csv" ou "/Users/.../file.csv"
     * @return : Une liste de listes contenant toutes les lignes CSV parsées
     *         Chaque sous-liste représente une ligne (colonne1, colonne2, ...)
     */
    public List<List<String>> chargementFichierCSV(String fichierCSV) {
        // ========== ÉTAPE 1 : Initialiser la variable de retour ==========
        // Cette liste va stocker toutes les données du fichier CSV
        List<List<String>> donneeCSV = new ArrayList<>();

        // ========== ÉTAPE 2 : Ouvrir le fichier de manière sécurisée ==========
        // try-with-resources : ouvre le fichier et le ferme AUTOMATIQUEMENT
        // même si une exception est levée
        try (BufferedReader br = new BufferedReader(new FileReader(fichierCSV))) {

            // ========== ÉTAPE 3 : Lire et parser le fichier ==========
            // Appel de la méthode privée pour faire le travail réel
            // Elle retourne une liste de listes avec toutes les données
            donneeCSV = lectureStockInfoCSV(br);

        } catch (IOException e) {
            // Si le fichier n'existe pas, n'est pas accessible, ou erreur de lecture
            System.err.println("Error reading the CSV file: " + e.getMessage());
            e.printStackTrace();
        }

        // ========== ÉTAPE 4 : Retourner les données ==========
        // donneeCSV peut être vide si une erreur s'est produite
        // ou contenir tous les données du CSV si tout s'est bien passé
        return donneeCSV;
    }
}
