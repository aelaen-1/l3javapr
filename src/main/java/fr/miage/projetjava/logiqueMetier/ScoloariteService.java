package fr.miage.projetjava.logiqueMetier;
import fr.miage.projetjava.model.*;

import java.util.ArrayList;

public class ScoloariteService {
    public Boolean possedePrerequis(UE uecible, Etudiant e) {
        if (uecible.getListePrerequis().isEmpty() || uecible.getListePrerequis() == null) {
            return true;
        }
        for (UE pre : uecible.getListePrerequis()) {
            boolean valide = false;
            for (ResultatUE res : e.getCursus()) {
                if (res.getUe().equals(pre) && res.getStatut() == StatutUE.VALIDE) {
                    return true;
                }
            }
            if (!valide) {
                return false;
            }
        }
        return true;
    }

    public Boolean estDiplome(Etudiant e) {
        int totalcredits = 0;
        for (ResultatUE res : e.getCursus()) {
            if (res.getStatut() == StatutUE.VALIDE) {
                totalcredits += res.getUe().getCredit();
            }
        }
        if (totalcredits < 180) {
            return false;
        }
        for (UE ueObligatoire : e.getParcours().getListeUeObligatoires()) {
            boolean valide = false;
            for (ResultatUE res : e.getCursus()) {
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
    public boolean inscrireEtudiant(Etudiant e, UE ue,
                                    String annee, Semestre semestre) {
        if (!possedePrerequis(ue,e)) {
            return false;
        }
        ResultatUE inscription =
                new ResultatUE(ue, annee, semestre, StatutUE.ENCOURS);
        e.getCursus().add(inscription);
        return true;
    }
    public void validerUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getCursus()) {
            if (res.getUe().equals(ue)) {
                res.setStatutUE(StatutUE.VALIDE);
                return;
            }
        }
    }
    public void echoueUE(Etudiant e, UE ue) {
        for (ResultatUE res : e.getCursus()) {
            if (res.getUe().equals(ue)) {
                res.setStatutUE(StatutUE.ECHOUE);
                return;
            }
        }
    }
    public ArrayList<UE> obtenirUEAccessibles(Etudiant e, ArrayList<UE> toutesUE) {
        ArrayList<UE> accessibles = new ArrayList<>();
        for (UE ue : toutesUE) {
            if (possedePrerequis(ue,e)) {
                accessibles.add(ue);
            }
        }
        return accessibles;
    }
    public void passerSemestre(Etudiant e) {
        if (e.getSemestreCourant() == Semestre.IMPAIR)
            e.setSemestreCourant(Semestre.PAIR);
        else
            e.setSemestreCourant(Semestre.IMPAIR);
    }


}

