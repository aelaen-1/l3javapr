package fr.miage.projetjava;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Log4j2 OK");

        /*
        test ScolariteService prerequis*/
        UE ue1 = new UE("ue1", "ue numero 1", 3, Mention.MIASHS);
        UE ue2 = new UE("ue2", "ue numero 2", 3, Mention.MIASHS);
        UE ue3 = new UE("ue3", "ue numero 3", 3, Mention.MIASHS);
        UE ue4 = new UE("ue4", "ue numero 4", 3, Mention.MIASHS);
        UE ue5 = new UE("ue5", "ue numero 5", 3, Mention.MIASHS);
        UE ue6 = new UE("ue6", "ue numero 6", 3, Mention.MIASHS);
        UE ue7 = new UE("ue7", "ue numero 7", 3, Mention.MIASHS);
        UE ue8 = new UE("ue8", "ue numero 8", 3, Mention.MIASHS);
        UE ue9 = new UE("ue9", "ue numero 9", 3, Mention.MIASHS);
        UE ue10 = new UE("ue10", "ue numero 10", 3, Mention.MIASHS);
        UE ue11 = new UE("ue11", "ue numero 11", 3, Mention.MIASHS);
        UE ue12 = new UE("ue12", "ue numero 12", 3, Mention.MIASHS);
        UE ue13 = new UE("ue13", "ue numero 13", 3, Mention.MIASHS);



        ue3.setUEprerequis(ue2);
        ue4.setUEprerequis(ue3);
        ue5.setUEprerequis(ue4);
        ue9.setUEprerequis(ue4);
        ue9.setUEprerequis(ue6);
        ue9.setUEprerequis(ue8);
        ue12.setUEprerequis(ue11);
        ue12.setUEprerequis(ue10);
        ue13.setUEprerequis(ue10);
        ue13.setUEprerequis(ue3);

        Parcours miage = new Parcours("Miage", Mention.MIASHS);
        miage.addUEObligatoire(ue2);
        miage.addUEObligatoire(ue3);
        miage.addUEObligatoire(ue6);

        Etudiant e1 = new Etudiant(1,"nom1","pre1",miage );

        Etudiant e2 = new Etudiant(2,"nom2","pre2",miage );

        ResultatUE resultatue1 = new ResultatUE(ue1,"2022", Semestre.IMPAIR, StatutUE.VALIDE);
        ResultatUE resultatue2 = new ResultatUE(ue2,"2022", Semestre.IMPAIR, StatutUE.VALIDE);
        ResultatUE resultatue3 = new ResultatUE(ue3,"2022", Semestre.PAIR, StatutUE.VALIDE);
        ResultatUE resultatue4 = new ResultatUE(ue4,"2023", Semestre.IMPAIR, StatutUE.VALIDE);
        ResultatUE resultatue5 = new ResultatUE(ue5,"2023", Semestre.PAIR, StatutUE.VALIDE);
        ResultatUE resultatue6 = new ResultatUE(ue6,"2023", Semestre.PAIR, StatutUE.VALIDE);
        ResultatUE resultatue8 = new ResultatUE(ue8,"2023", Semestre.PAIR, StatutUE.VALIDE);
        ResultatUE resultatue7 = new ResultatUE(ue7,"2025", Semestre.PAIR, StatutUE.ENCOURS);
        ResultatUE resultatue10 = new ResultatUE(ue10,"2024", Semestre.PAIR, StatutUE.ECHOUE);


        e1.setCursus(resultatue1);
        e1.setCursus(resultatue2);
        e1.setCursus(resultatue3);
        e1.setCursus(resultatue4);
        e1.setCursus(resultatue5);
        e1.setCursus(resultatue6);
        e1.setCursus(resultatue7);
        e1.setCursus(resultatue8);
        e1.setCursus(resultatue10);

        //e1.afficherCusrus();

        ScolariteService sco = new ScolariteService();
        log.info("UE X pre-requis : "+sco.prerequis(ue9,e1));
        log.info("Diplôme validé "+sco.estDiplome(e1));

        log.info("Etudiant 2 " +sco.prerequis(ue2, e2));
    }

}

