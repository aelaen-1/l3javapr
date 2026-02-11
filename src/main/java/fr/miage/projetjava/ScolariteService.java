package fr.miage.projetjava;

import java.util.ArrayList;

public class ScolariteService {

    public Boolean prerequis(UE ue, Etudiant etudiant){
        ArrayList<ResultatUE> listeUE = etudiant.getCursus(); //récupération liste des UE passé par l'étudiant peu importe son statut
        ArrayList<UE> UEvalide = new ArrayList<>(); //liste pour stocké les UE validé par l'étudiant pour après pouvoir comparé avec les UE nécessaire pour le pré-requis

        for (int i = 0; i<listeUE.size() ; i++){ //ajout des UE validé par l'étudiant dans la liste UEvalide
            if(listeUE.get(i).getStatut() == StatutUE.VALIDE){
                UEvalide.add(listeUE.get(i).getUe());
            }
        }

        ArrayList<UE> UEprerequis = ue.getUEprerequis();//récupération des UE pré-requis de l'UE en paramètre
        if(UEprerequis.isEmpty()){
            return true;
        }
        else if(!UEprerequis.isEmpty() && UEvalide.isEmpty()){ //s'il y a des pré-requis et que la liste des ue valides est vide
            return false;
        }
        return compare(UEvalide, UEprerequis);
    }

    //avoir validé 180 ECTS, avoir validé les UE obligatoire du parcours
    public Boolean estDiplome(Etudiant etudiant){
        ArrayList<ResultatUE> listeUE = etudiant.getCursus(); //récupération liste des UE passé par l'étudiant peu import son statut
        ArrayList<UE> UEvalide = new ArrayList<>(); //liste pour stocké les UE validé par l'étudiant pour après pouvoir comparé avec les UE nécessaire pour le pré-requis

        int nbECTS = 0;
        for (int i = 0; i<listeUE.size() ; i++){ //ajout des UE validé par l'étudiant dans la liste UEvalide
            if(listeUE.get(i).getStatut() == StatutUE.VALIDE){
                nbECTS +=listeUE.get(i).getUe().getCredit();
                UEvalide.add(listeUE.get(i).getUe());
            }
        }
        if(nbECTS<180){
            return false;
        }

        ArrayList<UE> UEobligatoire = etudiant.getParcours().getUEObligatoire();//récupération des UE obligaoires que doit passer l'étudiant
        return compare(UEvalide, UEobligatoire);
    }

    public Boolean compare (ArrayList<UE> UEvalide, ArrayList<UE> UEcomparaison){
        while(!UEcomparaison.isEmpty()){
            if(UEvalide.contains(UEcomparaison.getFirst())){
                UEcomparaison.removeFirst();
            }
            else {
                return false;
            }

        }
        return true;
    }
}

