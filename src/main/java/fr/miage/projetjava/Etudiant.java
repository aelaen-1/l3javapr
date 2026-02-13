package fr.miage.projetjava;

import java.util.ArrayList;

public class Etudiant {
    private int numE;
    private String nomE;
    private String prenomE;
    private Parcours parcours;
    private ArrayList<ResultatUE> cursus = new ArrayList<>();

    public Etudiant(int numE, String nomE, String prenomE, Parcours parcours){
        this.numE = numE;
        this.nomE = nomE;
        this.prenomE = prenomE;
        this.parcours = parcours;
    }


    public int getNumE(){
        return this.numE;
    }

    public Parcours getParcours() {
        return parcours;
    }

    public String getNomE() {
        return nomE;
    }

    public String getPrenomE() {
        return prenomE;
    }

    public ArrayList<ResultatUE> getCursus() {
        return cursus;
    }

    public void setCursus(ResultatUE resultat) {
        this.cursus.add(resultat);
    }

    public void afficherCusrus(){
        for (ResultatUE ue : cursus){
            ue.afficher();
        }
    }

    @Override
    public String toString(){
        String affichage = "Numéro étudiant: "+this.numE + "\n Nom: "+ this.nomE +"\n Prénom: "+this.prenomE +"\n Parcours: "+ this.parcours;
        return affichage;
    }
}
