package fr.miage.projetjava.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.lookup.RuntimeStrSubstitutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe ConnexionBD : Gère la connexion à la BD et la crée si elle n'existe pas
 * Une seule méthode static : Connection connexionBD()
 * Cette classe est responsable de :
 * 1. Établir une connexion à la BD
 * 2. Donner la possibilité d'avoir des clés étrangères
 * 3. Renvoyer la connexion à la BD
 *
 * Si la connexion a échoué un message d'erreur est renvoyé
 *
 *
 *
 */



public class ConnexionBD {
    //contient une String de connexion à la BD, si la base de donnée testBD.db n'existe pas elle seras créé automatiquement,
    //static car la base de données est la même pour tous et ca permet de ne pas avoir à instancier d'objet pour pouvoir faire une connexion à la BD
    //final car la BD reste la même du début à la fin, il ne faut donc pas qu'elle puisse être modifié
    // SQLite JDBC driver (librairie qui permet à JAVA de communiquer avec SQLite, mis dans les dépendances)
    // Ce driver nous permet de charger une base de données avec jdbc:sqlite:
    //jdbc indique que c'est une URL jdbc et nous donne donc le protocole à utiliser lors de la connexion à la BD
    // et sqlite indique le type de BD et donc le driver a utilisé, ici donc sqlite
    //:projetjavaBD.db indique le nom de la BD
    private static final String URL = "jdbc:sqlite:projetjavaBD.db";

    private static final Logger log = LogManager.getLogger(ConnexionBD.class);


    /**
     * Elle permet d'établir une connexion à la Base de données présente dans URL.
     * Elle est appelée depuis les classes dans le package dao (pour le moment BDEtudiant et BDParcours)
     *

     * @return : Une connexion à la BD de type Connection. Connection est une interface
     *         Retourne une erreur si la connexion à la BD a échoué ou si le sta
     */
    public static Connection connexionBD()
    {
        try{
            //établit une connexion à la BD testBD.db
            //DriverManager est une classe donnant accès à plusieurs services permettant de gérer plusieurs drivers JDBC
            //on va donc utiliser la méthode statique getConnection de DriverManager, qui renvoie un objet de type connection,
            //et qui va tenter d'établir une connexion avec la BD
            //Connection est une interface qui va permettre ensuite de faire appel à deux méthodes
            // (preparedStatement et createStatement, renvoie type Statement) qui vont permettre ensuite de pouvoir
            // envoyer des requêtes sql à la BD
            Connection connexionBD = DriverManager.getConnection(URL);

            //pour avoir la possibilité d'avoir des clés étrangères
            //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
            //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
            // à la BD
            try (Statement stmt = connexionBD.createStatement()) {
                //va permettre de pouvoir gérer les clés étrangères
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            log.info("Connexion à la base de donnée réussi");
            return connexionBD;
        }
        // le RuntimException permet d'arrêter le programme lorsqu'il y a une erreur qui n'est pas géré
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }

    }
}
