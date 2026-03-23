package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;



/**
 * Classe RecupererBD : Gère le fait de récupérer les différentes informations de la BD et de les stocker dans des listes.
 * Ces listes vont ensuite être utilisées pour l'affichege des informations sur l'interface.
 *
 */

public class RecupererBD {


    private static final Logger log = LogManager.getLogger(RecupererBD.class);

    /***
     * Méthode appelé dans
     *
     * Cette méthode va appeler les méthodes recupInfoUEBD, recupInfoParcoursBD et recupInfoEtudiantBD
     *
     * @param : Connection connexion, String typeRenvoie
     *  connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     *  typeRenvoie : indique quel type d'ArrayList on veut récupérer, celle des étudiants des parcours ou des UE
     * @return la liste de parcours, d'UE ou d'étudiant
     */


    public static ArrayList<?> renvoieListe(Connection connexion, String typeRenvoie)
    {
        ArrayList<UE> listeUE = recupInfoUEBD(connexion);
        ArrayList<Parcours> listeParcours = recupInfoParcoursBD(connexion, listeUE);
        ArrayList<Etudiant> listeEtudiant = recupInfoEtudiantBD(connexion, listeParcours, listeUE);

        if (typeRenvoie.equals("UE")){
            return listeUE;
        }
        else if (typeRenvoie.equals("Parcours")) {
            return listeParcours;
        }
        else{
            return listeEtudiant;
        }

    }



    /***
     * Méthode appelé dans renvoieListe
     *
     * Cette méthode va chercher toutes les informations des UE dans la bd créer une liste d'UE.
     * Elle va faire appel à la méthode recupInfoUEPrerequisObliBD pour récupérer les UE obligatoire de chaque UE
     *
     * @param : Connection connexion
     *  connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * @return la liste d'UE récupéré de la BD
     */

    public static ArrayList<UE> recupInfoUEBD(Connection connexion)
    {
        //liste d'UE que l'on va renvoyer
        ArrayList<UE> listeUE = new ArrayList<>();


        try{

            //requête pour récupérer les UE de la bd
            String requeteUE = "Select * from UE";

            //Statement est un objet qui représente une requête SQL à envoyer à la bd
            Statement recupUE = connexion.createStatement();

            //execute de Statement renvoie un boolean, pour exécuter la requête SQL.
            //renvoie true si la requête a été exécutée avec succès et qu'elle peut renvoyer des
            // lignes donc si dans la requête il y a un select. Si c'est un update ou delete ça renvoie pas de
            // ligne donc renvoie un false.
            //renvoie false si la requête renvoie un nombre de ligne, ca va être le cas avec update ou delete mais pas avec select
            boolean resultatRequeteUE = recupUE.execute(requeteUE);


            //si on a bien le select qui a été exécuté et que l'on a bien des lignes
            if(resultatRequeteUE){

                //on va récupérer les résultats de la requête
                //ResultSet est un curseur qui va parcourir les lignes une par une du résultat de la requête
                //si la requête n'a renvoyé aucune ligne alors rsListeUE va être vide
                ResultSet rsListeUE = recupUE.getResultSet();

                //c'est next qui va permettre de passer à la ligne suivante, il renvoie true tant qu'il reste des lignes à parcourir
                while(rsListeUE.next()){

                    //getInt ou getString renvoie l'information de la colonne mis en ()
                    String code = rsListeUE.getString("codeUE");
                    String intitule = rsListeUE.getString("intitule");
                    int credit = rsListeUE.getInt("credit");
                    String mention = rsListeUE.getString("mention");

                    //on créé l'UE et on a transformé mention en objet mention
                    UE ue = new UE(code, intitule, credit, Mention.valueOf(mention.toUpperCase()));

                    //on va aller récuperer les prérequis de l'UE si elle en a, on donne listeUE car les UE prérequis
                    // sont des UE qui existent déjà et qui sont donc dans la liste d'UE
                    ArrayList<UE> UEprerequis = recupInfoUEPrerequisObliBD(connexion, code, listeUE, "prerequis");

                    //on ajoute les prérequis de l'UE
                    for (UE uePrerequis : UEprerequis){
                        ue.setUEprerequis(uePrerequis);
                    }

                    //on l'ajout l'UE à la liste
                    listeUE.add(ue);

                }
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }


        return listeUE;
    }

    /***
     * Méthode appelé dans recupInfoUEBD et recupInfoParcoursBD
     *
     * Cette méthode va chercher toutes les UE prérequis ou obligatoires d'une UE ou d'un parcours passé en paramètre
     * et va renvoyer une ArrayList de ces UE prérequis ou obligatoires.
     * @param : Connection connexion, String nom, Connection connexion, ArrayList<UE> listeUE, String type:
     *  connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * nom : indique soit le code de l'UE sur laquelle on veut récupérer les prérequis, soit le nom du parcours sur lequel
     * on veut récupérer les UE obligatoires
     * listeUE : indique la liste d'UE déjà existante dans laquelle on va aller chercher les UE obligatoires ou prérequis
     * type : indique si c'est pour un parcours ou une UE
     *
     * @return la liste d'UE prérequis ou obligatoire récupérée de la BD
     */

    public static ArrayList<UE> recupInfoUEPrerequisObliBD(Connection connexion, String nom, ArrayList<UE> listeUE, String type){
        ArrayList<UE> listeUEPrerequisObli = new ArrayList<>();
        String requeteUE;

        if(type.equals("prerequis")) {
            //requête pour récupérer toutes les UE prérequis de l'UE que l'on veut ajouter dans l'ArrayList
            requeteUE = "Select * From UEprerequis where codeUE = ?";
        }
        else {
            //requête pour récupérer toutes les UE obligatoire du parcours que l'on veut ajouter dans l'ArrayList
            requeteUE = "Select * From UEObligatoire where nomParcours = ?" ;
        }


        try{
            //va nous permettre d'exécuter la requête
            PreparedStatement recupUEPrerequisObli = connexion.prepareStatement(requeteUE);

            recupUEPrerequisObli.setString(1, nom);

            //pour récupérer les informations, true si ça renvoie des lignes et false si ca renvoie un nombre de ligne
            boolean resultatRequeteUEPrerequisObli = recupUEPrerequisObli.execute();

            //true, si on a bien ce qu'on veut, donc les informations concernant les UEPrerequis
            if(resultatRequeteUEPrerequisObli){

                //récupération des UE, ResultSet est un curseur qui va passer un à un sur les différentes lignes de ce qui a été renvoyé
                ResultSet rsListeUEPrerequisObli = recupUEPrerequisObli.getResultSet();

                //on passe ligne par ligne sur les différentes UE prerequis pour les ajouter à la liste
                while(rsListeUEPrerequisObli.next()){

                    //on va récupérer le code de l'UE obliatoire ou prérequis
                    String codeUEprerequisObli;
                    if(type.equals("prerequis")){
                        //getString renvoie l'information de la colonne mis en ()
                        codeUEprerequisObli = rsListeUEPrerequisObli.getString("codeUEPrerequis");

                    }
                    else {
                        codeUEprerequisObli = rsListeUEPrerequisObli.getString("UE");

                    }

                    //les UE prérequis ou obligatoires sont des UE qui existent déjà on donc aller chercher dans la liste d'UE déjà
                    // existante et on va l'ajouter à la liste d'UE prérequis ou obligatoire
                    for (UE ueliste : listeUE){
                        //on compare les UE existantes avec le code UE de l'UE prérequis ou obligatoire que l'on a récupéré
                        if(ueliste.getCode().equals(codeUEprerequisObli)){
                            listeUEPrerequisObli.add(ueliste);
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return listeUEPrerequisObli;
    }


    /***
     * Méthode appelé dans renvoieListe
     *
     * Cette méthode va chercher toutes les informations des Parcours dans la bd et va renvoyer une liste de parcours
     *
     * @param : Connection connexion,  ArrayList<UE> listeUE
     * listeUE: liste contenant toutes UE et va permettre d'ajouter les UE exsitantes dans la liste d'UE obligatoires
     * associés à un parcours
     * connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * @return la liste de parcours récupéré de la BD
     */

    public static ArrayList<Parcours> recupInfoParcoursBD(Connection connexion, ArrayList<UE> listeUE)
    {
        //liste de parcours que l'on va renvoyer
        ArrayList<Parcours> listeParcours = new ArrayList<>();


        try{

            //requête pour récupérer les étudiants de la bd
            String requete = "Select * from Parcours";

            //Statement est un objet qui représente une requête SQL à envoyer à la bd
            Statement recupParcours = connexion.createStatement();

            //execute de Statement renvoie un boolean,
            //renvoie true si la requête a été exécutée avec succès et qu'elle peut renvoyer des
            // lignes donc si dans la requête il y a un select. Si c'est un update ou delete ça renvoie pas de
            // ligne donc renvoie un false.
            //renvoie false si la requête renvoie un nombre de ligne, ca va être le cas avec update ou delete mais pas avec select
            boolean resultatRequete = recupParcours.execute(requete);

            //si on a bien le select qui a été exécuté
            if(resultatRequete){

                //on va récupérer les résultats de la requête
                //ResultSet est un curseur qui va parcourir les lignes une par une du résultat de la requête
                //si la requête n'a renvoyé aucune ligne alors rsListeEtudiant va être vide
                ResultSet rsListeParcours = recupParcours.getResultSet();

                //c'est next qui va permettre de passer à la ligne suivante, il renvoie true tant qu'il reste des lignes à parcourir
                while(rsListeParcours.next()){

                    //getInt ou getString renvoie l'information de la colonne mis en ()
                    String nom = rsListeParcours.getString("code");
                    String mention = rsListeParcours.getString("mention");


                    //on créé le parcours et on a transformé mention en objet mention
                    Parcours parcours = new Parcours(nom,  Mention.valueOf(mention.toUpperCase()) );

                    ArrayList<UE> UEObligatoire = recupInfoUEPrerequisObliBD(connexion, nom, listeUE, "obligatoire");
                    for(UE ue : UEObligatoire ){
                        parcours.addUEObligatoire(ue);
                    }

                    //on l'ajout à la liste
                    listeParcours.add(parcours);

                }
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return listeParcours;
    }

    /***
     * Méthode appelé dans recupInfoEtudiantBD
     *
     * Cette méthode va chercher toutes les informations des résultats des UE des étudiants dans la bd.
     *
     * @param : Connection connexion, int numE, ArrayList<UE> listeUE
     *  connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * numE : numéro étudiant pour lequel on veut récupérer la liste de Resutat d'UE
     * listeUE: liste d'UE existante et qui va permettre d'ajouter les UE exsitantes dans les objets ResultatUE
     *
     * @return la liste d'étudiant récupéré de la BD
     */

    public static ArrayList<ResultatUE> recupInfoResultatUEBD(Connection connexion, int numE, ArrayList<UE> listeUE)
    {
        //liste de résultat d'UE de l'étudiant passer en paramètre
        ArrayList<ResultatUE> listeResultatUE = new ArrayList<>();

        try{

            //requête pour récupérer les étudiants de la bd
            String requete = "Select * from ResultatUE where numE = ?";


            //Statement est un objet qui représente une requête SQL à envoyer à la bd
            PreparedStatement recupResultatUE = connexion.prepareStatement(requete);

            recupResultatUE.setInt(1, numE);
            //execute de Statement renvoie un boolean,
            //renvoie true si la requête a été exécutée avec succès et qu'elle peut renvoyer des
            // lignes donc si dans la requête il y a un select. Si c'est un update ou delete ça renvoie pas de
            // ligne donc renvoie un false.
            //renvoie false si la requête renvoie un nombre de ligne, ca va être le cas avec update ou delete mais pas avec select
            boolean resultatResultatUE = recupResultatUE.execute();

            //si on a bien le select qui a été exécuté
            if(resultatResultatUE){

                //on va récupérer les résultats de la requête
                //ResultSet est un curseur qui va parcourir les lignes une par une du résultat de la requête
                //si la requête n'a renvoyé aucune ligne alors rsListeEtudiant va être vide
                ResultSet rsListeResultatUE = recupResultatUE.getResultSet();

                //c'est next qui va permettre de passer à la ligne suivante, il renvoie true tant qu'il reste des lignes à parcourir
                while(rsListeResultatUE.next()){

                    //getInt ou getString renvoie l'information de la colonne mis en ()
                    //on récupère toutes les informations des résultats des UE de la BD
                    String codeUE = rsListeResultatUE.getString("codeUE");
                    int annee = rsListeResultatUE.getInt("annee");
                    String semestre = rsListeResultatUE.getString("semestre");
                    String statut = rsListeResultatUE.getString("statut");

                    for(UE chercheUE : listeUE){
                        if (chercheUE.getCode().equals(codeUE)){
                            //pour que ça puisse coller avec l'enum statut
                            if (statut.equals("En cours")){
                                statut = "encours";
                            }
                            else if(statut.equals("Echoué")){
                                statut = "echoue";
                            }

                            //on créé le ResultatUE et on a transformé semestre en objet semestre et statut en un objet Statut

                            ResultatUE resultatUE = new ResultatUE(chercheUE, Integer.toString(annee),Semestre.valueOf(semestre.toUpperCase()), StatutUE.valueOf(statut.toUpperCase()));

                            //on l'ajout à la liste des résultats UE de l'étudiant
                            listeResultatUE.add(resultatUE);

                            break;
                        }

                    }
                }
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }


        return listeResultatUE;
    }

    /***
     * Méthode appelé dans renvoieListe
     *
     * Cette méthode va chercher toutes les informations des étudiants dans la bd.
     * Elle appel la méthode recupInfoResultatUEBD pour récupérer les résultats des UE des étudians.
     *
     * @param : Connection connexion,  ArrayList<Parcours> listeParcours, ArrayList<UE> listeUE
     *  connexion : connexion à la bd qui va permettre de pouvoir exécuter les différentes requêtes sql
     * listeParcours : utilisé pour rajouter le parcours que suit l'étudiant
     * listeUE : va être nécessaire pour l'appel à la méthode recupInfoResultatUEBD
     * @return la liste d'étudiant récupéré de la BD
     */

    public static ArrayList<Etudiant> recupInfoEtudiantBD(Connection connexion, ArrayList<Parcours> listeParcours, ArrayList<UE> listeUE)
    {
        //liste d'étudiant que l'on va renvoyer
        ArrayList<Etudiant> listeEtudiant = new ArrayList<>();


        try{

            //requête pour récupérer les étudiants de la bd
            String requete = "Select * from Etudiant";

            //Statement est un objet qui représente une requête SQL à envoyer à la bd
            Statement recupEtudiant = connexion.createStatement();

            //execute de Statement renvoie un boolean,
            //renvoie true si la requête a été exécutée avec succès et qu'elle peut renvoyer des
            // lignes donc si dans la requête il y a un select. Si c'est un update ou delete ça renvoie pas de
            // ligne donc renvoie un false.
            //renvoie false si la requête renvoie un nombre de ligne, ca va être le cas avec update ou delete mais pas avec select
            boolean resultatRequete = recupEtudiant.execute(requete);

            //si on a bien le select qui a été exécuté
            if(resultatRequete){

                //on va récupérer les résultats de la requête
                //ResultSet est un curseur qui va parcourir les lignes une par une du résultat de la requête
                //si la requête n'a renvoyé aucune ligne alors rsListeEtudiant va être vide
                ResultSet rsListeEtudiant = recupEtudiant.getResultSet();

                //c'est next qui va permettre de passer à la ligne suivante, il renvoie true tant qu'il reste des lignes à parcourir
                while(rsListeEtudiant.next()){

                    //getInt ou getString renvoie l'information de la colonne mis en ()
                    int numE = rsListeEtudiant.getInt("numE");
                    String prenomE = rsListeEtudiant.getString("prenomE");
                    String nomE = rsListeEtudiant.getString("nomE");
                    String parcoursString = rsListeEtudiant.getString("parcours");
                    String semestre = rsListeEtudiant.getString("semestre");

                    //on va chercher le parcours que l'étudiant suit dans la liste de parcours existant
                    for(Parcours chercheParcours : listeParcours){
                        if (chercheParcours.getNom().equals(parcoursString)){
                            //on créé l'étudiant et on a transformé semestre en objet semestre
                            Etudiant etudiant = new Etudiant(numE, nomE, prenomE, chercheParcours, Semestre.valueOf(semestre.toUpperCase()) );

                            //on va récupérer les Resultats d'UE de l'étudiant
                            ArrayList<ResultatUE> listeResultatUE = recupInfoResultatUEBD(connexion, numE, listeUE);

                            //on ajoute les resultat d'UE à l'étudiant
                            for (ResultatUE resultatUE : listeResultatUE){
                                log.info(etudiant + "L'etudiant à des resultats pour au moins une UE");
                                etudiant.addResultatUE(resultatUE);
                            }

                            //on l'ajout à la liste
                            listeEtudiant.add(etudiant);

                            break;
                        }

                    }
                }
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return listeEtudiant;
    }


}
