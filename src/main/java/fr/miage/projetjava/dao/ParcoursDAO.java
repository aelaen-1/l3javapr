package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class ParcoursDAO {
    //C'est le chemin vers le fichier qui contient la liste des parcours
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
                // Si la ligne est vide, on passe à la suivante
                if (line.isEmpty()) continue;
                // On découpe la ligne à chaque point-virgule ";"
                String[] data = line.split(";");
                // On vérifie qu'on a bien au moins le nom et la mention
                if (data.length < 2) continue;
                // On récupère le nom du parcours et la mention (ex: MIAGE et LICENCE)
                String nomParcours = data[0];
                String mentionStr = data[1].toUpperCase();
                try {
                    //On crée l'objet Parcours
                    Parcours p = new Parcours(nomParcours, Mention.valueOf(mentionStr));
                    // Chargements des Ues
                    // Si la 3ème colonne existe, elle contient les codes des UE
                    if (data.length > 2 && !data[2].isEmpty()) {
                        String[] codesUeEnTexte = data[2].split(",");
                        for (String codeUe : codesUeEnTexte) {
                            //On cherche l'objet UE correspondant dans la liste de toutes les UEs
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
                    System.out.println("Erreur sur le parcours : " + nomParcours);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier parcours.");
        }
        // On renvoie la liste de tous les parcours chargés
        return listeParcours;
    }
}