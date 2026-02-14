package fr.miage.projetjava.model;

import java.util.ArrayList;

public class Etudiant {

    private int numE;
    private String nomE;
    private String prenomE;
    private Parcours parcours;
    private ArrayList<ResultatUE> resultatsUE = new ArrayList<>();

    public Etudiant(int numE, String nomE, String prenomE, Parcours parcours) {

        this.numE = numE;
        this.nomE = nomE;
        this.prenomE = prenomE;
        this.parcours = parcours;
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
}
