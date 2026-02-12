package fr.miage.projetjava.model;

public enum Mention {
    MIASHS("MIASHS"),
    INFORMATIQUE("Informatique"),
    MATHEMATIQUES("Mathématiques"),
    CHIMIE("Chimie"),
    BIOLOGIE("Biologie"),
    PHYSIQUE("Physique"),
    GENIE_CIVIL("Genie civil");
    private String affichage;
    private Mention(String affichage) {
        this.affichage = affichage;
    }
    @Override
    public String toString() {
        return this.affichage;
    }

}
