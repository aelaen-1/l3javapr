package fr.miage.projetjava;

import java.util.ArrayList;

public class ScolariteService {

    public void remplirListeUEValidees(ArrayList<UE> UEvalidees, ArrayList<ResultatUE> resultats, int totalEcts){
        
        for (int i = 0; i < resultats.size(); i++){
            if (resultats.get(i).getStatut() == StatutUE.VALIDE) {
                if (totalEcts > -1)
                    totalEcts += resultats.get(i).getUe().getCredit();
                UEvalidees.add(resultats.get(i).getUe());
            }
        }
    }

    // vérifie que les UE obligatoires/pré-requises ont été validées par l'étudiant
    public Boolean verifierValidationDesUEObligatoires(ArrayList<UE> UEvalidees, ArrayList<UE> UEObligatoires){

        while (!UEObligatoires.isEmpty()){
            if (UEvalidees.contains(UEObligatoires.getFirst()))
                UEObligatoires.removeFirst();
            else
                return false;
        }
        return true;
    }

    public Boolean verifierValidationPrerequis(UE ue, Etudiant etudiant){

        ArrayList<ResultatUE> resultats = etudiant.getResultatsUE();
        ArrayList<UE> UEvalidees = new ArrayList<>();
        remplirListeUEValidees(UEvalidees, resultats, -1);
        
        ArrayList<UE> UEprerequis = ue.getUEprerequis();
        return verifierValidationDesUEObligatoires(UEvalidees, UEprerequis);
    }

    public Boolean estDiplome(Etudiant etudiant){

        ArrayList<ResultatUE> resultats = etudiant.getResultatsUE();
        ArrayList<UE> UEObligatoires = etudiant.getParcours().getUEObligatoire();
        ArrayList<UE> UEvalidees = new ArrayList<>();
        int totalEcts = 0;

        remplirListeUEValidees(UEvalidees, resultats, totalEcts);
        return ((totalEcts >= 180) && verifierValidationDesUEObligatoires(UEvalidees, UEObligatoires));
    }
}

