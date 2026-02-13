package fr.miage.projetjava;

public enum Mention {
    MIASHS("MIASHS"),
    INFORMATIQUE("Informatique"),
    BIOLOGIE("Biologie"),
    CHIMIE("Chimie"),
    PHYSIQUE("Physique");


    private String affichage;

    private Mention(String affichage)
    {
        this.affichage = affichage;
    }

    @Override
    public String toString()
    {
        return this.affichage;
    }
}