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

            // 1. ON VERIFIE LES PREREQUIS
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
                // si historique est vide on ajoute les Ues qui ont getPrerequis == null
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

    public Boolean estDiplome(Etudiant e) {
        int totalcredits = 0;
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getStatut() == StatutUE.VALIDE) {
                totalcredits += res.getUe().getCredit();
            }
        }
        if (totalcredits < 180) return false;

        for (UE ueObligatoire : e.getParcours().getUEObligatoire()) {
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().getCode().equals(ueObligatoire.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                    valide = true;
                    break;
                }
            }
            if (!valide) return false;
        }
        return true;
    }

    public boolean validerUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getUe().getCode().equals(ue.getCode())) {
                res.setStatut(StatutUE.VALIDE);
                return true;
            }
        }
        return false;
    }

    public boolean echoueUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getUe().getCode().equals(ue.getCode())) {
                res.setStatut(StatutUE.ECHOUE);
                return true;
            }
        }
        return false;
    }
    public void passerSemestre(Etudiant e) {
        if (e.getSemestreCourant() == Semestre.IMPAIR)
            e.setSemestreCourant(Semestre.PAIR);
        else
            e.setSemestreCourant(Semestre.IMPAIR);
    }
}