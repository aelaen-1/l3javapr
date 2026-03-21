package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class ParcoursDAO {
    // C'est le chemin vers le fichier qui contient la liste des parcours
    private static final String FILE_PATH = "data/parcours.csv";

    /*
     * Cette méthode sert à LIRE le fichier parcours.csv pour créer les objets Parcours.
     */
    public ArrayList<Parcours> chargerParcours(List<UE> ttesLesUEs) {
        // On prépare une liste vide pour stocker les parcours qu'on va trouver
        ArrayList<Parcours> listeParcours = new ArrayList<>();
        File file = new File(FILE_PATH);

        // On vérifie si le fichier existe bien sur le disque
        if (!file.exists()) {
            System.err.println("Le fichier parcours.csv n'existe pas !");
            return listeParcours;
        }

        // On ouvre le fichier pour le lire ligne par ligne
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // On ignore les lignes vides ET la ligne d'en-tête du CSV
                if (line.isEmpty() || line.startsWith("Code")) continue;

                // On découpe la ligne à chaque point-virgule ";" (le -1 évite les bugs si la dernière colonne est vide)
                String[] data = line.split(";", -1);

                // On vérifie qu'on a bien nos colonnes
                if (data.length < 3) continue;

                // On récupère les infos selon notre fichier CSV (Code;Nom;Mention;UEs)
                String codeParcours = data[0]; // ex: "MIAGE"
                String nomComplet = data[1];   // ex: "Méthodes Informatiques..."
                String mentionStr = data[2].toUpperCase(); // ex: "MIASHS"

                try {
                    // On crée l'objet Parcours (en utilisant le code court comme nom, pour s'adapter à ton constructeur)
                    Parcours p = new Parcours(codeParcours, Mention.valueOf(mentionStr));

                    // Chargement des UEs
                    // La 4ème colonne (index 3) contient les codes des UEs obligatoires
                    if (data.length > 3 && !data[3].isEmpty()) {
                        String[] codesUeEnTexte = data[3].split(",");
                        for (String codeUe : codesUeEnTexte) {
                            // On cherche l'objet UE correspondant dans la liste de toutes les UEs
                            for (UE uneUE : ttesLesUEs) {
                                if (uneUE.getCode().equalsIgnoreCase(codeUe)) {
                                    // Si on a trouvé l'UE, on l'ajoute au parcours
                                    p.addUEObligatoire(uneUE);
                                    // On arrête de chercher pour ce code, on passe au suivant
                                    break;
                                }
                            }
                        }
                    }
                    // On ajoute le parcours complet à notre liste finale
                    listeParcours.add(p);
                } catch (Exception e) {
                    System.out.println("Erreur sur le parcours : " + codeParcours);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier parcours.");
        }

        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les parcours dans la BD
        ParcoursBD.recuperationInformationListParcours(listeParcours);

        // On renvoie la liste de tous les parcours chargés
        return listeParcours;
    }
}