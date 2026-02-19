package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;

import java.io.*;
import java.util.List;

public class ResultatUEDAO {

    private static final String FILE_PATH = "data/resultats.csv";

    /**
     * Charge les résultats des étudiants depuis le fichier CSV et les associe aux étudiants et aux UEs.
     * Format CSV attendu : idEtudiant;codeUE;annee;semestre;statut
     * Exemple : 1;UE01;2025-2026;SEM1;VALIDE
     */
    public void chargerCursus(List<Etudiant> etudiants, List<UE> ues) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("Erreur cursus: fichier " + FILE_PATH + " introuvable.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // ignorer lignes vides
                String[] data = line.split(";");
                if (data.length != 5) {
                    System.err.println("Format invalide: " + line);
                    continue;
                }

                int idEtudiant = Integer.parseInt(data[0].trim());
                String codeUE = data[1].trim();
                String annee = data[2].trim();
                Semestre semestre = Semestre.valueOf(data[3].trim().toUpperCase());
                StatutUE statut = StatutUE.valueOf(data[4].trim().toUpperCase());

                // Chercher l'étudiant correspondant
                Etudiant etudiant = etudiants.stream()
                        .filter(e -> e.getNumE() == idEtudiant)
                        .findFirst()
                        .orElse(null);

                // Chercher l'UE correspondante
                UE ue = ues.stream()
                        .filter(u -> u.getCode().equals(codeUE))
                        .findFirst()
                        .orElse(null);

                if (etudiant != null && ue != null) {
                    ResultatUE resultat = new ResultatUE(ue, annee, semestre, statut);
                    etudiant.addResultatUE(resultat);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lecture fichier " + FILE_PATH + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erreur format ID étudiant: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur format Semestre ou StatutUE: " + e.getMessage());
        }
    }
}
