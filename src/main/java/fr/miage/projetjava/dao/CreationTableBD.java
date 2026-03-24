package fr.miage.projetjava.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe CreationTableBD : Gère la création des tables dans la bd
 *
 * Cette classe permet la création des différentes tables dans la BD
 *
 * Si la création des tables dans la base de données a échoué, alors un message d'erreur est renvoyé
 *
 */
public class CreationTableBD {
    private static final Logger log = LogManager.getLogger(CreationTableBD.class);

    /**
     * Méthode appelée  dans la méthode main de la classe MainApp
     *
     * Cette méthode va se connecter à la BD et exécuter toutes les requêtes pour créer les différentes tables
     * @param : Connection conn
     * conn : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */

    public static void createTable(Connection conn){
        //tableau de String contenant les requêtes permettant la suppresion des tables pour repartir sur de nouvelle table
        String[] requeteSuppressionTable = {
                "DROP Table ResultatUE",
                "DROP Table UEObligatoire",
                "DROP Table UEprerequis",
                "DROP Table UE",
                "DROP Table Etudiant",
                "DROP Table Parcours"
        };

        //tableau de String contenant toutes les requêtes de créations de table
        String[] requeteAjoutTable = {

                "CREATE TABLE Parcours (code Varchar2(30) PRIMARY KEY, nom Varchar2(30), mention Varchar2(20),CONSTRAINT ck_Parcours_MENTION CHECK (mention IN('MIASHS','Informatique', 'Biologie', 'Chimie', 'Physique')))",

                "CREATE TABLE Etudiant (numE number PRIMARY KEY, prenomE Varchar2(30), nomE Varchar2(30), parcours Varchar2(30), semestre Varchar2(20), CONSTRAINT fk_Etudiant_parcours FOREIGN KEY (parcours) REFERENCES Parcours(code), CONSTRAINT ck_Etudiant_Semestre CHECK(semestre = 'Pair' or semestre = 'Impair'))",

                "CREATE TABLE UE ( codeUE Varchar2(10) PRIMARY KEY, intitule Varchar2(50), credit Number, mention Varchar2(20), CONSTRAINT ck_UE_MENTION CHECK (mention IN('MIASHS','Informatique', 'Biologie', 'Chimie', 'Physique')))",

                "CREATE TABLE UEObligatoire (nomParcours Varchar2(30), UE Varchar2(10), PRIMARY KEY(nomParcours, UE),CONSTRAINT fk_UEObligatoire_Parcours FOREIGN KEY (nomParcours) REFERENCES Parcours(code),CONSTRAINT fk_UEObligatoire_UE FOREIGN KEY (UE) REFERENCES UE(codeUE))",

                "CREATE TABLE UEprerequis (codeUE Varchar2(10), codeUEPrerequis Varchar2(10), PRIMARY KEY(codeUE, codeUEPrerequis), CONSTRAINT fk_UEPrerequis_codeUE FOREIGN KEY (codeUE) REFERENCES UE(codeUE), CONSTRAINT fk_UEPrerequis_codeUEPrerequis FOREIGN KEY (codeUEPrerequis) REFERENCES UE(codeUE))",

                "CREATE TABLE ResultatUE (codeUE Varchar2(10), numE number, annee number ,semestre Varchar2(20), statut Varchar2(20), PRIMARY KEY (codeUE, numE), CONSTRAINT fk_ResultatUE_code FOREIGN KEY (codeUE) REFERENCES UE(codeUE), CONSTRAINT fk_ResultatUE_numE FOREIGN KEY (numE) REFERENCES Etudiant(numE), CONSTRAINT ck_ResultatUE_statut CHECK (statut in ('Valide', 'En cours', 'Echoué')), CONSTRAINT ck_ResultatUE_Semestre CHECK(semestre = 'Pair' or semestre = 'Impair'))"

        };

        // statement va être utilisé pour exécuter les requêtes SQL
        try(Statement stmt = conn.createStatement();){

            //boucle pour parcourir le tableau de String et exécuté une à une toutes les requêtes car stmt.execute
            // peux exécuter que une à une toutes les requêtes
            for (String requete : requeteSuppressionTable){

                stmt.executeUpdate(requete);
                log.info("table supprimé");

            }
            //boucle pour parcourir le tableau de String et exécuté une à une toutes les requêtes car stmt.execute
            // peux exécuter que une à une toutes les requêtes
            for (String requete : requeteAjoutTable){

                stmt.executeUpdate(requete);
                log.info("table créé");

            }

        }

        catch(SQLException e){
            log.error(e.getMessage());
        }
    }

}

