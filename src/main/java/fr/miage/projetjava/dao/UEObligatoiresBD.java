package fr.miage.projetjava.dao;


import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe UEObligatoires : Gère l'insertion des UE obligatoires liées à un parcours dans la BD
 * 2 méthodes : static void insertUEObligatoires et static void recuperationInformationListUEObli
 *
 * Cette classe permet de récupérer la liste d'UE fabriquée à partir du fichier csv dans UEDAO et
 *  d'insérer les différentes UE obligatoires d'un parcours dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */
public class UEObligatoiresBD {

    private static final Logger log = LogManager.getLogger(UEObligatoiresBD.class);


    /**
     * Méthode appelée dans recuperationInformationListUEObli
     *
     * Cette méthode va insérer les UE obligatoires pour un parcours dans la BD à partir des
     * informations de la liste d'UE obligatoire donnée en paramètre
     *
     * Si l'insertion dans la base de données a échoué, alors un message d'erreur est renvoyé
     *
     * @param : String nomParcours, ArrayList<UE>: information nécessaire pour l'ajout des UE obligatoires dans la BD
     *
     */
    public static void insertUEObligatoires(String nomParcours, ArrayList<UE> ueObligatoire)
    {

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        //Statement est une classe permettant l'exécution des différentes requêtes avec l'appel de la méthode execute
        //qui lui appartient. D'abord createStatement créé un objet Statement qui va ensuite pouvoir envoyer les requêtes sql
        // à la BD
        try(Connection connexion = ConnexionBD.connexionBD()){

            //on va parcourir la liste des UE obligatoire du parcours et les insérer dans la table
            for (UE ue : ueObligatoire){
                String requeteInsertUEObli = "INSERT INTO  UEObligatoire (nomParcours, UE)" +
                        "VALUES ( ?, ?)";


                PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertUEObli);

                ajoutValues.setString(1,nomParcours);
                ajoutValues.setString(2,ue.getCode());

                ajoutValues.executeUpdate();
                log.info("parcours " + nomParcours + "ue: " +  ue.getCode()  +" ajouté ");
            }
        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }


    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     *
     * Cette méthode va récupérer les informations des parcours donnés dans la liste de parcours
     * et va appeler pour chaque parcours la méthode insertUEObligatoires
     * afin d'insérer dans la table UEObligatoire toutes les UES obligatoires liées à un parcours
     *
     *
     * @param : ArrayList<Parcours> listeParcoursCSV:  liste d'objets Parcours créés à partir des données du CSV
     *
     *
     */
    public static void recuperationInformationListUEObli(ArrayList<Parcours> listeParcoursCSV)
    {
        for (Parcours parcours : listeParcoursCSV) {
            ArrayList<UE> ueObligatoire = parcours.getUEObligatoire();
            String nomParcours = parcours.getNom();

            insertUEObligatoires(nomParcours, ueObligatoire);
        }
    }

}
