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

        String requeteAjoutTable = "CREATE TABLE IF NOT EXISTS Etudiant ("
                                    + "numE INTEGER PRIMARY KEY,"
                                    + "prenomE text,"
                                    +    "nomE text,"
                                    + "parcours text," //a mettre en clé étrangère
                                    + "resultatUE text" //mettre en lcé étrangère aussi
                                    + ");";
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){
            // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL

            stmt.execute(requeteAjoutTable);
            BDEtudiant ajout = new BDEtudiant();
            ajout.insertEtudiant();
            log.info("table créé");
        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
