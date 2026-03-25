package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Semestre;
import fr.miage.projetjava.model.StatutUE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EtudiantUpdateBD {
    private static final Logger log = LogManager.getLogger(EtudiantUpdateBD.class);

    /**
     * Méthode appelée par la méthode validerUE et echoueUE de la classe ScolaritéService
     * Cette méthode mettre à jour les résultats des UE des étudiants dans la bd selon ce qui est modifié depuis l'interface.
     *
     * @param connexion, int numE, String codeUE, StatutUE statut: informations pour permettre la MAJ
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void updateResultatUE(Connection connexion, int numE, String codeUE, StatutUE statut)
    {


        try{

            String requeteInsertEtudiant = "UPDATE ResultatUE  set statut = ?  where numE = ? and codeUE = ? and statut = ?";

            //va permettre d'exéctuer la requête
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertEtudiant);

            ajoutValues.setString(1, statut.toString());
            ajoutValues.setInt(2, numE);
            ajoutValues.setString(3,codeUE);
            ajoutValues.setString(4,"En cours");


            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("résultat étudiant MAJ:" + numE + "  MAJ " + codeUE + "nouveau statut : " + statut );

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }

    /**
     * Méthode appelée par la méthode passerSemestre de la classe ScolaritéService
     *
     * Cette méthode mettre à jour le semestre des étudiants dans la bd selon ce qui est modifié depuis l'interface.
     *
     * @param connexion, int numE, Semestre semestre: informations pour permettre la MAJ
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     */
    public static void updateSemestre(Connection connexion, int numE, Semestre semestre)
    {


        try{

            String requeteInsertEtudiant = "UPDATE Etudiant  set semestre = ?  where numE = ?";

            //va permettre d'exéctuer la requête
            PreparedStatement ajoutValues = connexion.prepareStatement(requeteInsertEtudiant);

            ajoutValues.setString(1, semestre.toString());
            ajoutValues.setInt(2, numE);

            //utilisation de executeUpdate à la place de execute car ici on fait une MAJ des informations de la BD
            ajoutValues.executeUpdate();
            log.info("Semestre étudiant MAJ :" + numE + "  Semestre " + semestre );

        }
        catch(SQLException e){
            log.error(e.getMessage());
        }
    }
}
