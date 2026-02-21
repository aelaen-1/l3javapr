package fr.miage.projetjava.CSV;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.CSV.ChargementCSV;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class EtudiantCSV {
    private Parcours creerParcours(String nomParcours)
    {
        Parcours parcours;
        if (nomParcours == "IO" || nomParcours == "MIAGE")
        {
            parcours = new Parcours(nomParcours, Mention.MIASHS);
        }
        else if(nomParcours == "Biologie")
        {
            parcours = new Parcours(nomParcours, Mention.BIOLOGIE);
        }
        else if(nomParcours == "Chimie")
        {
            parcours = new Parcours(nomParcours, Mention.CHIMIE);
        }
        else if(nomParcours == "Informatique")
        {
            parcours = new Parcours(nomParcours, Mention.INFORMATIQUE);
        }
        else
        {
            parcours = new Parcours(nomParcours, Mention.PHYSIQUE);
        }
        return parcours;
    }

    //avec la liste de liste de données du fichier CSV, la méthode va créer un à un tous les étudiants et les stocker dans une liste d'étudiant
    private List<Etudiant> creationEtudiant(List<List<String>> data)
    {
        List<Etudiant> listeEtudiants = new ArrayList<>();

        for (int i = 0; i < data.size()-1; i++) {
            //récupération des différentes informations qui viennent du fichier CSV et qui sont nécessaire à la création des objets étudiants
            int id = parseInt(data.get(i+1).get(0));
            String prenomE = data.get(i+1).get(1);
            String nomE = data.get(i+1).get(2);
            String nomParcours = data.get(i+1).get(3);

            //création de l'objet parcours de l'étudiant
            Parcours parcours = creerParcours(nomParcours);


            //création de l'étudiant
            Etudiant e = new Etudiant(id, nomE, prenomE, parcours );

            //ajout de l'étudiant à la liste d'étudiant
            listeEtudiants.add(e);

        }
        return listeEtudiants;
    }


    public List<Etudiant> ChargerEtudiant(String cheminFichier )
    {
        //appel à la méthode pour charger un fichier étudiant et recevoir les informations sous formes de listes, une fois les donneEtudiantCSV
        //stocker on va pouvoir créer une liste d'objets Étudiants
        ChargementCSV chargerFichierCSV = new ChargementCSV();
        List<List<String>> donneeEtudiantCSV  = chargerFichierCSV.chargementFichierCSV(cheminFichier);


        //va stocker les étudiants qui vont être créer à partir du fichier csv et de l'appel de la méthode creationEtudiant
        List<Etudiant> listeEtudiants;
        listeEtudiants = creationEtudiant(donneeEtudiantCSV);

        return listeEtudiants;
    }
}