package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class ResultatUEDAO {
    private static final String FILE_PATH = "data/resultats.csv";

    public void chargerCursus(List<Etudiant> etudiants, List<UE> ues) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                int idEtudiant = Integer.parseInt(data[0].trim());
                UE ue = ues.stream().filter(u -> u.getCode().equals(data[1].trim())).findFirst().orElse(null);

                etudiants.stream().filter(e -> e.getNumE() == idEtudiant).findFirst().ifPresent(e -> {
                    if (ue != null) {
                        e.addResultatUE(new ResultatUE(ue, data[2].trim(),
                                Semestre.valueOf(data[3].trim().toUpperCase()),
                                StatutUE.valueOf(data[4].trim().toUpperCase())));
                    }
                });
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void sauvegarderCursus(List<Etudiant> etudiants) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Etudiant e : etudiants) {
                for (ResultatUE res : e.getResultatsUE()) {
                    bw.write(e.getNumE() + ";" + res.getUe().getCode() + ";" + res.getAnnee() + ";" +
                            res.getSemestre().name() + ";" + res.getStatut().name());
                    bw.newLine();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}