package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.util.*;

public class UEDAO {
    private static final String FILE_PATH = "data/ues.csv";

    public List<UE> chargerUEs() {
        List<UE> ues = new ArrayList<>();
        Map<String, String> prerequisMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                UE ue = new UE(data[0].trim(), data[1].trim(), Integer.parseInt(data[2].trim()),
                        Mention.valueOf(data[3].trim().toUpperCase()));
                ues.add(ue);
                if (data.length > 4) prerequisMap.put(data[0].trim(), data[4].trim());
            }
            // Liaison des prérequis
            for (UE ue : ues) {
                String codePre = prerequisMap.get(ue.getCode());
                if (codePre != null && !codePre.isEmpty()) {
                    ues.stream().filter(u -> u.getCode().equals(codePre)).findFirst().ifPresent(ue::setUEprerequis);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return ues;
    }
}