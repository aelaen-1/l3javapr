package fr.miage.projetjava.model;

import java.util.ArrayList;

public class Etudiant {

    private int numE;
    private String nomE;
    private String prenomE;
    private Semestre semestreCourant;
    private Parcours parcours;
    private ArrayList<ResultatUE> resultatsUE = new ArrayList<>();

    public Etudiant(int numE, String nomE, String prenomE, Parcours parcours, Semestre semestreCourant) {
        this.numE = numE;
        this.nomE = nomE;
        this.prenomE = prenomE;
        this.semestreCourant=semestreCourant;
        this.parcours = parcours;
    }

    public Semestre getSemestreCourant() {
        return semestreCourant;
    }
    public void setSemestreCourant(Semestre semestreCourant){
        this.semestreCourant=semestreCourant;
    }
    public int getNumE() {

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

    public ArrayList<ResultatUE> getResultatsUE() {
        return resultatsUE;
    }

    public void addResultatUE(ResultatUE resultat) {
        this.resultatsUE.add(resultat);
    }

    public void afficherCusrus() {
        for (ResultatUE ue : resultatsUE) {
            ue.afficher();
        }
    }
<<<<<<< HEAD
    public void setNomE(String nomE){
        this.nomE=nomE;
    }
    public void setPrenomE(String prenomE){
        this.prenomE=prenomE;
    }
    public void setParcours(Parcours p){
        this.parcours=p;
    }


    public int getTotalCredits() {
        int totalcredit =0;
        for( ResultatUE res:resultatsUE){
            if(res.getStatut()==StatutUE.VALIDE){
                totalcredit =+res.getUe().getCredit();

            }
        }
        return totalcredit;
    }
    @Override
    public String toString() {
        return this.numE+" - "+this.nomE+" - "+this.prenomE+"\n"+this.parcours+" - "+this.semestreCourant;
    }
}
=======

    @Override
    public String toString(){
        return "Numéro étudiant: "+this.numE + "\n Nom: "+ this.nomE +"\n Prénom: "+this.prenomE +"\n Parcours: "+ this.parcours;
    }
}
>>>>>>> 248a4bf9eab9d7833a9b78b88e41ce003a65f133
