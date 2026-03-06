package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class ResultatUEDAO {
    // Le chemin vers le fichier qui contient toutes les notes des étudiants
    private static final String FILE_PATH = "data/resultats.csv";

    /**
     * Cette méthode lit le fichier des résultats et distribue chaque note au bon étudiant.
     */
    public void chargerCursus(List<Etudiant> etudiants, List<UE> ues) {
        File file = new File(FILE_PATH);
        // Si le fichier n'existe pas, on s'arrête tout de suite
        if (!file.exists()) {
            return;
        }
        // On ouvre le fichier pour lire les lignes une par une
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                // On découpe la ligne à chaque point-virgule
                // Format fichier :NumE ; CODE_UE ; ANNEE ; SEMESTRE ; STATUT
                String[] morceaux = ligne.split(";");
                // On récupère le NumE de l'étudiant et le code de la matière
                int idEtudiant = Integer.parseInt(morceaux[0]);
                String codeUE = morceaux[1];
                // Trouver l'object Ue correspondant
                // On cherche dans la liste de toutes les UEs celle qui a le bon code
                UE ueTrouvee = null;
                for (UE u : ues) {
                    if (u.getCode().equals(codeUE)) {
                        ueTrouvee = u;
                        // On a trouvé la matière, on arrête la boucle
                        break;
                    }
                }
                // trouver l'etudiant et  marquer une Ue qu'il suit comme validée ou Echouée
                if (ueTrouvee != null) {
                    for (Etudiant etu : etudiants) {
                        // Si le numéro de l'étudiant correspond à l'ID lu dans le fichier
                        if (etu.getNumE() == idEtudiant) {
                            // On crée l'objet ResultatUE avec les infos de la ligne
                            ResultatUE note = new ResultatUE(
                                    ueTrouvee,
                                    morceaux[2],
                                    Semestre.valueOf(morceaux[3].toUpperCase()),
                                    StatutUE.valueOf(morceaux[4].toUpperCase())
                            );
                            // On ajoute cette note au dossier de l'étudiant
                            etu.addResultatUE(note);
                            // On a trouvé l'étudiant, on passe à la ligne suivante du fichier
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture des résultats.");
        }
    }
    /*
     * Cette méthode enregistre toutes les notes de tous les étudiants dans le fichier CSV.
     */
    public void sauvegarderCursus(List<Etudiant> etudiants) {
        // On ouvre le fichier en mode écriture et on écrase l'ancien contenu
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // On parcourt la liste de tous les étudiants
            for (Etudiant e : etudiants) {
                // Pour chaque étudiant, on parcourt la liste de ses notes (son cursus)
                for (ResultatUE res : e.getResultatsUE()) {
                    // On prépare la ligne à écrire : NumE; CodeUE ; Année ; Semestre ; Statut
                    String ligneNote = e.getNumE() + ";" +
                            res.getUe().getCode() + ";" +
                            res.getAnnee() + ";" +
                            res.getSemestre().name() + ";" +
                            res.getStatut().name();
                    // On écrit la ligne dans le fichier et on saute à la ligne suivante
                    bw.write(ligneNote);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des résultats.");
        }
    }
}