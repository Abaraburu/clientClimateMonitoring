package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Login {
    private JPanel panel1;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    // Connessione al server RMI
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

                    // Chiamata al metodo remoto per validare le credenziali
                    boolean isValid = stub.validateCredentials(username, password);

                    if (isValid) {
                        JOptionPane.showMessageDialog(null, "Accesso riuscito!", "Login", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Credenziali errate!", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Errore di Login: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JPanel getPanel() {
        return panel1;
    }
}
