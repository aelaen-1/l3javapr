package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.util.ArrayList;


/**
 * Classe InsertBD : Gère l'appel aux autres classes permettant l'insertion des données dans la BD
 *
 */


public class InsertBD {

    /**
     * Méthode appelée  dans la méthode main de la classe MainApp
     *
     * Cette méthode va faire appel aux différentes méthodes d'insertion des données dans les BD
     *
     */

    public static void insertionDonneeBD(){

        //on charge les UES du fichier csv dans une liste d'UE
        ArrayList<UE> listeUE = new UEDAO().chargerUEs();
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les UE dans la BD
        UEBD.recuperationInformationListUE(listeUE);

        //on met listeUE car c'est la liste contenant les objets UE et leurs prérequis
        UEPrerequisBD.recuperationInformationListUEPre(listeUE);

        //on charge les parcours du fichier csv dans une liste de parcours
        ArrayList<Parcours> listeParcours = new ParcoursDAO().chargerParcours(listeUE);
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les parcours dans la BD
        ParcoursBD.recuperationInformationListParcours(listeParcours);

        //on met listeParcours car c'est lui qui va contenir les UE obligatoires d'un parcours
        UEObligatoiresBD.recuperationInformationListUEObli(listeParcours);

        //on charge les étudiants du fichier csv dans une liste d'étudiants
        ArrayList<Etudiant> listeEtudiants = new EtudiantDAO().chargerTout(listeParcours, listeUE);
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les étudiants dans la BD
        EtudiantBD.recuperationInformationListEtudiant(listeEtudiants);

        ResultatUEBD.recuperationInformationListResultatUE(listeEtudiants);
    }
}
