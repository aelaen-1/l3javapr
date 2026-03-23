package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.Semestre;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Classe EtudiantBD : Gère l'insertion des étudiants dans la BD
 * 2 méthodes : static void insertEtudiant et static void recuperationInformationListEtudiant
 *
 * Cette classe permet de récupérer la liste d'étudiants fabriquée à partir du fichier csv et
 * d'insérer les différents étudiants dans la BD.
 *
 * Si l'insertion dans la base de données a échoué, alors un message d'erreur est renvoyé
 *
 *
 *
 */

public class EtudiantBD {


    private static final Logger log = LogManager.getLogger(EtudiantBD.class);

    /**
     * Méthode appelée par la méthode recuperationInformationListEtudiant de la même classe
     *
     * Cette méthode va insérer les étudiants dans la bd à partir des informations de l'étudiant passé en paramètre.
     *
     * @param: Connection connexion, int numE, String prenomE, String nomE, String parcours: informations qui doivent être ajoutées dans la BD
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void insertEtudiant(Connection connexion, int numE, String prenomE, String nomE, String parcours, String semestre)
    {


        try{

            log.info("numeE: "+numE +"\n prenomE "+prenomE+"\n nomE: "+nomE+"\n parcours: "+parcours);

            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

            String requeteInsertEtudiant = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours, semestre)" +
                    "VALUES ( ?, ?, ?, ?, ?)";

            //PreparedStatement est un objet représentant une requête sql pré-compilé, elle va permettre d'envoyer la requête sql
            //pour être exécuté, chaque ? de la requête doit être définie sur un type (int, String...)
            //On utilise PreparedSatetment car les valeurs changent à chaque fois, et de plus il permet de bien formater les différentes valeurs
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertEtudiant);
            //on indique au driver (ici JDBC) le type de chaque paramètres de la requête
            // et en premier c'est l'indice de où se trouve le paramètre dans la requête
            ajoutValues.setInt(1, numE);
            ajoutValues.setString(2,prenomE);
            ajoutValues.setString(3,nomE);
            ajoutValues.setString(4,parcours);
            ajoutValues.setString(5, semestre);

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
     * Cette méthode va récupérer les informations des étudiants données dans la liste d'étudiants
     * et va appeler pour chaque étudiant la méthode insertEtudiant
     * afin d'insérer un par un les étudiants dans la BD.
     *
     *
     * @param: ArrayList<Etudiant> listeEtudiantCSV, Connection connexion
     * listeEtudiantCSV:  liste d'objets Étudiant créés à partir des données du CSV
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     *
     */
    public static void recuperationInformationListEtudiant (Connection connexion, ArrayList<Etudiant> listeEtudiantCSV)
    {
        for (Etudiant etudiant : listeEtudiantCSV) {
            int numE = etudiant.getNumE();
            String prenomE = etudiant.getPrenomE();
            String nomE = etudiant.getNomE();
            Parcours parcours = etudiant.getParcours();
            Semestre semestre = etudiant.getSemestreCourant();

            insertEtudiant(connexion, numE,prenomE,nomE,parcours.getNom(), semestre.toString());
        }
    }

}
