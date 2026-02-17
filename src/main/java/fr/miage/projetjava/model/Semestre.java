package fr.miage.projetjava.model;

public enum Semestre {
    IMPAIR("Impair"),
    PAIR("Pair");


    private String affichage;

    private Semestre(String affichage) {
        this.affichage = affichage;
    }

    @Override
    public String toString() {
        return this.affichage;
    }
}