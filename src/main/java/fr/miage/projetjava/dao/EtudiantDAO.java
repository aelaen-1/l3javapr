package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    private static final String FILE_PATH = "data/etudiants.csv";

    /**
     * Charge tous les étudiants depuis le CSV et associe les parcours existants.
     * Format CSV attendu : id;nom;prenom;parcours;semestre
     * Exemple : 1;Dupont;Jean;Informatique;SEM1
     */
    public List<Etudiant> chargerTout(List<Parcours> parcoursDisponibles) {
        List<Etudiant> etudiants = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.err.println("Fichier introuvable : " + FILE_PATH);
            return etudiants;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // ignorer lignes vides
                String[] data = line.split(";");
                if (data.length < 5) {
                    System.err.println("Format invalide : " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(data[0].trim());
                    String nom = data[1].trim();
                    String prenom = data[2].trim();
                    String nomParcours = data[3].trim();
                    Semestre semestre = Semestre.valueOf(data[4].trim().toUpperCase());

                    // Chercher le parcours correspondant
                    Parcours parcours = parcoursDisponibles.stream()
                            .filter(p -> p.getNom().equalsIgnoreCase(nomParcours))
                            .findFirst()
                            .orElse(null);

                    if (parcours == null) {
                        System.err.println("Parcours introuvable pour étudiant " + nom + " : " + nomParcours);
                        continue;
                    }

                    Etudiant etu = new Etudiant(id, nom, prenom, parcours, semestre);
                    etudiants.add(etu);

                } catch (NumberFormatException e) {
                    System.err.println("ID invalide : " + data[0]);
                } catch (IllegalArgumentException e) {
                    System.err.println("Semestre invalide : " + data[4]);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lecture fichier " + FILE_PATH + " : " + e.getMessage());
        }

        return etudiants;
    }

    /**
     * Sauvegarde tous les étudiants dans le fichier CSV.
     */
    public void sauvegarderTout(List<Etudiant> etudiants) {
        File file = new File(FILE_PATH);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Etudiant e : etudiants) {
                String line = String.join(";",
                        String.valueOf(e.getNumE()),
                        e.getNomE(),
                        e.getPrenomE(),
                        e.getParcours().getNom(),
                        e.getSemestreCourant().name()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur écriture fichier " + FILE_PATH + " : " + e.getMessage());
        }
    }
}
