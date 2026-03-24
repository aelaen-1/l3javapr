package fr.miage.projetjava.dao;


import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Parcours;
import fr.miage.projetjava.model.UE;

import java.sql.Connection;
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
     * Elle va d'abord charger une liste pour un fichier csv afin de récupérer les différentes informations de ces fichiers.
     * Elle va ensuite utiliser ces listes comme paramètre pour l'insertion des données dans la BD.
     *
     * @param : Connection connexion
     * connexion : connexion à la bd qui va être donné en appel des méthodes d'insertions des données dans la BD et qui
     * va permettre d'exécuter les requêtes sql.
     *
     */
    public static void insertionDonneeBD(Connection connexion){

        //on charge les informations des UES du fichier csv dans une liste d'objets UE
        ArrayList<UE> listeUE = new UEDAO().chargerUEs();
        //Une fois toute les données du fichier csv chargées dans l'ArrayList on appel la méthode pour insérer les UE dans la BD
        UEBD.recuperationInformationListUE(connexion, listeUE);

        //Pour les prérequis des UE on utilise la liste d'UE car c'est elle qui contient les objets UE et leurs prérequis
        UEPrerequisBD.recuperationInformationListUEPre(connexion, listeUE);

        //on charge les parcours du fichier csv dans une liste d'objets parcours
        ArrayList<Parcours> listeParcours = new ParcoursDAO().chargerParcours(listeUE);
        //Une fois toute les données du fichier csv chargées dans l'ArrayList on appel la méthode pour insérer les parcours dans la BD
        ParcoursBD.recuperationInformationListParcours(connexion, listeParcours);

        //Pour les UE obligatoireson on utilise listeParcours car c'est cette liste qui va contenir les UE obligatoires d'un parcours
        UEObligatoiresBD.recuperationInformationListUEObli(connexion, listeParcours);

        //on charge les étudiants du fichier csv dans une liste d'objets étudiants
        ArrayList<Etudiant> listeEtudiants = new EtudiantDAO().chargerTout(listeParcours, listeUE);
        //Une fois toute les données du fichier csv chargées dans l'ArrayList on appel la méthode pour insérer les étudiants dans la BD
        EtudiantBD.recuperationInformationListEtudiant(connexion, listeEtudiants);

        //Pour résultat UE on utilise la liste des étudiants car c'est dans celle-ci qu'est contenu les différents résultats des étudiants
        ResultatUEBD.recuperationInformationListResultatUE(connexion, listeEtudiants);
    }
}

