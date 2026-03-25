package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.ResultatUE;
import fr.miage.projetjava.model.UE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Classe ResultatUEBD : Gère l'insertion des résultats aux UE des étudiants dans la BD
 *
 * 2 méthodes : static void insertResultatUE et static void recuperationInformationListResultatUE
 *
 * Cette classe permet de récupérer la liste d'étudiants fabriquée à partir du fichier csv et
 * d'insérer les différents résultats aux UE des étudiants dans la BD
 *
 * Si l'insertion dans la base de données a échoué, alors un message d'erreur est renvoyé
 */

public class ResultatUEBD {

    private static final Logger log = LogManager.getLogger(ResultatUEBD.class);


    /**
     * Méthode appelée dans recuperationInformationListResultatUE
     *
     * Cette méthode va insérer les résultats des UE des étudiants dans la base de données
     *
     * Si l'insertion dans la base de données a échoué, alors un message d'erreur est renvoyé
     *
     * @param :Connection connexion, int numE, String codeUE, int annee, String semestre, String statut: information nécessaire pour l'ajout des résultats à une UE dans la BD
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void insertResultatUE(Connection connexion, int numE, String codeUE, int annee, String semestre, String statut)
    {

        try{
            String requeteInsertResultatUE = "INSERT INTO  ResultatUE (codeUE, numE, annee, semestre, statut)" +
                    "VALUES ( ?, ?, ?, ?, ?)";


            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertResultatUE);

            ajoutValues.setString(1,codeUE);
            ajoutValues.setInt(2,numE);
            ajoutValues.setInt(3,annee);
            ajoutValues.setString(4,semestre);
            ajoutValues.setString(5,statut);


            ajoutValues.executeUpdate();
            log.info("Étudiant : " + numE + " resultat ue " +  codeUE  +" ajouté ");

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }



    /**
     * Méthode appelée depuis EtudiantBD dans la méthode supprimerEtudiant
     *
     * Cette méthode va supprimer les resultats d'UE de l'étudiant de la BD.
     *
     *
     * @param  Connection connexion, int numE
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * numE : numéro de l'étudiant qui va être supprimé.
     */
    public static void supprimerResultatUE(Connection connexion, int numE){
        try{
            log.info("Les résultats d'UE de l'étudiant, numeE: "+numE +"\n vont être supprimés");

            //requête qui va être exécuté
            String requeteSupResultatUE = "Delete From  ResultatUE  where numE = ?";

            //PreparedStatement va permettre d'envoyer la requête sql
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteSupResultatUE);
            //on indique le type de chaque paramètres de la requête
            // et en premier c'est l'indice de où se trouve le paramètre dans la requête
            ajoutValues.setInt(1, numE);


            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("Résultats supprimés " );

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }

    /**
     * Méthode appelée depuis InsertBD dans la méthode insertionDonneeBD
     *
     * Cette méthode va récupérer les informations des résultats d'UE des étudiants à partir de la liste d'étudiants
     * donnée en paramètre.
     * Si un étudiant possède des résultats pour au moins une UE, la méthode va appeler pour chaque UE la méthode insertResultatUE
     * afin d'insérer dans la table ResultatUE les informations concernant ce résultat d'UE pour cet étudiant.
     *
     *
     * @param : Connection connexion,  ArrayList<Etudiant> listeEtudiantCSV:  liste d'objets Etudiant créés à partir des données du CSV
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     *
     */
    public static void recuperationInformationListResultatUE(Connection connexion, ArrayList<Etudiant> listeEtudiantCSV)
    {
        for (Etudiant etudiant : listeEtudiantCSV) {
            ArrayList<ResultatUE> listeResultatUE = etudiant.getResultatsUE();
            int numE = etudiant.getNumE();

            //si l'étudiant possède au moins un résultat à une UE alors on va l'ajouter à la BD
            if(!listeResultatUE.isEmpty()){
                for (ResultatUE resultatUE : listeResultatUE){
                    UE ue = resultatUE.getUe();
                    String codeUE = ue.getCode();

                    int annee = parseInt(resultatUE.getAnnee());
                    String semetre = resultatUE.getSemestre().toString();
                    String statut = resultatUE.getStatut().toString();

                    insertResultatUE(connexion, numE,  codeUE, annee, semetre,  statut);

                }
            }
        }
    }

}