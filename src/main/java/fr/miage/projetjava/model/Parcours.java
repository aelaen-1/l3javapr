package fr.miage.projetjava.model;

import java.util.ArrayList;

public class Parcours {
    private String nom;
    private ArrayList<UE> UEObligatoire = new ArrayList<>();
    private Mention mention;

    public Parcours(String nom, Mention mention) {
        this.nom = nom;
        this.mention = mention;
    }

    public ArrayList<UE> getUEObligatoire() {
        return UEObligatoire;
    }

    public String getNom() {
        return nom;
    }

    public void addUEObligatoire(UE ue) {
        if (!UEObligatoire.contains(ue))
            UEObligatoire.add(ue);
    }

    public Mention getMention() {
        return mention;
    }
}
