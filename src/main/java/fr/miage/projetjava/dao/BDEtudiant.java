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

public class BDEtudiant {
    private static final Logger log = LogManager.getLogger(BDEtudiant.class);

    public void insertEtudiant(int numE, String prenomE, String nomE, String parcours)
    {

        // on se connecte à la bd et statement va être utilisé pour exécuter les requêtes SQL
        try(Connection conn = ConnexionBD.connexionBD()){


            log.info("numeE: "+numE +"\n prenomE "+prenomE+"\n nomE: "+nomE+"\n parcours: "+parcours);

            String requeteInsertEtudiant = "INSERT INTO  Etudiant (numE, prenomE, nomE, parcours )" +
                    "VALUES ( ?, ?, ?, ?);";

            PreparedStatement ajoutValues = conn.prepareStatement(requeteInsertEtudiant);
            ajoutValues.setInt(1, numE);
            ajoutValues.setString(2,prenomE);
            ajoutValues.setString(3,nomE);
            ajoutValues.setString(4,parcours);

            ajoutValues.executeUpdate();
            log.info("étudiant ajouté : " );

        }
        catch(SQLException e){
            log.error(e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de donnée");
        }
    }

    public void recuperationInformationListEtudiant (List<Etudiant> listeEtudiantCSV){
        for (Etudiant etu : listeEtudiantCSV) {
            int numE = etu.getNumE();
            String prenomE = etu.getPrenomE();
            String nomE = etu.getNomE();
            Parcours parcours = etu.getParcours();

            insertEtudiant(numE,prenomE,nomE,parcours.getNom());
        }
    }


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
