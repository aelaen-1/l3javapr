package fr.miage.projetjava.database;


import fr.miage.projetjava.dao.BDEtudiant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreationTableBD {
    private static final Logger log = LogManager.getLogger(ConnexionBD.class);

    public static void init()
    {

        //tableau de String contenant toutes les requêtes de créations de table
        String[] requeteAjoutTable = {



                "CREATE TABLE IF NOT EXISTS Parcours ("
                        + "nom text PRIMARY KEY,"
                        + "mention text,"
                        + "CONSTRAINT ck_UE_MENTION CHECK (mention in('MIASHS','Informatique', 'Biologie', 'Chimie', 'Physique'))"
                        + ");",

                "CREATE TABLE IF NOT EXISTS Etudiant ("
                        + "numE INTEGER PRIMARY KEY,"
                        + "prenomE text,"
                        +    "nomE text,"
                        + "parcours text,"
                        + "CONSTRAINT fk_Etudiant_parcours FOREIGN KEY (parcours) REFERENCES Parcours(nom)"
                        + ");",

                "CREATE TABLE IF NOT EXISTS UE ("
                        + "code text PRIMARY KEY,"
                        + "intitule text,"
                        + "credit int,"
                        + "mention text,"
                        + "CONSTRAINT ck_UE_MENTION CHECK (mention in('MIASHS','Informatique', 'Biologie', 'Chimie', 'Physique'))"
                        + ");",

                "CREATE TABLE IF NOT EXISTS UEObligatoire ("
                        + "nom text,"
                        + "UE text,"
                        + "PRIMARY KEY(nom, UE),"
                        + "CONSTRAINT fk_UEObligatoire_Parcours FOREIGN KEY (nom) REFERENCES Parcours(nom),"
                        + "CONSTRAINT fk_UEObligatoire_UE FOREIGN KEY (UE) REFERENCES UE(code)"
                        + ");",

                "CREATE TABLE IF NOT EXISTS UEprerequis ("
                        + "codeUE text," //correspond au code de l'UE qui veut stocker ces UEprerequis
                        + "codeUEPrerequis text,"//correspond au code de l'UE qui fait partie des prerequis de codeUE
                        + "PRIMARY KEY(codeUE, codeUEPrerequis),"
                        + "CONSTRAINT fk_UEPrerequis_codeUE FOREIGN KEY (codeUE) REFERENCES UE(code),"
                        + "CONSTRAINT fk_UEPrerequis_codeUEPrerequis FOREIGN KEY (codeUEPrerequis) REFERENCES UE(code)"
                        + ");",



                "CREATE TABLE IF NOT EXISTS ResultatUE ("
                        + "code text,"
                        + "numE text,"
                        + "annee text,"
                        + "semestre text,"
                        + "statut text,"
                        + "PRIMARY KEY (code, numE),"
                        + "CONSTRAINT fk_ResultatUE_code FOREIGN KEY (code) REFERENCES UE(code),"
                        + "CONSTRAINT fk_ResultatUE_numE FOREIGN KEY (numE) REFERENCES Etudiant(numE),"
                        + "CONSTRAINT ck_ResultatUE_statut CHECK (statut in ('Valide', 'En cours', 'Echoué')),"
                        + "CONSTRAINT ck_ResultatUE_Semestre CHECK(semestre = 'Pair' or semestre = 'Impair')"
                        + ");",

        } ;

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){

            //boucle pour parcourir le tableau de String et exécuté une à une toutes les requêtes
            for (String requete : requeteAjoutTable){

                stmt.execute(requete);
                log.info("table créé");

            }

            //BDEtudiant ajout = new BDEtudiant();
            //ajout.insertEtudiant();
        }

        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
