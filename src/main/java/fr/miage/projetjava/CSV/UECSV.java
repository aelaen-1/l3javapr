package fr.miage.projetjava.CSV;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class UECSV {

    //dans les données du fichier csv UE que l'on va recevoir la mention va être sous forme de String il va donc
    // falloir la passer en type Mention pour pouvoir créer l'UE
    private Mention passageStringToMention(String mention)
    {

        if (mention == Mention.MIASHS.toString()){
            return Mention.MIASHS;
        }
        else if (mention == Mention.BIOLOGIE.toString()){
            return Mention.BIOLOGIE;
        }
        else if (mention == Mention.CHIMIE.toString()){
            return Mention.CHIMIE;
        }
        else if (mention == Mention.INFORMATIQUE.toString()){
            return Mention.INFORMATIQUE;
        }
        else{
            return Mention.PHYSIQUE;
        }
    }


    //utiliser split(,) pour creer l'arraylist de string puis les passer en arraylist d'UE
    private List<UE>  passageStringToArrayList(String stringUEprerequis)
    {
        List<UE> UEprerequis = new ArrayList<>();

        return UEprerequis;
    }

    //String code, String intitule, int credit,   mention, ArrayList<UE> UEprerequis = new ArrayList<>();
    //avec la liste de liste de données du fichier CSV, la méthode va créer un à un toutes les UE et les stocker dans une liste d'UE
    private List<UE> creationUE(List<List<String>> data)
    {
        List<UE> listeEtudiants = new ArrayList<>();

        for (int i = 0; i < data.size()-1; i++) {
            //récupération des différentes informations qui viennent du fichier CSV et qui sont nécessaire à la création des objets UE
            String code = data.get(i+1).get(0);
            String intitule = data.get(i+1).get(1);
            int credit = parseInt(data.get(i+1).get(2));
            Mention mention = passageStringToMention(data.get(i+1).get(3));
            List<UE> UEprerequis = data.get(i+1).get(4);

            //création de l'UE
            UE ue = new UE(code, intitule, credit, mention, UEprerequis);

            //ajout de l'UE à la liste d'étudiant
            listeEtudiants.add(ue);

        }
        return listeEtudiants;
    }


}
