package fr.miage.projetjava;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Log4j2 OK");
    }

}