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
        //Properties est une classe java qui permet de stocker des paires clé/valeur et
        //elle sait lire les fichiers .properties.
        //Pour l'instant il est vide
        Properties properties = new Properties();

        //FileInputStream ouvre le fichier en lecture brute (octet par octet)
        //try-with-ressources, java va fermer automatiquement le fichier à la fin du bloc
        try(FileInputStream file = new FileInputStream("src/main/resources/config.properties")){
            //Ici le fichier est vraiment lu et parsé. L'objet properties lit le fichier et
            //transforme chaque ligne clé=valeur du fichier en entrée accessible en java
            properties.load(file);

            //db.URL contient une URL de type String qui contient les paramètres permettant la connexion à la BD,
            // et getProperty permet de récupérer la valeur associée à la clé db.URL
            //url = jdbc:oracle:thin:@adresse:port:SID
            //jdbc est une API de bas niveau de Java et permet à java un accès à une BD
            //c'est lui qui permet de se connecter et d'intéragir avec une BD
            //jdbc permet d'utiliser la même API pour accéder aux BD grâce à l'utilisation de pilotes (drivers)
            //oracle correspond au type de BD utilisé
            //thin correspond au driver
            //@adresse = nom du domaine du serveur où tourne Oracle
            //port correspond au port du réseau
            //SID nom de l'instance Oracle sur le serveur
            String URL = properties.getProperty("db.URL");

            //db.USER contient une String avec l'identifiant de connexion à la BD, et getProperty permet de récupérer la valeur
            //associée à la clé db.USER
            String USER = properties.getProperty("db.USER");

            //db.PASSWORD contient une String avec le mdp de connexion à la BD, et getProperty permet de récupérer la valeur
            //associée à la clé db.PASSWORD
            String PASSWORD = properties.getProperty("db.PASSWORD");

            // cette ligne permet d'établir une connexion avec la bd
            //DriverManager est une classe donnant accès à plusieurs services permettant de gérer plusieurs drivers JDBC
            //la méthode statique getConnection de DriverManager, renvoie un objet de type connection et
            // permet la connexion à la BD
            //Connection est une interface qui va permettre à connexionBD de représenter le canal ouvert
            // entre la bd et le code, tant que connexionBD existe et est ouvert alors on peut envoyer des
            // requêtes sql
            Connection connexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

            log.info("Connexion à la base de donnée réussi");
            return connexionBD;
        }
        // le RuntimException permet d'arrêter le programme lorsqu'il y a une erreur qui n'est pas géré
        catch (IOException e){
            log.error(e.getMessage());
            //Exception que java n'oblige pas à gérer, RuntimeException est la classe mère de toutes les exceptions non vérifiées
            throw new RuntimeException("Problème dans le chargement de config.properties");
        }
        catch(SQLException e){
            log.error(e.getMessage());
            //DriverManager.getConnection() peut lancer une SQLException mais connexionBD doit renvoyer une Connection,
            // on enveloppe donc l'erreur dans une RuntimeExcpetion pour la propager
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
