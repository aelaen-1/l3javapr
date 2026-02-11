package fr.miage.projetjava.model;

import java.util.ArrayList;

public class Etudiant {
    private int numeroEtrudiant;
    private String nom;
    private String prenom;
    private Parcours parcours;
    private ArrayList<ResultatUE> cursus;
    public Etudiant(int numeroEtudiant, String nom, String prenom, Parcours parcours ){
        this.numeroEtrudiant=numeroEtudiant;
        this.nom=nom;
        this.prenom=prenom;
        this.parcours=parcours;
        this.cursus=new ArrayList<>();
    }
    public String getNom() {
        return nom;
    }
    public Parcours getParcours() {
        return parcours;
    }
    public int getNumeroEtrudiant() {
        return numeroEtrudiant;
    }
    public String getPrenom() {
        return prenom;
    }
    public ArrayList<ResultatUE> getCursus() {
        return cursus;
    }
    public void SetCurcus(ResultatUE res){
        this.cursus.add(res);
    }
}
