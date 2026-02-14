package fr.miage.projetjava;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class ChargementCSV {

    //Va lire le fichier csv et récupérer ligne par ligne les information du fichier pour les stocker dans une liste de liste de String
    private List<List<String>> lectureStockInfoCSV (BufferedReader br)
    {
        // va stocker les différentes lignes du fichier csv
        List<List<String>> donneeCSV = new ArrayList<>();

        //utiliser pour quand on va lire ligne par ligne le fichier csv qui est dans le buffer br
        String line;

        try
        {
            while ((line = br.readLine()) != null)
            {
                //line = 1,Prise,Bracelet,IO -> on stocke cette ligne dans un tableau de String et sépare les mots à
                // dans ce tableau chaque virgule qu'il y a dans line pour que valeur donne ["1","Prise","Bracelet","IO"]
                String[] valeur = line.split(",");

                //transforme le tableau de String en une liste de String
                List<String> lineData = Arrays.asList(valeur);

                //ajout de la ligne de csv dans donneeCSV
                donneeCSV.add(lineData);
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading the CSV file: " + e.getMessage());
            e.printStackTrace();
        }

        return donneeCSV;
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

            //création de l'étudiant
            Etudiant e = new Etudiant(id, nomE, prenomE, parcours );

            //ajout de l'étudiant à la liste d'étudiant
            listeEtudiants.add(e);

        }
        return listeEtudiants;
    }


    //va charger le ficher CSV, appeller la méthode pour lire et stocker les informations du fichier csv
    // puis utiliser ce qui est retourné par cette méthode pour appeler la méthode pour créer la liste d'étudiant
    //Cette méthode va être appelé dans ControllerVisualisationDonneeCSV
    public List<Etudiant> chargementFichierCSV()
    {

        // Chemin vers le fichier CSV, on pourras le mettre en paramètre de la méthode aussi
        String fichierCSV = "src/main/resources/etudiant.csv";

        // va stocker les différentes lignes du fichier csv lors de l'appel de la méthode lectureStockInfoCSV
        List<List<String>> donneeCSV = new ArrayList<>();

        //va stocker les étudiants qui vont être créer à partir du fichier csv et de l'appel de la méthode creationEtudiant
        List<Etudiant> listeEtudiants = new ArrayList<>();

        //BufferedReader va permettre de lire ligne par ligne le fichier CSV
        try (BufferedReader br = new BufferedReader(new FileReader(fichierCSV))) {

            donneeCSV = lectureStockInfoCSV (br);
            listeEtudiants = creationEtudiant(donneeCSV);

        } catch (IOException e) {

            System.err.println("Error reading the CSV file: " + e.getMessage());
            e.printStackTrace();

        }
        return listeEtudiants;
    }
}

