package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.UE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Classe UEPrerequisBD : Gère l'insertion des UE prérequis à une autre UE dans la BD
 * 2 méthodes : static void insertUEPrerequis et static void recuperationInformationListUEPre
 *
 * Cette classe permet de récupérer la liste d'UE fabriqué à partir du fichier csv dans UEDAO et
 *  d'insérer les différentes UE prérequis à d'autres UE dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */



public class UEPrerequisBD {

    private static final Logger log = LogManager.getLogger(UEPrerequisBD.class);


    /**
     * Méthode appelée dans recuperationInformationListUEPre
     *
     * Cette méthode va insérer les UE prérequis pour un parcours dans la BD à partir des
     * informations de la liste d'UE prérequis donné en paramètre
     *
     * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
     *
     * @param : String nomParcours, ArrayList<UE>: information nécessaire pour l'ajout des UE prérequis dans la BD
     *
     */
    public static void insertUEPrerequis(String codeUE, ArrayList<UE> uePrerequis)
    {

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
        //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
        // à la BD
        try(Connection connexion = ConnexionBD.connexionBD()){

            //on va parcourir la liste des UE prerequis de l'UE mis en paramètre et on les insérer dans la table
            for (UE ue : uePrerequis){
                String requeteInsertUEPrere = "INSERT INTO  UEprerequis (codeUE, codeUEPrerequis)" +
                        "VALUES ( ?, ?)";


                PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertUEPrere);

                ajoutValues.setString(1,codeUE);
                ajoutValues.setString(2,ue.getCode());

                ajoutValues.executeUpdate();
                log.info("UE : " + codeUE + "ue prerequis: " +  ue.getCode()  +" ajouté ");
            }
        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }


    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     *
     * Cette méthode va récupérer les informations des UE donné dans la liste d'UE
     * et va appeler pour chaque UE la méthode insertUEPrerequis si celle-ci possède des prérequis
     * afin d'insérer dans la table UEprerequis toutes les UES prérequis concernant l'UE.
     *
     *
     * @param : ArrayList<UE> listeUECSV:  liste d'objets UE créés à partir des données du CSV
     *
     *
     */
    public static void recuperationInformationListUEPre(ArrayList<UE> listeUECSV)
    {
        for (UE ue : listeUECSV) {
            ArrayList<UE> uePrerequis = ue.getUEprerequis();
            String codeUE = ue.getCode();

            //si l'UE contient des prérequis alors on va les ajouter dans la BD
            if(!uePrerequis.isEmpty()){
                insertUEPrerequis(codeUE, uePrerequis);
            }
        }
    }
}
