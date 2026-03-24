package fr.miage.projetjava.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Classe ConnexionBD : Gère la connexion à la BD et la crée si elle n'existe pas
 * Une seule méthode static : Connection connexionBD()
 * Cette classe est responsable de :
 * 1. Établir une connexion à la BD
 * 2. Donner la possibilité d'avoir des clés étrangères
 * 3. Renvoyer la connexion à la BD
 *
 * Si la connexion a échoué, un message d'erreur est renvoyé
 * Cette classe utilise les identifiants présents dans le fichier config.properties pour se connecter à la BD

 */

public class ConnexionBD {

    private static final Logger log = LogManager.getLogger(ConnexionBD.class);

    public static Connection connexionBD()
    {

        Properties properties = new Properties();

        try(FileInputStream file = new FileInputStream("src/main/resources/config.properties")){

            properties.load(file);

            //db.URL contient une URL de type String qui contient les paramètres permettant la connexion à la BD,
            String URL = properties.getProperty("db.URL");

            //db.USER contient une String avec l'identifiant de connexion à la BD
            String USER = properties.getProperty("db.USER");

            //db.PASSWORD contient une String avec le mdp de connexion à la BD
            String PASSWORD = properties.getProperty("db.PASSWORD");

            // cette ligne permet d'établir une connexion avec la bd
            Connection connexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

            log.info("Connexion à la base de donnée réussi");
            return connexionBD;
        }
        catch (IOException e){
            log.error(e.getMessage());
            throw new RuntimeException("Problème dans le chargement de config.properties");
        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
