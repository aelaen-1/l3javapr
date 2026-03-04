package fr.miage.projetjava.database;


import fr.miage.projetjava.dao.BDEtudiant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Classe CreationTableBD : Gère la création des tables dans la bd
 * 3 méthodes : void insertEtudiant, void recuperationInformationListEtudiant  et void recuperationInformationEtudiantCSV
 *
 * Cette classe est responsable de :
 * 1. Récupérer les informations transmises des fichiers CSV concernant les Étudiants
 * 2. Mettre ces informations sous le bon format
 * 3. Ajouter ces informations dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */


public class CreationTableBD {
    private static final Logger log = LogManager.getLogger(ConnexionBD.class);


    /**
     * Méthode appelée  dans la méthode main de la classe Main
     *
     * Cette méthode va se connecter à la BD et exécuter toute les requêtes pour créer les différentes tables
     *
     *
     *
     */
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
        //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
        //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
        // à la BD
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){

            //boucle pour parcourir le tableau de String et exécuté une à une toutes les requêtes car stmt.execute
            // peux éxecuter que un à un toutes les requêtes
            for (String requete : requeteAjoutTable){

                stmt.execute(requete);
                log.info("table créé");

            }

            //BDEtudiant ajout = new BDEtudiant();
            //ajout.insertEtudiant();
        }

        catch(SQLException e){
            log.error(e.getMessage());
        }

    }
}
