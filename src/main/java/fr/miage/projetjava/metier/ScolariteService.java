package fr.miage.projetjava.metier;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import fr.miage.projetjava.dao.ConnexionBD;
import fr.miage.projetjava.dao.EtudiantUpdateBD;
import fr.miage.projetjava.dao.ResultatUEBD;
import fr.miage.projetjava.model.Etudiant;
import fr.miage.projetjava.model.Mention;
import fr.miage.projetjava.model.ResultatUE;
import fr.miage.projetjava.model.Semestre;
import fr.miage.projetjava.model.StatutUE;
import fr.miage.projetjava.model.UE;
import javafx.scene.control.Alert;

import static java.lang.Integer.parseInt;

public class ScolariteService {

    private static Connection connexion = ConnexionBD.connexionBD();

    /**
     * cette méthode permet d'obtenir les Ues accessibles par un etudiant
     * elle prend en entrée un etudiant et la liste de TOUTES les Ues
     * si l'utilisateur veut voir les Ues accessibles par un etudiant, le controller
     * doit faire appel a cete methode
     * @param etudiant ,l'etudiant où on veut savoir ses Ues accessibles
     * @param toutesLesUE  toutes les Ues de sa mention
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

    /**
     * methode pour inscrire un etudiant a une Ue
     * @param e , un etudiant
     * @param ue, une Ue
     * @param annee, Une année
     * @param semestre, Un sesmestre
     * @return
     */
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Limite atteinte");
            alert.setHeaderText("Surcharge de crédits");
            alert.setContentText("Impossible d'ajouter " + ue.getIntitule() + " (" + ue.getCredit() + " ECTS).\n" +
                        "Le total des ECTS pour ce semestre dépasserait de 39 crédits.");
            alert.showAndWait();
            return false;

        }
        // Si tout est bon, on procède à l'inscription
        ResultatUE inscription = new ResultatUE(ue, annee, semestre, StatutUE.ENCOURS);
        e.getResultatsUE().add(inscription);

        //on va insérer le résultat dans la bd
        ResultatUEBD.insertResultatUE(connexion, e.getNumE(), ue.getCode(), parseInt(annee), semestre.toString(), StatutUE.ENCOURS.toString());

        return true;
    }

    /**
     * méthode pour vérifier si un étudiant possède le prérequis de l'UE dans lequl on veut l'inscrire
     * @param uecible
     * @param e
     * @return
     */

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

    /**
     * cette méthode permet savoir si un etudiant peut avoir son diplome
     * @param e , l'etudiant où il fallait tester s'il est diplomé ou pas
     * */
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

    /**
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
                EtudiantUpdateBD.updateResultatUE( connexion,  e.getNumE(),  res.getUe().getCode(), StatutUE.VALIDE);
                return true;
            }
        }
        // s'il existe pas alors on retourne false car on peut pas valider qu on la suit
        // pas
        return false;
    }

    /**
     * cette methode permet de marquer Echouer une Ue suivit par l'etudiant
     */
    public boolean echoueUE(Etudiant e, UE ue) {
        // on recupere les resulats de l'etudiant
        for (ResultatUE res : e.getResultatsUE()) {
            // on cherche l'Ue correspondant
            if (res.getUe().getCode().equals(ue.getCode())) {
                // on marque statut ECHOUE
                res.setStatut(StatutUE.ECHOUE);
                EtudiantUpdateBD.updateResultatUE( connexion,  e.getNumE(),  res.getUe().getCode(), StatutUE.ECHOUE);
                return true;
            }
        }
        // si l'Ue n est pas suivi par l'etudiant alors on retourne false
        return false;
    }

    /**
     * cette methode permet de passer un etudiant d'un semesre cournat à un semestre
     * suivant
     * @param e ,l'etudiant qu'on veut faire passer au semestre suivant
     */
    public void passerSemestre(Etudiant e) {
        // on recupere le semestre Cournat de l'etudiant suivant et on teste s'il est
        // Impair
        if (e.getSemestreCourant() == Semestre.IMPAIR){
            // on change à PAIR
            e.setSemestreCourant(Semestre.PAIR);
            EtudiantUpdateBD.updateSemestre(connexion,  e.getNumE(),  Semestre.PAIR);
        }
        else{
            // sion on change à IMPAIR le semestre courant
            e.setSemestreCourant(Semestre.IMPAIR);
            EtudiantUpdateBD.updateSemestre(connexion, e.getNumE(), Semestre.IMPAIR);
        }

    }

    /**
     * Simule le parcours le plus rapide possible pour obtenir la licence (180 ECTS
     * + Obligatoires).
     * 
     * @param etudiant    L'étudiant concerné
     * @param toutesLesUE La liste globale des UEs de la formation
     * @return Le nombre de semestres supplémentaires nécessaires
     */
    public int simulerDureeOptimale(Etudiant etudiant, List<UE> toutesLesUE) {

        int totalCredits = 0;
        ArrayList<ResultatUE> simulationResultats = new ArrayList<>(etudiant.getResultatsUE());
        ArrayList<UE> obligatoiresRestantes = new ArrayList<>(etudiant.getParcours().getUEObligatoire());
        // On cherche l'année et le semestre actuels de l'étudiant
        int anneeSimulee = java.time.Year.now().getValue();
        Semestre semestreSimule = etudiant.getSemestreCourant();

        /*
         * 1ère boucle pour voir combien on a d'ects actuellement et quelles UE
         * obligatoires on a validé
         */
        for (ResultatUE res : simulationResultats) {
            if (res.getStatut() == StatutUE.VALIDE) {
                totalCredits += res.getUe().getCredit();
                // si cette ue est obligatoire, on la retire de obligatoiresRestantes
                for (int i = 0; i < obligatoiresRestantes.size(); i++) {
                    if (obligatoiresRestantes.get(i).getCode().equals(res.getUe().getCode())) {
                        obligatoiresRestantes.remove(res.getUe());
                        break;
                    }
                }
            }
        }
        // 2nde boucle pour connaitre l'année courante (= celle du dernier résultat)
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

        // 3rd boucle pour avoir la liste de toutes les UE disponibles que l'on n'a pas
        // validé
        Mention mention = etudiant.getParcours().getMention();
        List<UE> uesRestantes = new ArrayList<>();
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
                    uesRestantes.add(ue);
                }
            }
        }

        int semestresSupplementaires = 0;
        // On boucle tant que le total credit est inférieur à 180 credits et qu'il reste
        // des matières obligatoires
        // on arrete la boucle si au bout de 6 ans l'etudiant n'a pas abotenu son
        // diplome c est pas démandé mais pour etre coherant
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

            // on récupère la liste des UE auxquelles on peut s'inscrire (= dont on a validé
            // tous les prérequis)
            List<UE> uesAccessibles = new ArrayList<>();
            for (UE ue : uesRestantes) {
                if (verifierPrerequisSimulation(ue, simulationResultats))
                    uesAccessibles.add(ue);
            }

            /* Algo de tri qui compare chaque UE à toutes les autres pour comparer
            leur niveau de priorité. Si une UE est prioritaire, on la swap
            dans la liste des UE accessibles pour s'y inscrire */
            for (int i = 0; i < uesAccessibles.size() - 1; i++) {
                for (int j = i + 1; j < uesAccessibles.size(); j++) {
                    UE ue1 = uesAccessibles.get(i);
                    UE ue2 = uesAccessibles.get(j);

                    int prioriteUE1 = calculerPrioriteUE(ue1, uesRestantes, obligatoiresRestantes);
                    int prioriteUE2 = calculerPrioriteUE(ue2, uesRestantes, obligatoiresRestantes);

                    if (prioriteUE2 > prioriteUE1) {
                        uesAccessibles.set(i, ue2);
                        uesAccessibles.set(j, ue1);
                    }
                }
            }

            int creditsCeSemestre = 0;
            List<UE> uesChoisiesCeSemestre = new ArrayList<>();
            //remplissage du semestre
            for (UE ue : uesAccessibles) {
            if (creditsCeSemestre + ue.getCredit() <= 39) {
                if (!estDansLaListe(ue, obligatoiresRestantes) 
                        && totalCredits + creditsCeSemestre >= 180) {
                    continue;
                }
                creditsCeSemestre += ue.getCredit();
                uesChoisiesCeSemestre.add(ue);
            }
        }
            

            String anneeStr = String.valueOf(anneeSimulee);

            // si aucune matière ne peut être prise (bloqué), on arrête
            if (uesChoisiesCeSemestre.isEmpty()) {
                break;
            }
            // On valide les matières pour la suite de la simulation
            for (UE ue : uesChoisiesCeSemestre) {
                simulationResultats.add(new ResultatUE(ue, anneeStr, semestreSimule, StatutUE.VALIDE));
                uesRestantes.remove(ue);
                // au lieu de faire directement obligatoiresRestantes.remove(ue), on vérifie
                // qu'elle y soit;
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

    /** Méthode de vérification des prérequis basée sur une liste de résultats (pour la simulation)
     * @param ue
     * @param resultats
     * @return
     * */
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

    /** methode pour savoir si une Une est dans une liste d'ues chosies ou pas
     * @param ue
     * @param listeUE
     * @return
     */
    public boolean estDansLaListe(UE ue, List<UE> listeUE) {
        for (UE elt : listeUE) {
            if (ue.getCode().equals(elt.getCode()))
                return true;
        }
        return false;
    }

    /**
     * méthode qui donne un score de priorité en fonction de si elle est obligatoire
     * et si elle est prérequis d'autres ue (si elle "bloque" d'autres ue)
     * plus elle est bloquante/prérequis pour d'autres ue, plus elle est prioritaire
     * @param ue
     * @param obligatoiresRestantes
     * @param uesRestantes
     * @return
     */
    public int calculerPrioriteUE(UE ue, List<UE> uesRestantes, List<UE> obligatoiresRestantes) {
        int niveauDePriorite = 0;

        if (estDansLaListe(ue, obligatoiresRestantes)) {
            niveauDePriorite += 1000;
        }
        int nbBloquees = 0;
        for (UE autreUE : uesRestantes) {
            if (autreUE.getUEprerequis() != null) {
                for (UE pre : autreUE.getUEprerequis()) {
                    if (pre.getCode().equals(ue.getCode())) {
                        nbBloquees++;
                        break;
                    }
                }
            }
        }
        niveauDePriorite += nbBloquees * 100;
        niveauDePriorite += ue.getCredit();
        return niveauDePriorite;
    }
}
