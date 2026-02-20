package fr.miage.projetjava.dao;

import fr.miage.projetjava.database.ConnexionBD;
import fr.miage.projetjava.model.Etudiant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BDEtudiant {
    private static final Logger log = LogManager.getLogger(ConnexionBD.class);

    public void insertEtudiant()
    {
        String requeteNewTable = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours, resultatUE )" +
                                "VALUES (1, 'nom', 'pr','MIASHS','U1');";
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){
            // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL

            stmt.execute(requeteNewTable);
            log.info("table ajouté");
        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }
    }

}
