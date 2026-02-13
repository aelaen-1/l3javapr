package fr.miage.projetjava;

import java.util.ArrayList;

public class ScolariteService {

    public int remplirListeUEValidees(ArrayList<UE> UEvalidees, ArrayList<ResultatUE> resultats) {

        int totalEcts = 0;

        for (int i = 0; i < resultats.size(); i++) {
            if (resultats.get(i).getStatut() == StatutUE.VALIDE) {
                totalEcts += resultats.get(i).getUe().getCredit();
                UEvalidees.add(resultats.get(i).getUe());
            }
        }
        return totalEcts;
    }

    // vérifie que les UE obligatoires/pré-requises ont été validées par l'étudiant
    public Boolean verifierValidationDesUEObligatoires(ArrayList<UE> UEvalidees, ArrayList<UE> UEObligatoires) {

        ArrayList<UE> tempList = new ArrayList<>(UEObligatoires);
        while (!tempList.isEmpty()) {
            if (UEvalidees.contains(tempList.getFirst()))
                tempList.removeFirst();
            else
                return false;
        }
        return true;
    }

    public Boolean verifierValidationPrerequis(UE ue, Etudiant etudiant) {

        ArrayList<ResultatUE> resultats = etudiant.getResultatsUE();
        ArrayList<UE> UEvalidees = new ArrayList<>();
        remplirListeUEValidees(UEvalidees, resultats);

        ArrayList<UE> UEprerequis = new ArrayList<>(ue.getUEprerequis());
        return verifierValidationDesUEObligatoires(UEvalidees, UEprerequis);
    }

    public Boolean estDiplome(Etudiant etudiant) {

        ArrayList<ResultatUE> resultats = etudiant.getResultatsUE();
        ArrayList<UE> UEObligatoires = etudiant.getParcours().getUEObligatoire();
        ArrayList<UE> UEvalidees = new ArrayList<>();

        int totalEcts = remplirListeUEValidees(UEvalidees, resultats);
        return ((totalEcts >= 180) && verifierValidationDesUEObligatoires(UEvalidees,
                UEObligatoires));
    }
}
