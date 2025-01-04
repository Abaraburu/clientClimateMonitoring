package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegisterAreaInteresse {
    private JTextField textFieldNome;
    private JTextField textFieldNomeASCII;
    private JTextField textFieldStato;
    private JTextField textFieldStatoCodice;
    private JTextField textFieldLatitudine;
    private JTextField textFieldLongitudine;
    private JButton aggiungiButton;
    private JPanel jpanel1;

    public RegisterAreaInteresse() {
        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddButtonClicked();
            }
        });
    }

    private void onAddButtonClicked() {
        String nome = textFieldNome.getText().trim();
        String nomeASCII = textFieldNomeASCII.getText().trim();
        String stato = textFieldStato.getText().trim();
        String statoCodice = textFieldStatoCodice.getText().trim();
        String latitudineStr = textFieldLatitudine.getText().trim();
        String longitudineStr = textFieldLongitudine.getText().trim();

        if (nome.isEmpty() || nomeASCII.isEmpty() || stato.isEmpty() || statoCodice.isEmpty() || latitudineStr.isEmpty() || longitudineStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Controllo che statoCodice abbia massimo due lettere
        if (statoCodice.length() > 2 || !statoCodice.matches("[A-Za-z]{1,2}")) {
            JOptionPane.showMessageDialog(null, "Il codice dello stato deve contenere al massimo due lettere!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double latitudine = Double.parseDouble(latitudineStr);
            double longitudine = Double.parseDouble(longitudineStr);

            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Metodo per aggiungere l'area di interesse
            boolean success = stub.registerArea(nome, nomeASCII, stato, statoCodice, latitudine, longitudine);

            if (success) {
                JOptionPane.showMessageDialog(null, "Area di interesse aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(jpanel1).dispose(); // Chiude la finestra
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta dell'area. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Latitudine e Longitudine devono essere valori numerici!", "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return jpanel1;
    }
}