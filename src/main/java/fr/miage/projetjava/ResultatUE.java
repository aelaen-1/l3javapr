package fr.miage.projetjava;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResultatUE {
    private UE ue;
    private String annee;
    private Semestre semestre;
    private StatutUE statut;
    private static final Logger log = LogManager.getLogger(ResultatUE.class);


    public ResultatUE(UE ue, String annee, Semestre semestre, StatutUE statut)
    {
        this.ue = ue;
        this.annee = annee;
        this.semestre = semestre;
        this.statut = statut;
    }

    public UE getUe() {
        return ue;
    }

    public String getAnnee() {
        return annee;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public StatutUE getStatut() {
        return statut;
    }

    public void setStatut(StatutUE statut) {
        this.statut = statut;
    }

    public void afficher()
    {
        String message = "nome de l'UE " + ue.getIntitule() + " UE pre-requis " + ue.getUEprerequis() + "\n Statut " + statut;
        log.info(message);
    }
}
