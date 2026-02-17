package fr.miage.projetjava.CSV;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class UECSV {


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
            Mention mention = data.get(i+1).get(3);
            List<UE> UEprerequis = data.get(i+1).get(4);

            //création de l'UE
            UE ue = new UE(code, intitule, credit, mention, UEprerequis);

            //ajout de l'UE à la liste d'étudiant
            listeEtudiants.add(ue);

        }
        return listeEtudiants;
    }


}
