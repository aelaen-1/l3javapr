package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.util.ArrayList;

public class InsertBD {


    public static void insertionDonneeBD(){

        //on charge les UES du fichier csv dans une liste d'UE
        ArrayList<UE> ues = new UEDAO().chargerUEs();
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les UE dans la BD
        UEBD.recuperationInformationListUE(ues);

        //on charge les parcours du fichier csv dans une liste de parcours
        ArrayList<Parcours> listeParcours = new ParcoursDAO().chargerParcours(ues);
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les parcours dans la BD
        ParcoursBD.recuperationInformationListParcours(listeParcours);

        //on charge les étudiants du fichier csv dans une liste d'étudiants
        ArrayList<Etudiant> etudiants = new EtudiantDAO().chargerTout(listeParcours, ues);
        //Une fois toute les données du fichier csv chargés dans l'ArrayList on appel la méthode pour insérer les étudiants dans la BD
        EtudiantBD.recuperationInformationListEtudiant(etudiants);

    }
}
