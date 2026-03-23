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
 * Cette classe permet de récupérer la liste de parcours issus du fichier csv dans ParcoursDAO et
 *  d'insérer les différents parcours dans la BD
 *
 * Si l'insertion dans la base de données, a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */

public class ParcoursBD {
    private static final Logger log = LogManager.getLogger(ParcoursBD.class);


    /**
     * Méthode appelée dans recuperationInformationListParcours
     *
     * Cette méthode va insérer les parcours dans la bd à partir des informations du parcours passé en paramètre
     *
     * Si l'insertion dans la base de données, a échoué alors un message d'erreur est renvoyé
     *
     * @param : Connection connexion, String code, String nom, String mention: information nécessaire pour l'ajout du parcours dans la BD
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void insertParcours(Connection connexion, String code, String nom, String mention)
    {

        try{

            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

            String requeteInsertParcours = "INSERT INTO  Parcours (code, nom, mention)" +
                    "VALUES ( ?, ?, ?)";

            //PreparedStatement est un objet représentant une requête sql pré-compilé, elle va permettre d'envoyer la requête sql
            //pour être exécuté chaque ? de la requête doit être définie sur un type (int, String...)
            //On utilise PreparedSatetment car les valeurs changent à chaque fois, et de plus il permet de bien formater les différentes valeurs
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
     * Cette méthode va récupérer les informations des parcours données dans la liste de parcours
     * et va appeler pour chaque parcours la méthode insertParcours
     * afin d'insérer un par un les parcours dans la BD.
     *
     *
     * @param : Connection connexion, ArrayList<Parcours> listeParcoursCSV
     * listeParcoursCSV:  liste d'objets Parcours créés à partir des données du CSV
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void recuperationInformationListParcours (Connection connexion, ArrayList<Parcours> listeParcoursCSV)
    {
        for (Parcours parcours : listeParcoursCSV) {
            String code = parcours.getNom();
            Mention mention = parcours.getMention();

            insertParcours(connexion, code, null,  mention.toString());
        }
    }
}
