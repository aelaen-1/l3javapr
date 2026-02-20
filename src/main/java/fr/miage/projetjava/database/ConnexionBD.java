package fr.miage.projetjava.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionBD {
    //contient une String de connexion à la BD, si la base de donnée testBD.db n'existe pas elle seras créé automatiquement
    private static final String URL = "jdbc:sqlite:projetjavaBD.db";
    private static final Logger log = LogManager.getLogger(ConnexionBD.class);


    public static Connection connexionBD()
    {
        try{
            //établit une connexion à la BD testBD.db
            Connection connexionBD = DriverManager.getConnection(URL);

            //pour avoir la possibilité d'avoir des clés étrangères
            try (Statement stmt = connexionBD.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            log.info("Connexion à la base de donnée réussi");
            return connexionBD;
        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
