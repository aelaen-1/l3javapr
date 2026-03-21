package fr.miage.projetjava.metier;

import java.util.ArrayList;
import java.util.List;

import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.ResultatUE;
import fr.miage.projetjava.model.Semestre;
import fr.miage.projetjava.model.StatutUE;
import fr.miage.projetjava.model.UE;

public class ScolariteService {
    /*
     * cette méthode permet d'obtenir les Ues accessibles par un etudiant
     * elle prend en entrée un etudiant et la liste de TOUTES les Ues
     * si l'utilisateur veut voir les Ues accessibles par un etudiant, le controller
     * doit faire appel a cete methode
     */
    public List<UE> obtenirUEAccessibles(Etudiant etudiant, List<UE> toutesLesUE) {
        // on crée un tableaux des Ues pour stocker les Ues accessibmes par l'étudiant
        ArrayList<UE> accessibles = new ArrayList<>();

        // on recupere la mention de l'Etudiant (ex: MIASHS ou Biologie)
        Mention mentionEtudiant = etudiant.getParcours().getMention();

        // on recupere les Ues de la formation globale de l'etudiant
        List<UE> uesdelaformation = new ArrayList<>();
        for (UE ue : toutesLesUE) {
            if (ue.getMention() == mentionEtudiant) {
                uesdelaformation.add(ue);
            }
        }

        // On boucle sur toutes les UE de la formation
        for (UE ueCible : uesdelaformation) {
            // On part du principe que c'est bon, et on cherche si un prérequis manque
            boolean possedePrerequis = true;

            // Si la matière a des prérequis, on les vérifie un par un
            if (ueCible.getUEprerequis() != null && !ueCible.getUEprerequis().isEmpty()) {
                for (UE prerequis : ueCible.getUEprerequis()) {
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
                        break; // On arrête de chercher les autres prérequis, c'est mort pour cette UE
                    }
                }
            } // Fin de la vérification des prérequis

            // Si l'étudiant a tous les prérequis validés (ou s'il n'y en a pas du tout)
            if (possedePrerequis) {
                // On cherche si l'étudiant a déjà été inscrit à cette UE précise
                ResultatUE historique = null;
                for (ResultatUE res : etudiant.getResultatsUE()) {
                    // C'est bien ueCible qu'on compare ici
                    if (res.getUe().getCode().equals(ueCible.getCode())) {
                        historique = res;
                        break; // On a trouvé son historique pour cette matière
                    }
                }

                // Si l'historique est vide, c'est qu'il ne l'a jamais faite. On l'ajoute !
                if (historique == null) {
                    accessibles.add(ueCible);
                }
                // Sinon il a été inscrit au moins une fois, on regarde s'il l'a ratée
                else {
                    StatutUE statut = historique.getStatut();
                    // Si c'est ECHOUE, il peut la reprendre mais au semestre suivant
                    if (statut == StatutUE.ECHOUE) {
                        if (!historique.getSemestre().equals(etudiant.getSemestreCourant())) {
                            accessibles.add(ueCible);
                        }
                    }
                }
            }
        }
        // on retourne le tableau des Ues accessibles, prêt pour l'affichage
        return accessibles;
    }

    public boolean inscrireEtudiant(Etudiant e, UE ue, String annee, Semestre semestre) {
        // Vérification des prérequis
        if (!possedePrerequis(ue, e)) {
            return false;
        }
        // Calcul du total des crédits POUR CE SEMESTRE précis
        int creditsDuSemestre = 0;
        for (ResultatUE res : e.getResultatsUE()) {
            // On additionne les crédits de toutes les matières de cette année ET de ce
            // semestre
            if (res.getAnnee().equals(annee) && res.getSemestre() == semestre) {
                creditsDuSemestre += res.getUe().getCredit();
            }
        }
        // On bloque si la nouvelle matière fait dépasser les 39 crédits sur le semestre
        if (creditsDuSemestre + ue.getCredit() > 39) {
            System.out.println("Inscription bloquée : La limite de 39 ECTS par semestre est atteinte.");
            return false;
        }
        // Si tout est bon, on procède à l'inscription
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
            if (!valide)
                return false;
        }
        return true;
    }

    /* cette méthode permet savoir si un etudiant peut avoir son diplome */
    public Boolean estDiplome(Etudiant e) {
        // on initilase à 0 un compteur qui calcul les credits de l'etudiant
        int totalcredits = 0;
        // on parcout dans les resutats de l'etudiant
        for (ResultatUE res : e.getResultatsUE()) {
            // on ajoute les credit des Ues validés par l'etuadiant
            if (res.getStatut() == StatutUE.VALIDE) {
                totalcredits += res.getUe().getCredit();
            }
        }
        // on verifie si total credit est inferieur à 180
        if (totalcredits < 180)
            // si oui on retourne false
            return false;

        // On verifie s'il a validé toutes les Ues obligatoires du parcours
        for (UE ueObligatoire : e.getParcours().getUEObligatoire()) {
            // varibale boolean pour verfie s'il a validé toutes les Ues de l'etudiant
            boolean valide = false;
            for (ResultatUE res : e.getResultatsUE()) {
                if (res.getUe().getCode().equals(ueObligatoire.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                    // si l'ue a un statut VALIDE , la variable valide deveint true
                    valide = true;
                    break;
                }
            }
            // si au moins une Ue du parcours n est pas validé la variable est false
            if (!valide)
                // on retourne false
                return false;
        }
        // si l'etudiant à un credit >=180 et a validé toutes les Ues obligatoires du
        // parcours
        // alors on retourne true ,il est diplomé
        return true;
    }

    /*
     * Cette methode permet de changer le statut d'une UE de l'etudant ( qui suit au
     * semestre courant) à VALIDE
     */
    public boolean validerUE(Etudiant e, UE ue) {
        // on boucle sur Resutlat de l'etudant
        for (ResultatUE res : e.getResultatsUE()) {
            // on recupere l'UE correspondant
            if (res.getUe().getCode().equals(ue.getCode())) {
                // on change le statut
                res.setStatut(StatutUE.VALIDE);
                return true;
            }
        }
        // s'il existe pas alors on retourne false car on peut pas valider qu on la suit
        // pas
        return false;
    }

    /* cette methode permet de marquer Echouer une Ue suivit par l'etudiant */
    public boolean echoueUE(Etudiant e, UE ue) {
        // on recupere les resulats de l'etudiant
        for (ResultatUE res : e.getResultatsUE()) {
            // on cherche l'Ue correspondant
            if (res.getUe().getCode().equals(ue.getCode())) {
                // on marque statut ECHOUE
                res.setStatut(StatutUE.ECHOUE);
                return true;
            }
        }
        // si l'Ue n est pas suivi par l'etudiant alors on retourne false
        return false;
    }

    /*
     * cette methode permet de passer un etudiant d'un semesre cournat à un semestre
     * suivant
     */
    public void passerSemestre(Etudiant e) {
        // on recupere le semestre Cournat de l'etudiant suivant et on teste s'il est
        // Impair
        if (e.getSemestreCourant() == Semestre.IMPAIR)
            // on change à PAIR
            e.setSemestreCourant(Semestre.PAIR);
        else
            // sion on change à IMPAIR le semestre courant
            e.setSemestreCourant(Semestre.IMPAIR);
    }

    /**
     * Simule le parcours le plus rapide possible pour obtenir la licence (180 ECTS
     * + Obligatoires).
     * 
     * @param etudiant    L'étudiant concerné
     * @param toutesLesUE La liste globale des UEs de la formation
     * @return Le nombre de semestres supplémentaires nécessaires
     */
    public int simulerDureeOptimale(Etudiant etudiant, ArrayList<UE> toutesLesUE) {
        ArrayList<ResultatUE> simulationResultats = new ArrayList<>(etudiant.getResultatsUE());
        int totalCredits = 0;
        ArrayList<UE> obligatoiresRestantes = new ArrayList<>(etudiant.getParcours().getUEObligatoire());
        // On cherche l'année et le semestre actuels de l'étudiant
        int anneeSimulee = java.time.Year.now().getValue();
        for (ResultatUE res : etudiant.getResultatsUE()) {
            try {
                int annee = Integer.parseInt(res.getAnnee());
                if (annee > anneeSimulee) {
                    anneeSimulee = annee;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Semestre semestreSimule = etudiant.getSemestreCourant();
        // On compte les crédits déjà acquis et on retire les obligatoires déjà validées
        for (ResultatUE res : simulationResultats) {
            if (res.getStatut() == StatutUE.VALIDE) {
                totalCredits += res.getUe().getCredit();
                // on va rajouter dans la classe UE un CampareTO pour supprimer la bonne UE
                obligatoiresRestantes.remove(res.getUe());
            }
        }
        // On récupère toutes les matières de la Mention qui n'ont pas encore été
        // validées
        Mention mention = etudiant.getParcours().getMention();
        List<UE> uesDisponibles = new ArrayList<>();
        for (UE ue : toutesLesUE) {
            if (ue.getMention() == mention) {
                boolean dejaValidee = false;
                for (ResultatUE res : simulationResultats) {
                    if (res.getUe().getCode().equals(ue.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                        dejaValidee = true;
                        break;
                    }
                }
                if (!dejaValidee) {
                    uesDisponibles.add(ue);
                }
            }
        }
        int semestresSupplementaires = 0;
        // On boucle tant que le total credit est inférieur à 180 credits et qu'il reste
        // des matières obligatoires
        // on arrete la boucle si au bout de 6 ans l'etudiant n'a pas abotenu son
        // diplome
        // c est pas démandé mais pour etre coherant
        while ((totalCredits < 180 || !obligatoiresRestantes.isEmpty()) && semestresSupplementaires < 12) {
            semestresSupplementaires++;
            // avancement du temps pour simuler le semestre
            if (semestreSimule == Semestre.IMPAIR) {
                semestreSimule = Semestre.PAIR;
            } else {
                semestreSimule = Semestre.IMPAIR;
                // on passe à l'année suivante
                anneeSimulee++;
            }
            String anneeStr = String.valueOf(anneeSimulee);
            int creditsCeSemestre = 0;
            List<UE> uesChoisiesCeSemestre = new ArrayList<>();

            /*
             * ici on fait un tri sur Les Ues obligatoires du parcous et Ues Restantes de la
             * formation
             * uesDisponibles trier pour prioriser les Ues Verrous (quand une Ue est
             * prerequis pour d'autres Ues il faut que l'algo priorise cet UE)
             */

            // remplissage du semestre
            for (UE ue : new ArrayList<>(uesDisponibles)) {
                if (verifierPrerequisSimulation(ue, simulationResultats)
                        && (creditsCeSemestre + ue.getCredit() <= 39)) {
                    // Si c'est une optionnelle et qu'on a déja les 180 crédits (on cherche juste à
                    // finir les obligatoires), on ne la prend pas.
                    if (!obligatoiresRestantes.contains(ue) && (totalCredits + creditsCeSemestre >= 180)) {
                        continue;
                    }
                    creditsCeSemestre += ue.getCredit();
                    uesChoisiesCeSemestre.add(ue);
                }
            }
            // si aucune matière ne peut être prise (bloqué), on arrête
            if (uesChoisiesCeSemestre.isEmpty()) {
                break;
            }
            // On valide les matières pour la suite de la simulation
            for (UE ue : uesChoisiesCeSemestre) {
                simulationResultats.add(new ResultatUE(ue, anneeStr, semestreSimule, StatutUE.VALIDE));
                uesDisponibles.remove(ue);
                for (int i = 0; i < obligatoiresRestantes.size(); i++) {
                    if (obligatoiresRestantes.get(i).getCode().equals(ue.getCode())) {
                        obligatoiresRestantes.remove(i);
                        break;
                    }
                }
                totalCredits += ue.getCredit();
            }
        }
        return semestresSupplementaires;
    }

    // Vérifie si une UE bloque d'autres matières dans la liste restante
    private boolean estUnPrerequis(UE ueCible, ArrayList<UE> uesRestantes) {
        for (UE ue : uesRestantes) {
            if (ue.getUEprerequis() != null && ue.getUEprerequis().contains(ueCible)) {
                return true;
            }
        }
        return false;
    }

    // Vérification des prérequis basée sur une liste de résultats (pour la
    // simulation)
    private boolean verifierPrerequisSimulation(UE ue, List<ResultatUE> resultats) {
        if (ue.getUEprerequis() == null || ue.getUEprerequis().isEmpty()) {
            return true;
        }
        for (UE pre : ue.getUEprerequis()) {
            boolean valide = false;
            for (ResultatUE res : resultats) {
                if (res.getUe().getCode().equals(pre.getCode()) && res.getStatut() == StatutUE.VALIDE) {
                    valide = true;
                    break;
                }
            }
            if (!valide) {
                return false;
            }
        }
        return true;
    }
}