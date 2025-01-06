package org.example;

/**
 * Classe per gestire lo stato della sessione dell'applicazione.
 * Consente di memorizzare e recuperare l'utente attualmente autenticato.
 *
 * @author Agliati Lorenzo 753378
 */
public class SessionManager {

    private static String loggedInUser; // Nome dell'utente attualmente autenticato

    /**
     * Imposta l'utente attualmente autenticato.
     *
     * @param username Nome dell'utente da autenticare.
     */
    public static void setLoggedInUser(String username) {
        loggedInUser = username; // Salva il nome dell'utente autenticato
    }

    /**
     * Restituisce il nome dell'utente attualmente autenticato.
     *
     * @return Nome dell'utente autenticato o null se nessun utente è autenticato.
     */
    public static String getLoggedInUser() {
        return loggedInUser; // Ritorna il nome dell'utente autenticato
    }
}