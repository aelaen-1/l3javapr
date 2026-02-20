package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EtudiantDAO {
    private static final String FILE_PATH = "data/etudiants.csv";

    public List<Etudiant> chargerTout(List<Parcours> parcoursDispos, List<UE> toutesLesUE) {
        List<Etudiant> etudiants = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path)) {
            System.err.println("[DAO] Fichier non trouvé : " + FILE_PATH);
            return etudiants;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Le -1 est crucial pour lire la colonne des notes même si elle est vide
                String[] data = line.split(";", -1);
                if (data.length < 5) continue;

                try {
                    int id = Integer.parseInt(data[0].trim());
                    String nom = data[1].trim();
                    String prenom = data[2].trim();
                    String nomParcours = data[3].trim();
                    Semestre sem = Semestre.valueOf(data[4].trim().toUpperCase());

                    // Recherche du parcours correspondant
                    Parcours parc = parcoursDispos.stream()
                            .filter(p -> p.getNom().equalsIgnoreCase(nomParcours))
                            .findFirst().orElse(null);

                    if (parc != null) {
                        Etudiant etu = new Etudiant(id, nom, prenom, parc, sem);

                        // Chargement des résultats (colonne 6)
                        if (data.length >= 6 && !data[5].trim().isEmpty()) {
                            String[] results = data[5].split(",");
                            for (String resStr : results) {
                                String[] p = resStr.split(":");
                                if (p.length == 4) {
                                    String code = p[0];
                                    toutesLesUE.stream()
                                            .filter(u -> u.getCode().equals(code))
                                            .findFirst()
                                            .ifPresent(ue -> etu.addResultatUE(new ResultatUE(ue, p[1], Semestre.valueOf(p[2]), StatutUE.valueOf(p[3]))));
                                }
                            }
                        }
                        etudiants.add(etu);
                    }
                } catch (Exception e) {
                    System.err.println("[DAO] Erreur ligne : " + line + " -> " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[DAO] " + etudiants.size() + " étudiants chargés.");
        return etudiants;
    }

    public void sauvegarderTout(List<Etudiant> etudiants) {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
                for (Etudiant e : etudiants) {
                    String res = e.getResultatsUE().stream()
                            .map(r -> r.getUe().getCode() + ":" + r.getAnnee() + ":" + r.getSemestre().name() + ":" + r.getStatut().name())
                            .collect(Collectors.joining(","));

                    bw.write(String.format("%d;%s;%s;%s;%s;%s",
                            e.getNumE(), e.getNomE(), e.getPrenomE(),
                            e.getParcours().getNom(), e.getSemestreCourant().name(), res));
                    bw.newLine();
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}