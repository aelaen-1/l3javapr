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
        return resultatsUE.stream()
                .filter(r -> r.getStatut() == StatutUE.VALIDE) // Uniquement les UE validées
                .mapToInt(r -> r.getUe().getCredit())       // On récupère les crédits de l'UE
                .sum();                                        // On fait la somme
    }


    @Override
    public String toString() {
        return this.numE+" - "+this.nomE+" - "+this.prenomE+"/n"+this.parcours+" - "+this.semestreCourant;
    }
}
