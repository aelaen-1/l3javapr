package fr.miage.projetjava.model;

import java.util.ArrayList;

public class Parcours {
    private String nomParcours;
    private Mention mention;
    private ArrayList<UE> listeUeObligatoires;
    public Parcours(String nomParcours, Mention mention){
        this.nomParcours=nomParcours;
        this.mention=mention;
        this.listeUeObligatoires=new ArrayList<>();
    }
    public Mention getMention() {
        return mention;
    }
    public String  getNomParcours(){
        return nomParcours;
    }

    public ArrayList<UE> getListeUeObligatoires() {
        return listeUeObligatoires;
    }
    public void addUeObligatoire(UE ue){
        this.listeUeObligatoires.add(ue);
    }
}
