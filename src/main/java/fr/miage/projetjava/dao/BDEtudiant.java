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

public class BDEtudiant {
    private static final Logger log = LogManager.getLogger(BDEtudiant.class);

    public void insertEtudiant(List<Etudiant> listeEtudiantCSV)
    {

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        try(Connection conn = ConnexionBD.connexionBD(); Statement stmt = conn.createStatement();){

            for (Etudiant etu : listeEtudiantCSV){
                int numE = etu.getNumE();
                String prenomE = etu.getPrenomE();
                String nomE = etu.getNomE();
                Parcours parcours = etu.getParcours();
                List<ResultatUE> resultatUE = etu.getResultatUE();

                log.info("numeE: "+numE +"\n prenomE "+prenomE+"\n nomE: "+nomE+"\n parcours: "+parcours);

                String requeteInsertEtudiant = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours )" +
                        "VALUES ( ?, ?, ?, ?);";

                PreparedStatement ajoutValues = conn.prepareStatement(requeteInsertEtudiant);
                ajoutValues.setInt(1, numE);
                ajoutValues.setString(2,prenomE);
                ajoutValues.setString(3,nomE);
                ajoutValues.setString(4,parcours.getNom());

                ajoutValues.executeUpdate();

                log.info("étudiant ajouté : " + etu);
            }

        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }
    }

}
