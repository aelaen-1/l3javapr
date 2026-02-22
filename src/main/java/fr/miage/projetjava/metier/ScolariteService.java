package fr.miage.projetjava.metier;
import fr.miage.projetjava.model.*;
import java.util.ArrayList;
import java.util.List;

public class ScolariteService {
    /* cette méthode permet d'obtenir  les Ues accessibles par un etudiant
     * elle prend en entrée un etduant et la liste des Ues du parcours dans le quel l'etudidant est inscrit
     * si l'utilisateur veut voir les Ues accessibles par un etudiant, le controller doit faire appel  a cete methode */
    public List<UE> obtenirUEAccessibles(Etudiant etudiant, List<UE> toutesLesUE) {
        //on crée un tableaux des Ues pour stocker les Ues accessibmes par l'étudiant
        ArrayList<UE> accessibles = new ArrayList<>();
        //on recupere les Ues du parcours de l'etudiant
        List<UE> uesDuParcours = etudiant.getParcours().getUEObligatoire();
        // On boucle sur les UE du parcours
        for (UE ueDuParcours : uesDuParcours) {
            // On part du principe que c'est bon, et on cherche si un prérequis manque
            boolean possedePrerequis = true;
            if (ueDuParcours.getUEprerequis() != null && !ueDuParcours.getUEprerequis().isEmpty()) {
                for (UE prerequis : ueDuParcours.getUEprerequis()) {
                    boolean prerequisValide = false;
                    // On cherche dans les résultats de l'étudiant si le prérequis est validé
                    for (ResultatUE res : etudiant.getResultatsUE()) {
                        if (res.getUe().getCode().equals(prerequis.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                            prerequisValide = true;
                            break;
                        }
                    }
                    // Si un seul prérequis n'est pas validé, l'étudiant ne peut pas prendre l'UE
                    if (!prerequisValide) {
                        possedePrerequis = false;
                        break;
                    }
                }
            }
            // Si l'étudiant a les prérequis, on regarde s'il peut s'inscrire
            if (possedePrerequis) {
                // On cherche si l'étudiant a déjà été inscrit à cette UE
                ResultatUE historique = null;
                for (ResultatUE res : etudiant.getResultatsUE()) {
                    if (res.getUe().getCode().equals(ueDuParcours.getCode())) {
                        historique = res;
                        break;
                    }
                }
                // si historique est vide on ajoute les Ues qui ont null
                if (historique == null) {
                    if (ueDuParcours.getUEprerequis() == null || ueDuParcours.getUEprerequis().isEmpty()) {
                        // on ajoute l'UE car elle n'a pas de prérequis et jamais été inscrite
                        accessibles.add(ueDuParcours);
                    }
                }
                // Sinon il a été inscrit au moins une fois
                else {
                    StatutUE statut = historique.getStatut();
                    // Si c'est ECHOUE, il peut la reprendre mais au semestre suivant
                    if (statut == StatutUE.ECHOUE) {
                        if (!historique.getSemestre().equals(etudiant.getSemestreCourant())) {
                            accessibles.add(ueDuParcours);
                        }
                    }
                }
            }
        }
        //on retourne le tableau des Ues accessibles
        return accessibles;
    }
    public boolean inscrireEtudiant(Etudiant e, UE ue, String annee, Semestre semestre) {
        if (!possedePrerequis(ue, e)) {
            return false;
        }
        ResultatUE inscription = new ResultatUE(ue, annee, semestre, StatutUE.ENCOURS);
        e.getResultatsUE().add(inscription);
        return true;
    }

    public Boolean possedePrerequis(UE uecible, Etudiant e) {
        if (uecible.getUEprerequis() == null || uecible.getUEprerequis().isEmpty()) {
            return true;
        }
        for (UE pre : uecible.getUEprerequis()) {
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().getCode().equals(pre.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                    valide = true;
                    break;
                }
            }
            if (!valide) return false;
        }
        return true;
    }
/* cette méthode permet savoir si un etudiant peut avoir son diplome*/
    public Boolean estDiplome(Etudiant e) {
        //on initilase à 0 un compteur qui calcul les credits de l'etudiant
        int totalcredits = 0;
        //on parcout dans les resutats de l'etudiant
        for (ResultatUE res : e.getResultatsUE()) {
            //on ajoute les credit des Ues validés par l'etuadiant
            if (res.getStatut() == StatutUE.VALIDE) {
                totalcredits += res.getUe().getCredit();
            }
        }
        //on verifie si total credit est inferieur à 180
        if (totalcredits < 180)
            //si oui on retourne false
            return false;
        //On verifie s'il a validé toutes les Ues obligatoires du parcours
        for (UE ueObligatoire : e.getParcours().getUEObligatoire()) {
            //varibale boolean pour verfie s'il a validé toutes les Ues de l'etudiant
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().getCode().equals(ueObligatoire.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                    // si l'ue a un statut VALIDE , la variable valide deveint true
                    valide = true;
                    break;
                }
            }
            //si au moins une Ue du parcours n est pas validé  la variable est false
            if (!valide)
                //on retourne false
                return false;
        }
        // si l'etudiant à un credit >180 et a validé toutes les Ues obligatoires du parcours
        // alors on retourne true ,il est diplomé
        return true;
    }
/* Cette methode  permet de changer le statut d'une UE de l'etudant ( qui suit au semestre courant
) à VALIDE*/
    public boolean validerUE(Etudiant e, UE ue) {
        //on boucle sur Resutlat de l'etudant
        for (ResultatUE res : e.getResultatsUE()) {
            //on recupere l'UE correspondant
            if (res.getUe().getCode().equals(ue.getCode())) {
                //on change le statut
                res.setStatut(StatutUE.VALIDE);
                return true;
            }
        }
        //s'il existe pas alors on retourne false car on peut pas valider qu on la suit pas
        return false;
    }
/* cette methode permet de marquer Echouer une Ue suivit par l'etudiant*/
    public boolean echoueUE(Etudiant e, UE ue) {
        //on recupere les resulats de l'etudiant
        for (ResultatUE res : e.getResultatsUE()) {
            //on cherche l'Ue correspondant
            if (res.getUe().getCode().equals(ue.getCode())) {
                //on marque statut ECHOUE
                res.setStatut(StatutUE.ECHOUE);
                return true;
            }
        }
        //si l'Ue n est pas suivi par l'etudiant alors on retourne false
        return false;
    }
    /* cette methode permet de passer un etudiant d'un semesre cournat à un semestre
    * suivant*/
    public void passerSemestre(Etudiant e) {
        //on recupere le semestre Cournat de l'etudiant suivant et on teste s'il est Impair
        if (e.getSemestreCourant() == Semestre.IMPAIR)
            //on change à PAIR
            e.setSemestreCourant(Semestre.PAIR);
        else
            //sion on change à IMPAIR le semestre courant
            e.setSemestreCourant(Semestre.IMPAIR);
    }
}