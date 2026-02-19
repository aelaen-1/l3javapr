package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class ParcoursDAO {
    private static final String FILE_PATH = "data/parcours.csv";

    public List<Parcours> chargerParcours(List<UE> ttesLesUEs) {
        List<Parcours> listeParcours = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.err.println("Fichier introuvable : " + file.getAbsolutePath());
            return listeParcours;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Ignore les lignes vides

                String[] data = line.split(";");
                if (data.length < 2) continue; // Ligne mal formée

                // Nettoyage des données (trim) pour éviter les erreurs "Parcours introuvable"
                String nomParcours = data[0].trim();
                String mentionStr = data[1].trim().toUpperCase();

                try {
                    Parcours p = new Parcours(nomParcours, Mention.valueOf(mentionStr));

                    // Chargement des UEs obligatoires
                    if (data.length > 2 && !data[2].trim().isEmpty()) {
                        String[] codes = data[2].split(",");
                        for (String code : codes) {
                            String codeNettoye = code.trim();

                            // Recherche de l'UE dans la liste globale chargée par UEDAO
                            ttesLesUEs.stream()
                                    .filter(ue -> ue.getCode().equalsIgnoreCase(codeNettoye))
                                    .findFirst()
                                    .ifPresent(p::addUEObligatoire);
                        }
                    }
                    listeParcours.add(p);
                } catch (IllegalArgumentException e) {
                    System.err.println("Mention inconnue dans le CSV : " + mentionStr);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture parcours.csv : " + e.getMessage());
        }
        return listeParcours;
    }
}