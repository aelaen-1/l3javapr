package fr.miage.projetjava.dao;

import fr.miage.projetjava.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    // C'est l'endroit où on range le fichier sur l'ordinateur
    private static final String FILE_PATH = "data/etudiants.csv";
    /*
     * Cette méthode sert à lire le fichier et à créer les étudiants dans le logiciel.
     */
    public ArrayList<Etudiant> chargerTout(List<Parcours> parcoursDispos, List<UE> toutesLesUE) {
        // On prépare une liste vide pour y mettre les étudiants qu'on va trouver
        ArrayList<Etudiant> etudiants = new ArrayList<>();
        // On vérifie si le fichier existe bien avant de l'ouvrir
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            System.err.println("Le fichier n'existe pas encore.");
            return etudiants;
        }

        // On ouvre le fichier pour lire ce qu'il y a dedans
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String ligne;

            // On lit le fichier ligne après ligne
            while ((ligne = br.readLine()) != null) {

                // Si une ligne est vide, on l'ignore
                if (ligne.isEmpty()) continue;

                // On découpe la ligne en morceaux dès qu'on voit un ";"
                // Le -1 permet de ne pas perdre de colonnes si la fin est vide
                String[] morceaux = ligne.split(";", -1);

                // Il nous faut au moins 5 colonnes pour faire un étudiant
                if (morceaux.length < 5) continue;

                try {
                    // On récupère les infos de base (nume, Nom, Prénom)
                    int nume = Integer.parseInt(morceaux[0]);
                    String nom = morceaux[1];
                    String prenom = morceaux[2];
                    String nomDuParcours = morceaux[3];
                    // On transforme le texte du semestre en objet Semestre
                    Semestre sem = Semestre.valueOf(morceaux[4].toUpperCase());
                    // trouver le bon parcours
                    // On cherche dans la liste des parcours celui qui a le bon nom
                    Parcours leBonParcours = null;
                    for (Parcours p : parcoursDispos) {
                        if (p.getNom().equalsIgnoreCase(nomDuParcours)) {
                            leBonParcours = p;
                            // On a trouvé, on arrête de chercher
                            break;
                        }
                    }
                    // Si on a trouvé le parcours, on crée l'étudiant
                    if (leBonParcours != null) {
                        Etudiant etu = new Etudiant(nume, nom, prenom, leBonParcours, sem);
                        // chargement des notes
                        // Si la 6ème colonne contient des notes
                        if (morceaux.length >= 6 && !morceaux[5].isEmpty()) {
                            // On sépare chaque note par la virgule
                            String[] toutesLesNotes = morceaux[5].split(",");
                            for (String noteTexte : toutesLesNotes) {
                                // On découpe les infos de la note Code:Année:Sem:Statut
                                String[] infos = noteTexte.split(":");
                                if (infos.length == 4) {
                                    String codeUE = infos[0];
                                    // On cherche l'objet UE qui correspond à ce code
                                    for (UE ue : toutesLesUE) {
                                        if (ue.getCode().equals(codeUE)) {
                                            // On crée la note et on l'ajoute à l'étudiant
                                            ResultatUE res = new ResultatUE(ue, infos[1],
                                                    Semestre.valueOf(infos[2]), StatutUE.valueOf(infos[3]));
                                            etu.addResultatUE(res);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // On ajoute l'étudiant à notre liste finale
                        etudiants.add(etu);
                    }
                } catch (Exception e) {
                    System.out.println("Erreur sur cette ligne : " + ligne);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return etudiants;
    }
    /*
     * Cette méthode sert à enregistrer la liste des étudiants dans le fichier CSV.
     */
    public void sauvegarderTout(List<Etudiant> etudiants) {
        try {
            // On crée le dossier "data" s'il n'existe pas
            Files.createDirectories(Paths.get("data"));
            // On ouvre le fichier pour écrire par dessus l'ancien
            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
                for (Etudiant e : etudiants) {
                    // On prépare le texte pour les notes
                    String blocNotes = "";
                    List<ResultatUE> notes = e.getResultatsUE();
                    for (int i = 0; i < notes.size(); i++) {
                        ResultatUE r = notes.get(i);
                        // Format Code:Année:Semestre:Statut
                        blocNotes += r.getUe().getCode() + ":" + r.getAnnee() + ":" +
                                r.getSemestre().name() + ":" + r.getStatut().name();
                        // On met une virgule seulement s'il y a une autre note après
                        if (i < notes.size() - 1) {
                            blocNotes += ",";
                        }
                    }
                    // On construit la ligne entière pour cet étudiant
                    String ligne = e.getNumE() + ";" + e.getNomE() + ";" + e.getPrenomE() + ";" +
                            e.getParcours().getNom() + ";" + e.getSemestreCourant().name() + ";" +
                            blocNotes;
                    // On écrit la ligne et on saute à la ligne suivante
                    bw.write(ligne);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}