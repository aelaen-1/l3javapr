package fr.miage.projetjava.model;

public class ResultatUE {
    private UE ue;
    private String annee;
    private Semestre semestre;
    private StatutUE statut;
    public ResultatUE(UE ue,String annee,Semestre semestre,StatutUE statut){
        this.ue=ue;
        this.annee=annee;
        this.semestre=semestre;
        this.statut=statut;
    }
    public Semestre getSemestre() {
        return semestre;
    }
    public StatutUE getStatut() {
        return statut;
    }
    public String getAnnee() {
        return annee;
    }
    public UE getUe() {
        return ue;
    }
    public void setStatutUE(StatutUE statut){
        this.statut=statut;
    }
}
