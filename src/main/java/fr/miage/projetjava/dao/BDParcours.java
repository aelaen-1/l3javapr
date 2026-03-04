package fr.miage.projetjava.dao;

import fr.miage.projetjava.database.ConnexionBD;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.ResultatUE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Classe BDParcours : Gère l'insertion des parcours dans la BD
 * 1 méthodes : void insertParcours
 *
 * Insère seulement les requêtes toutes faites des parcours dans la BD
 *
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */


public class BDParcours {
    private static final Logger log = LogManager.getLogger(BDParcours.class);



    /**
     * Méthode appelée dans EtudiantCSV
     *
     * Cette méthode va insérer les parcours dans la base de donnée à partir des informations de l'étudiant passé en paramètre
     *
     * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
     *
     */
    public void insertParcours()
    {
        String[] requeteAjoutParcours = {
                "delete from Etudiant; ",
                "delete from Parcours; ",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'MIAGE', 'MIASHS' );",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'IO', 'MIASHS' );",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'Biologie', 'Biologie' );",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'Physique', 'Physique' );",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'Chimie', 'Chimie' );",
                "INSERT INTO  Parcours (nom, mention) VALUES ( 'Informatique', 'Informatique' );"
        };
        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
        //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
        // à la BD
        try(Connection connexion = ConnexionBD.connexionBD(); Statement stmt = connexion.createStatement();){

            //boucle pour parcourir le tableau de String et exécuté une à une toutes les requêtes car stmt.execute
            // peut exécuter que un à un toutes les requêtes
            for(String requete : requeteAjoutParcours){
                stmt.executeUpdate(requete);
                log.info("parcours ajouté : ");
            }


        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }
}
