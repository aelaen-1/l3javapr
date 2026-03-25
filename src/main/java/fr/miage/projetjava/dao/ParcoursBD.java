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
     * @param connexion, String code, String nom, String mention: information nécessaire pour l'ajout du parcours dans la BD
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void insertParcours(Connection connexion, String code, String nom, String mention)
    {

        try{

            //requête qui va être exécuté

            String requeteInsertParcours = "INSERT INTO  Parcours (code, nom, mention)" +
                    "VALUES ( ?, ?, ?)";

            //PreparedStatement va permettre d'envoyer la requête sql
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertParcours);
            ajoutValues.setString(1,code);
            ajoutValues.setString(2,nom);
            ajoutValues.setString(3, mention);

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
     * @param connexion, ArrayList<Parcours> listeParcoursCSV
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