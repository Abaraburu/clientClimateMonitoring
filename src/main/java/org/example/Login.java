package org.example;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Login {
    private JPanel panel1;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    private Home home; // Riferimento alla finestra principale

    public Login(Home home) {
        this.home = home; // Inizializza il riferimento

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Connessione al server RMI
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

                boolean isValid = stub.validateCredentials(username, password);

                if (isValid) {
                    JOptionPane.showMessageDialog(null, "Accesso riuscito!", "Login", JOptionPane.INFORMATION_MESSAGE);

                    // Rendi visibili i pulsanti
                    home.showOperatorButtons();

                    // Chiudi la finestra di login
                    SwingUtilities.getWindowAncestor(panel1).dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Credenziali errate!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Errore di Login: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public JPanel getPanel() {
        return panel1;
    }
}
