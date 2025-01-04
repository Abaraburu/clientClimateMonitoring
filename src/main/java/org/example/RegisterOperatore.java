package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RegisterOperatore {
    private JTextField textFieldNome;
    private JTextField textFieldCognome;
    private JTextField textFieldCodicefiscale;
    private JTextField textFieldEmail;
    private JTextField textFieldPassword;
    private JTextField textFieldUsername;
    private JComboBox<String> comboBoxCentromonitoraggio;
    private JButton buttonRegistrati;
    private JPanel jpanel1;

    public RegisterOperatore() {
        populateComboBox(); // Popola la comboBox all'avvio

        buttonRegistrati.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegisterButtonClicked();
            }
        });
    }

    private void populateComboBox() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            List<String> centers = stub.getAllMonitoringCenters();
            for (String center : centers) {
                comboBoxCentromonitoraggio.addItem(center);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dei centri: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegisterButtonClicked() {
        String nome = textFieldNome.getText().trim();
        String cognome = textFieldCognome.getText().trim();
        String codiceFiscale = textFieldCodicefiscale.getText().trim();
        String email = textFieldEmail.getText().trim();
        String username = textFieldUsername.getText().trim();
        String password = textFieldPassword.getText().trim();
        String centroMonitoraggio = (String) comboBoxCentromonitoraggio.getSelectedItem();

        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Controllo se l'email è valida
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Inserire un'email valida!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            boolean success = stub.registerOperator(nome, cognome, codiceFiscale, email, username, password, centroMonitoraggio);

            if (success) {
                JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(jpanel1).dispose(); // Chiudi la finestra
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante la registrazione. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return jpanel1;
    }
}