package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class UEDAO {
    // Le chemin vers le fichier des UEs
    private static final String FILE_PATH = "data/ues.csv";
    /*
     * Cette méthode charge toutes les UEs et gère les listes de prérequis.
     */
    public ArrayList<UE> chargerUEs() {
        ArrayList<UE> ues = new ArrayList<>();
        // on cree des objects
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isEmpty()) continue;
                // On découpe : Code ; Nom ; Crédits ; Mention
                String[] morceaux = ligne.split(";", -1);
                UE nouvelleUE = new UE(
                        morceaux[0],
                        morceaux[1],
                        Integer.parseInt(morceaux[2]),
                        Mention.valueOf(morceaux[3].toUpperCase())
                );
                ues.add(nouvelleUE);
            }
        } catch (IOException e) {
            System.out.println("Erreur lecture étape 1");
        }
        // On lie les prerequis des Ues
        // On rouvre le fichier pour lire la 5ème colonne (les prérequis)
        try (BufferedReader br2 = new BufferedReader(new FileReader(FILE_PATH))) {
            String ligne;
            while ((ligne = br2.readLine()) != null) {
                if (ligne.isEmpty()) continue;
                String[] morceaux = ligne.split(";", -1);
                // Si on a des prérequis dans la 5ème colonne par exemple une UE peut avoir plusieurs prerequis
                if (morceaux.length > 4 && !morceaux[4].isEmpty()) {
                    String codeUEActuelle = morceaux[0];
                    // On sépare les différents codes par la virgule
                    String[] codesPrerequis = morceaux[4].split(",");
                    // On cherche l'UE à laquelle on veut ajouter les prérequis
                    for (UE ueCible : ues) {
                        if (ueCible.getCode().equals(codeUEActuelle)) {
                            // Pour chaque code de prérequis trouvé
                            for (String codeUnSeulPre : codesPrerequis) {
                                // On cherche l'objet UE qui correspond à ce code
                                for (UE objetPrerequis : ues) {
                                    if (objetPrerequis.getCode().equals(codeUnSeulPre)) {
                                        // On l'ajoute à la liste des prérequis de notre UE cible
                                        ueCible.setUEprerequis(objetPrerequis);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lecture étape 2");
        }
        return ues;
    }
}