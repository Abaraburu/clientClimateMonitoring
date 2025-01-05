package org.aglia;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe per la gestione del login degli operatori.
 * Consente l'autenticazione tramite credenziali e comunica con il server RMI.
 *
 * @author Agliati Lorenzo 753378
 */
public class Login {

    private JPanel panel1; // Pannello principale della GUI
    private JTextField usernameField; // Campo di input per l'username
    private JPasswordField passwordField; // Campo di input per la password
    private JButton loginButton; // Pulsante per effettuare il login
    private JLabel usernameLabel; // Etichetta per il campo username
    private JLabel passwordLabel; // Etichetta per il campo password

    private Home home; // Riferimento alla finestra principale dell'applicazione

    /**
     * Costruttore della classe Login.
     * Configura la GUI e i listener per l'autenticazione.
     *
     * @param home Riferimento alla finestra principale dell'applicazione.
     */
    public Login(Home home) {
        this.home = home; // Inizializza il riferimento alla finestra principale

        // Listener per spostarsi al campo password premendo Invio
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus(); // Sposta il focus al campo password
                }
            }
        });

        // Listener per effettuare il login premendo Invio nel campo password
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick(); // Simula il clic sul pulsante di login
                }
            }
        });

        // Listener per il pulsante di login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText(); // Ottiene l'username inserito
            String password = new String(passwordField.getPassword()); // Ottiene la password inserita

            try {
                // Connessione al server RMI
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

                // Valida le credenziali tramite il server
                boolean isValid = stub.validateCredentials(username, password);

                if (isValid) {
                    SessionManager.setLoggedInUser(username); // Salva l'utente autenticato
                    home.onLoginSuccess(); // Notifica il successo del login alla finestra principale
                    SwingUtilities.getWindowAncestor(panel1).dispose(); // Chiude la finestra di login
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali errate!", "Errore", JOptionPane.ERROR_MESSAGE); // Mostra un messaggio di errore
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Stampa l'eccezione per il debug
                JOptionPane.showMessageDialog(null, "Errore di Login: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Mostra un messaggio di errore
            }
        });
    }

    /**
     * Restituisce il pannello principale della GUI.
     *
     * @return Pannello principale della GUI.
     */
    public JPanel getPanel() {
        return panel1; // Ritorna il pannello principale
    }
}