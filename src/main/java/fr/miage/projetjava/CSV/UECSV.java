package fr.miage.projetjava.CSV;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    //va transformer la ligne String de pré-requis en liste de String et va ensuite aller chercher cette UE dans la liste
    // d'UE déjà existante pour aller l'ajouter dans l'ArrayList pré-requis de l'UE
    private void  ajoutPreRequisUE(String stringUEprerequis, List<UE> listeUE, UE ue)
    {
        //va faire un tableau de String et chaque élément du tableau va correspondre à un element de la liste qui est
        // séparé par une virgule
        //Exemple: stringUEprerequis = "UE1, UE2, UE3"
        //valeur va donner = ["UE1", "UE2", "UE3"]
        String[] tabStringUEPrerequis = stringUEprerequis.split(",");

        //passage en ArrayList
        List<String> listeStringUECSV = Arrays.asList(tabStringUEPrerequis);


        //faire une boucle for qui va vérifier que chaque UE de listeStringUE existe dans listeUE et si c'est le cas on prend
        // l'UE de listeUE et on l'ajoute à l'ArrayList qui possède ces pré-requis et qui est dans les paramètres

        //on fait le tour de toutes les UE de la liste de pre-requis du CSV
        for (int i = 0; i < listeStringUECSV.size(); i++){

            //on fait le tour de toutes les UE déjà existantes
            for(int j = 0; j<listeUE.size(); j++){

                //si on trouve l'UE pré-requis dans la liste d'UE déjà existante on fait appel à la méthode
                // de la classe UE permettant d'ajouter les UE pré-requis dans l'ArrayList
                if (listeUE.get(j).getIntitule() == listeStringUECSV.get(i)){
                    ue.setUEprerequis(listeUE.get(j));
                }
            }
        }

    }

    //avec la liste de liste de données du fichier CSV, la méthode va créer un à un toutes les UE et les stocker dans une liste d'UE
    private List<UE> creationUE(List<List<String>> data)
    {
        //liste d'UE
        List<UE> listeUE = new ArrayList<>();

        for (int i = 0; i < data.size()-1; i++) {
            //récupération des différentes informations qui viennent du fichier CSV et qui sont nécessaire à la création des objets UE
            String code = data.get(i+1).get(0);
            String intitule = data.get(i+1).get(1);
            int credit = parseInt(data.get(i+1).get(2));

            //on récupère mention qui va être sous forme de String et la méthode va la passer en Mention
            Mention mention = passageStringToMention(data.get(i+1).get(3));

            //on récupère la ligne des pré-requis et on va après appelé la méthode permettant de créer la liste des UE pré-requis
            String stringUEprerequis = data.get(i+1).get(4);

            //création de l'UE
            UE ue = new UE(code, intitule, credit, mention);

            //méthode permettant de passer de la ligne contenant les pre-requis à l'ajout des UE déjà
            // existante dans l'arrayList de UE
            ajoutPreRequisUE(stringUEprerequis, listeUE, ue);


            //ajout de l'UE à la liste d'UE
            listeUE.add(ue);

        }
        return listeUE;
    }


}
