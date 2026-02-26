package fr.miage.projetjava.vue;

import fr.miage.projetjava.vue.MainApp;
/*
 *cette méthode lance notre application en fait appel a la classe main APP
*/
public class Lanceur {
    public static void main(String[] args) {
        //Appelle le main de MainApp sans hériter d'Application
        MainApp.main(args);
    }
}