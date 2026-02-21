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

public class BDParcours {
    private static final Logger log = LogManager.getLogger(BDParcours.class);


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
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){
            for(String requete : requeteAjoutParcours){
                stmt.executeUpdate(requete);
                log.info("parcours ajouté : ");
            }


        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }
    }
}
