package fr.miage.projetjava.model;

public enum StatutUE {
    VALIDE("Valide"),
    ENCOURS("En cours"),
    ECHOUE("Echoué");


    private String affichage;

    private StatutUE(String affichage) {
        this.affichage = affichage;
    }

    @Override
    public String toString() {
        return this.affichage;
    }
}
