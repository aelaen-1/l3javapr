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
     * Cette méthode va chercher toutes les informations des UE dans la bd créer une liste d'UE.
     * Elle va faire appel à la méthode recupInfoUEPrerequisObliBD pour récupérer les UE obligatoire de chaque UE
     * @return la liste d'UE récupéré de la BD
     */

    public static ArrayList<UE> recupInfoUEBD()
    {
        //liste d'UE que l'on va renvoyer
        ArrayList<UE> listeUE = new ArrayList<>();


        try(Connection connexion = ConnexionBD.connexionBD()){

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
                    ArrayList<UE> UEprerequis = recupInfoUEPrerequisObliBD(code, connexion, listeUE, "prerequis");

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
     * Méthode appelé dans recupInfoUEBD
     *
     * Cette méthode va chercher toutes les UE prérequis ou obligatoires d'une UE ou d'un parcours passé en paramètre
     * et va renvoyer une ArrayList de ces UE prérequis ou obligatoires.
     * @param : String nom, Connection connexion, ArrayList<UE> listeUE, String type:
     * nom : indique soit le code de l'UE sur laquelle on veut récupérer les prérequis, soit le nom du parcours sur lequel
     * on veut récupérer les UE obligatoires
     * listeUE : indique la liste d'UE déjà existante dans laquelle on va aller chercher les UE obligatoires ou prérequis
     * type : indique si c'est pour un parcours ou une UE
     *
     * @return la liste d'UE prérequis ou obligatoire récupérée de la BD
     */

    public static ArrayList<UE> recupInfoUEPrerequisObliBD(String nom, Connection connexion, ArrayList<UE> listeUE, String type){
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




}
