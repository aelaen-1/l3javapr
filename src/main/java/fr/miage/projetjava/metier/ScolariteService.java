package fr.miage.projetjava.metier;
import fr.miage.projetjava.model.*;

import java.util.ArrayList;

public class ScolariteService {
    public ArrayList<UE> obtenirUEAccessibles(Etudiant e, ArrayList<UE> toutesUE) {
        ArrayList<UE> accessibles = new ArrayList<>();
        System.out.println(" UE accessibles pour " + e.getNomE() + " ---");

        for (UE ue : toutesUE) {
            // Règle 1 : Même mention
            boolean memeMention = ue.getMention().equals(e.getParcours().getMention());
            // Règle 2 : Pas déjà inscrit
            boolean dejaInscrit = e.getResultatsUE().stream()
                    .anyMatch(res -> res.getUe().getCode().equals(ue.getCode()));
            // Règle 3 : Prérequis OK
            boolean prerequisOK = possedePrerequis(ue, e);

            if (memeMention && !dejaInscrit && prerequisOK) {
                accessibles.add(ue);
                System.out.println("  [ACCESSIBLE] " + ue.getCode());
            }
        }
        return accessibles;
    }

    public boolean inscrireEtudiant(Etudiant e, UE ue, String annee, Semestre semestre) {
        System.out.print("Inscription de " + e.getNomE() + " à " + ue.getCode() + "... ");
        if (!possedePrerequis(ue, e)) {
            System.out.println("ÉCHEC (Prérequis manquants)");
            return false;
        }
        ResultatUE inscription = new ResultatUE(ue, annee, semestre, StatutUE.ENCOURS);
        e.getResultatsUE().add(inscription);
        System.out.println("RÉUSSIE");
        return true;
    }
    public Boolean possedePrerequis(UE uecible, Etudiant e) {
        if (uecible.getUEprerequis() == null || uecible.getUEprerequis().isEmpty()) {
            return true;
        }
        for (UE pre : uecible.getUEprerequis()) {
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().equals(pre) && res.getStatut() == StatutUE.VALIDE) {
                    valide = true;
                    break;
                }
            }
            if (!valide) {
                return false; // un prérequis non validé → l'étudiant ne peut pas s'inscrire
            }
        }
        return true; // tous les prérequis validés
    }


    public Boolean estDiplome(Etudiant e) {
        int totalcredits = 0;
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getStatut() == StatutUE.VALIDE) {
                totalcredits += res.getUe().getCredit();
            }
        }
        if (totalcredits < 180) {
            return false;
        }
        for (UE ueObligatoire : e.getParcours().getUEObligatoire()) {
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().equals(ueObligatoire) && res.getStatut() == StatutUE.VALIDE) {
                    valide = true;
                }
            }
            if (!valide) {
                return false;
            }
        }
        return true;
    }
    public boolean validerUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getUe().equals(ue)) {
                res.setStatut(StatutUE.VALIDE);
                return true;
            }
        }
        return false;
    }

    public boolean echoueUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getResultatsUE()) {
            if (res.getUe().equals(ue)) {
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