package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class UEDAO {
    private static final String FILE_PATH = "data/ues.csv";

    public List<UE> chargerUEs() {
        List<UE> ues = new ArrayList<>();
        System.out.println("=== CHARGEMENT DES UEs DEPUIS CSV ===");
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(";");

                // data: code;intitule;credits;mention
                UE ue = new UE(data[0], data[1], Integer.parseInt(data[2]),
                        Mention.valueOf(data[3].trim().toUpperCase()));
                ues.add(ue);

                // AFFICHAGE CONSOLE
                System.out.println("[DAO] UE détectée : " + ue.getCode() + " | " + ue.getIntitule());
            }
            System.out.println("TOTAL : " + ues.size() + " UEs chargées.\n");
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erreur lecture UEs: " + e.getMessage());
        }
        return ues;
    }
}