package fr.miage.projetjava.dao;

import fr.miage.projetjava.database.ConnexionBD;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.ResultatUE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.lang.Integer.parseInt;


/**
 * Classe BDEtudiant : Gère l'insertion des étudiants dans la BD
 * 3 méthodes : void insertEtudiant, void recuperationInformationListEtudiant  et void recuperationInformationEtudiantCSV
 *
 * Cette classe est responsable de :
 * 1. Récupérer les informations transmises des fichiers CSV concernant les Étudiants
 * 2. Mettre ces informations sous le bon format
 * 3. Ajouter ces ifnormations dans la BD
 *
 * Si l'insertion dans la base de données a échoué alors un message d'erreur est renvoyé
 *
 *
 *
 */

public class BDEtudiant {
    private static final Logger log = LogManager.getLogger(BDEtudiant.class);


    /**
     * Méthode appelée  par  les deux autres méthodes de cette classe (recuperationInformationListEtudiant et recuperationInformationEtudiantCSV)
     *
     * Cette méthode va insérer les Étudiants dans la base de donnée à partir des informations de l'étudiant passé en paramètre
     *
     * @param int numE, String prenomE, String nomE, String parcours: informations qui doivent être ajoutées dans la BD
     *
     *
     */
    public void insertEtudiant(int numE, String prenomE, String nomE, String parcours)
    {

        // on se connecte à la bd
        try(Connection connexion = ConnexionBD.connexionBD()){

            log.info("numeE: "+numE +"\n prenomE "+prenomE+"\n nomE: "+nomE+"\n parcours: "+parcours);

            //requête qui va être exécuté
            // les ? sont là car on va utiliser PreparedStatement qui crée des objets preparedStatement permettant
            // d'envoyer des requêtes sql paramétrés à la BD

                    String requeteInsertEtudiant = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours )" +
                    "VALUES ( ?, ?, ?, ?);";

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
     * Méthode appelée depuis EtudiantCSV  dans la méthode ChargerEtudiant
     *
     * Cette méthode va récupérer les informations des étudiants donné dans la liste d'étudiant
     * et va appelé pour chaque étudiant la méthode insertEtudiant
     * afin d'insérer un par un les étudiants dans la BD.
     *
     *
     * @param List<Etudiant> listeEtudiantCSV:  liste d'objets Étudiant créés à partir des données du CSV
     *
     *
     */
    public void recuperationInformationListEtudiant (List<Etudiant> listeEtudiantCSV){
        for (Etudiant etu : listeEtudiantCSV) {
            int numE = etu.getNumE();
            String prenomE = etu.getPrenomE();
            String nomE = etu.getNomE();
            Parcours parcours = etu.getParcours();

            insertEtudiant(numE,prenomE,nomE,parcours.getNom());
        }
    }



    /**
     * Méthode appelée depuis EtudiantCSV dans la méthode ChargerEtudiant
     *
     * Cette méthode va récupérer les informations des étudiants donné dans la liste de liste d'étudiant
     * sortant directement du fichhier csv et va appelé pour chaque étudiant la méthode insertEtudiant
     * afin d'insérer un par un les étudiants dans la BD.
     *
     *
     *
     * @param List<List<String>> listeEtudiantCSV: liste d'objets Étudiant créés à partir des données du CSV
     *
     *
     */
    public void recuperationInformationEtudiantCSV(List<List<String>> listeEtudiantCSV) {
        for (int i = 0; i < listeEtudiantCSV.size() - 1; i++) {
            //récupération des différentes informations qui viennent du fichier CSV et qui sont nécessaire à la création des objets étudiants
            int numE = parseInt(listeEtudiantCSV.get(i + 1).get(0));
            String prenomE = listeEtudiantCSV.get(i + 1).get(1);
            String nomE = listeEtudiantCSV.get(i + 1).get(2);
            String nomParcours = listeEtudiantCSV.get(i + 1).get(3);

            insertEtudiant(numE,prenomE,nomE,nomParcours);

        }

    }

}
