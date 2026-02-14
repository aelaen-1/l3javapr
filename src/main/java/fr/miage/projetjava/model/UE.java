package fr.miage.projetjava.model;

import java.util.ArrayList;

public class UE {
    private String code;
    private String intitule;
    private int credit;
    private Mention mention;
    private ArrayList<UE> UEprerequis = new ArrayList<>();

    public UE(String code, String intitule, int credit, Mention mention){
        this.code = code;
        this.intitule = intitule;
        this.credit = credit;
        this.mention = mention;
    }

    public ArrayList<UE> getUEprerequis() {
        return UEprerequis;
    }

    public String getCode() {
        return code;
    }

    public Mention getMention() {
        return mention;
    }

    public String getIntitule() {
        return intitule;
    }

    public int getCredit() {
        return credit;
    }

    public void setUEprerequis(UE ue) {
        this.UEprerequis.add(ue);
    }
}
