package org.example;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ModificaCentroMonitoraggioOperatore {
    private JLabel usernameLabel;
    private JComboBox<String> comboBoxCentroMon;
    private JButton buttonModifica;
    private JPanel jpanel1;

    /**
     * Costruttore della classe ModificaCentroMonitoraggioOperatore.
     * Inizializza la GUI, imposta l'username dell'operatore connesso, popola la comboBox e gestisce il pulsante di modifica.
     */
    public ModificaCentroMonitoraggioOperatore() {
        // Recupera l'username dell'operatore connesso dalla SessionManager
        String username = SessionManager.getLoggedInUser();

        // Imposta l'username nella label
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText(username);
            buttonModifica.setEnabled(true); // Abilita il pulsante se c'è un operatore connesso
        } else {
            usernameLabel.setText("Errore: Nessun operatore connesso");
            buttonModifica.setEnabled(false); // Disabilita il pulsante se non c'è un operatore connesso
        }

        // Popola la comboBox con i centri di monitoraggio
        populateComboBoxCentroMon();

        // Listener per il pulsante di modifica
        buttonModifica.addActionListener(e -> onModificaButtonClicked());
    }

    /**
     * Popola la comboBox con i centri di monitoraggio disponibili.
     */
    private void populateComboBoxCentroMon() {
        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Recupera la lista dei centri di monitoraggio
            List<String> centriMonitoraggio = stub.getAllMonitoringCenters();

            // Aggiunge ogni centro alla comboBox
            for (String centro : centriMonitoraggio) {
                comboBoxCentroMon.addItem(centro);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dei centri di monitoraggio: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gestisce l'evento di clic sul pulsante "Modifica".
     */
    private void onModificaButtonClicked() {
        String username = SessionManager.getLoggedInUser();
        String nomeCentroMonitoraggio = (String) comboBoxCentroMon.getSelectedItem();

        // Controlla se è stato selezionato un centro di monitoraggio
        if (nomeCentroMonitoraggio == null || nomeCentroMonitoraggio.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selezionare un centro di monitoraggio.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Aggiorna il centro di monitoraggio dell'operatore
            boolean success = stub.updateCentroMonitoraggioOperatore(username, nomeCentroMonitoraggio);

            if (success) {
                JOptionPane.showMessageDialog(null, "Centro di monitoraggio aggiornato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(jpanel1).dispose(); // Chiude la finestra della GUI
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante l'aggiornamento del centro di monitoraggio.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Restituisce il pannello principale della GUI.
     * @return Il pannello principale.
     */
    public JPanel getPanel() {
        return jpanel1; // Ritorna il pannello principale della GUI
    }
}