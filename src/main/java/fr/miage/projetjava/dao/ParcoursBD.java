package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Classe ParcoursBD : Gère l'insertion des parcours dans la BD
 * 2 méthodes : static void insertParcours et static void recuperationInformationListParcours
 *
 * Cette classe permet de récupérer la liste de parcours fabriquer à partie du fichier csv dans ParcoursDAO et
 *  d'insérer les différents étudiants dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */

public class ParcoursBD {
    private static final Logger log = LogManager.getLogger(ParcoursBD.class);


    /**
     * Méthode appelée dans recuperationInformationListParcours
     *
     * Cette méthode va insérer les parcours dans la base de donnée à partir des informations du parcours passé en paramètre
     *
     * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
     *
     * @param : String code, String nom, String mention: information nécessaire pour l'ajout du parcours dans la BD
     *
     */
    public static void insertParcours(String code, String nom, String mention)
    {

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
        //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
        // à la BD
        try(Connection connexion = ConnexionBD.connexionBD()){

            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

            String requeteInsertParcours = "INSERT INTO  Parcours (code, nom, mention)" +
                    "VALUES ( ?, ?, ?)";

            //PreparedStatement est un objet représentant une requête sql pré-compilé, elle va permettre d'envoyer la requête sql
            //pour être exécuté chaque ? de la requête doit être définie sur un type (int, String...)
            //prend en paramètre une requête sql qui contient des ? qui vont ensuite être remplacé par des valeurs
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertParcours);
            //on indique au driver (ici JDBC) le type de chaque paramètres de la requête
            // et en premier c'est l'indice de où se trouve le paramètre dans la requête
            ajoutValues.setString(1,code);
            ajoutValues.setString(2,nom);
            ajoutValues.setString(3, mention);

            //executeUpdate() va executer la requête sql qui est contenu dans l'objet ajoutValues
            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("parcours " +  code  +" ajouté : ");



        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }


    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     *
     * Cette méthode va récupérer les informations des parcours donné dans la liste de parcours
     * et va appeler pour chaque parcours la méthode insertParcours
     * afin d'insérer un par un les parcours dans la BD.
     *
     *
     * @param : ArrayList<Parcours> listeParcoursCSV:  liste d'objets Parcours créés à partir des données du CSV
     *
     *
     */
    public static void recuperationInformationListParcours (ArrayList<Parcours> listeParcoursCSV)
    {
        for (Parcours parcours : listeParcoursCSV) {
            String code = parcours.getNom();
            Mention mention = parcours.getMention();

            insertParcours(code, null,  mention.toString());
        }
    }
}
