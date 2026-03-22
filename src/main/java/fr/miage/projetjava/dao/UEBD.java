package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.UE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;



/**
 * Classe UEBD : Gère l'insertion des UE dans la BD
 * 2 méthodes : void insertUE et void recuperationInformationListUE
 *
 * Cette classe permet de récupérer la liste d'UE issues du fichier csv dans UEDAO et
 * d'insérer les différentes UE dans la BD.
 *
 * Si l'insertion dans la base de données a échoué, alors un message d'erreur est renvoyé.
 *
 *
 *
 */
public class UEBD {


    private static final Logger log = LogManager.getLogger(EtudiantBD.class);

    /**
     * Méthode appelée par la méthode recuperationInformationListUE appartenant à la même classe
     *
     * Cette méthode va insérer les UE dans la base de données à partir des informations de l'UE passée en paramètre
     *
     * @param: String code, String intitule, int credit, String mention: informations qui doivent être ajoutées dans la BD
     *
     *
     */
    public static void insertUE(String code, String intitule, int credit, String mention)
    {


        // on se connecte à la bd
        try(Connection connexion = ConnexionBD.connexionBD()){


            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

            String requeteInsertUE = "INSERT INTO  UE (codeUE, intitule, credit, mention )" +
                    "VALUES ( ?, ?, ?, ?)";

            //PreparedStatement est un objet représentant une requête sql pré-compilé, elle va permettre d'envoyer la requête sql
            //pour être exécuté chaque ? de la requête doit être définie sur un type (int, String...)
            //prend en paramètre une requête sql qui contient des ? qui vont ensuite être remplacé par des valeurs
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertUE);
            //on indique au driver (ici JDBC) le type de chaque paramètres de la requête
            // et en premier c'est l'indice de où se trouve le paramètre dans la requête
            ajoutValues.setString(1, code);
            ajoutValues.setString(2,intitule);
            ajoutValues.setInt(3,credit);
            ajoutValues.setString(4,mention);

            //executeUpdate() va executer la requête sql qui est contenu dans l'objet ajoutValues
            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("UE "+ intitule +" ajouté : " );

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }



    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     * Cette méthode va récupérer les informations des UE données dans la liste d'UE
     * et va appeler pour chaque UE la méthode insertUE afin d'insérer une par une toutes
     * les UE dans la BD.
     *
     *
     * @param: ArrayList<UE> listeUECSV:  liste d'objets UE créés à partir des données du CSV
     *
     *
     */
    public static void recuperationInformationListUE (ArrayList<UE> listeUECSV)
    {
        for (UE ue : listeUECSV) {
            String code = ue.getCode();
            String intitule = ue.getIntitule();
            int credit = ue.getCredit();
            Mention mention = ue.getMention();

            insertUE(code,intitule,credit, mention.toString());
        }
    }
}
