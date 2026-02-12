package fr.miage.projetjava;

import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Semestre;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Log4j2 OK");
        Parcours p=new Parcours("MIAGE", Mention.MIASHS);
        Etudiant e= new Etudiant(222,"ms","terefi", Semestre.IMPAIR,p);
        log.info(e.toString());

    }

    }

