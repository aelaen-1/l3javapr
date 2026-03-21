package fr.miage.projetjava.dao;


import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe BDEtudiant : Gère l'insertion des étudiants dans la BD
 * 2 méthodes : void insertEtudiant et void recuperationInformationEtudiantCSV
 *
 * Cette classe permet de récupérer la liste d'étudiant fabriquer à partie du fichier csv dans EtudiantDAO et
 * d'insérer les différents étudiants dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */

public class EtudiantBD {


    private static final Logger log = LogManager.getLogger(EtudiantBD.class);

    /**
     * Méthode appelée  par  l'autre méthode de cette classe
     *
     * Cette méthode va insérer les Étudiants dans la base de donnée à partir des informations de l'étudiant passé en paramètre
     *
     * @param int numE, String prenomE, String nomE, String parcours: informations qui doivent être ajoutées dans la BD
     *
     *
     */
    public static void insertEtudiant(int numE, String prenomE, String nomE, String parcours)
    {


        // on se connecte à la bd
        try(Connection connexion = ConnexionBD.connexionBD()){

            log.info("numeE: "+numE +"\n prenomE "+prenomE+"\n nomE: "+nomE+"\n parcours: "+parcours);

            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

            String requeteInsertEtudiant = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours )" +
                    "VALUES ( ?, ?, ?, ?)";

            //PreparedStatement est un objet représentant une requête sql pré-compilé, elle va permettre d'envoyer la requête sql
            //pour être exécuté chaque ? de la requête doit être définie sur un type (int, String...)
            //prend en paramètre une requête sql qui contient des ? qui vont ensuite être remplacé par des valeurs
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertEtudiant);
            //on indique au driver (ici JDBC) le type de chaque paramètres de la requête
            // et en premier c'est l'indice de où se trouve le paramètre dans la requête
            ajoutValues.setInt(1, numE);
            ajoutValues.setString(2,prenomE);
            ajoutValues.setString(3,nomE);
            ajoutValues.setString(4,parcours);

            //executeUpdate() va executer la requête sql qui est contenu dans l'objet ajoutValues
            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("étudiant ajouté : " );

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }



    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     *
     * Cette méthode va récupérer les informations des étudiants donné dans la liste d'étudiant
     * et va appelé pour chaque étudiant la méthode insertEtudiant
     * afin d'insérer un par un les étudiants dans la BD.
     *
     *
     * @param ArrayList<Etudiant> listeEtudiantCSV:  liste d'objets Étudiant créés à partir des données du CSV
     *
     *
     */
    public static void recuperationInformationListEtudiant (ArrayList<Etudiant> listeEtudiantCSV)
    {
        for (Etudiant etudiant : listeEtudiantCSV) {
            int numE = etudiant.getNumE();
            String prenomE = etudiant.getPrenomE();
            String nomE = etudiant.getNomE();
            Parcours parcours = etudiant.getParcours();

            insertEtudiant(numE,prenomE,nomE,parcours.getNom());
        }
    }


}
