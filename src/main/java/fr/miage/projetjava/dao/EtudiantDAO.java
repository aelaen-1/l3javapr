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
            boolean premiereLigne = true;

            // On lit le fichier ligne après ligne
            while ((ligne = br.readLine()) != null) {

                // On ignore la première ligne si c'est l'en-tête (ex: "NumE;Nom;...")
                if (premiereLigne) {
                    premiereLigne = false;
                    if (ligne.startsWith("NumE")) continue; // On passe à la suite si c'est bien l'en-tête
                }

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
                    String codeDuParcours = morceaux[3]; // On utilise le code du parcours (ex: MIAGE), c'est plus sûr

                    // On transforme le texte du semestre en objet Semestre
                    Semestre sem = Semestre.valueOf(morceaux[4].toUpperCase());

                    // Trouver le bon parcours
                    Parcours leBonParcours = null;
                    for (Parcours p : parcoursDispos) {
                        // On compare avec le code du parcours (getNom() dans ton constructeur)
                        if (p.getNom().equalsIgnoreCase(codeDuParcours)) {
                            leBonParcours = p;
                            break;
                        }
                    }

                    // Si on a trouvé le parcours, on crée l'étudiant
                    if (leBonParcours != null) {
                        Etudiant etu = new Etudiant(nume, nom, prenom, leBonParcours, sem);

                        // Chargement des notes
                        // Si la 6ème colonne contient des notes
                        if (morceaux.length > 5 && !morceaux[5].isEmpty()) {
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
                                            break; // Ce break est mnt bien placé, il sort de la boucle de recherche d'UE
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
                    e.printStackTrace(); // Utile pour voir d'où vient l'erreur
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

                // On écrit l'en-tête (Optionnel, mais c'est propre)
                bw.write("NumE;Nom;Prenom;Parcours;SemestreCourant;ResultatsUE");
                bw.newLine();

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