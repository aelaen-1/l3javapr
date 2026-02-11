package fr.miage.projetjava.model;
import java.util.ArrayList;
public class UE {
    private String code;
    private String intitule;
    private int credit;
    private Mention mention;
    private ArrayList<UE> listePrerequis;
    public UE(String Code ,String intitule,int credit,Mention mention){
        this.code=code;
        this.intitule=intitule;
        this.credit=credit;
        this.mention=mention;
        this.listePrerequis=new ArrayList<>();
    }
    public void AddPrerequis(UE ue) {
        this.listePrerequis.add(ue);
    }
    public int getCredit() {
        return credit;
    }
    public String getCode() {
        return code;
    }
    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public ArrayList<UE> getListePrerequis() {
        return listePrerequis;
    }

    public Mention getMention() {
        return mention;
    }
}
