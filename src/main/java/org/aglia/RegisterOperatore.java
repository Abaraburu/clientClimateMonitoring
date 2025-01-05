package org.aglia;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Classe per la gestione della registrazione di nuovi operatori.
 * Consente di inserire i dettagli di un operatore e inviarli al server RMI per la registrazione.
 *
 * @author Agliati Lorenzo 753378
 */
public class RegisterOperatore {

    private JTextField textFieldNome; // Campo di input per il nome dell'operatore
    private JTextField textFieldCognome; // Campo di input per il cognome dell'operatore
    private JTextField textFieldCodicefiscale; // Campo di input per il codice fiscale
    private JTextField textFieldEmail; // Campo di input per l'email
    private JTextField textFieldPassword; // Campo di input per la password
    private JTextField textFieldUsername; // Campo di input per l'username
    private JComboBox<String> comboBoxCentromonitoraggio; // ComboBox per selezionare un centro di monitoraggio
    private JButton buttonRegistrati; // Pulsante per completare la registrazione
    private JPanel jpanel1; // Pannello principale della GUI

    /**
     * Costruttore della classe RegisterOperatore.
     * Configura la GUI e popola la comboBox con i centri di monitoraggio disponibili.
     */
    public RegisterOperatore() {
        populateComboBox(); // Popola la comboBox con i centri di monitoraggio

        // Listener per il pulsante di registrazione
        buttonRegistrati.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegisterButtonClicked(); // Gestisce l'evento di clic sul pulsante
            }
        });
    }

    /**
     * Popola la comboBox con i centri di monitoraggio disponibili recuperati dal server RMI.
     */
    private void populateComboBox() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al server RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub remoto del servizio

            List<String> centers = stub.getAllMonitoringCenters(); // Recupera la lista dei centri di monitoraggio
            for (String center : centers) {
                comboBoxCentromonitoraggio.addItem(center); // Aggiunge ogni centro alla comboBox
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dei centri: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
        }
    }

    /**
     * Gestisce l'evento di clic sul pulsante "Registrati".
     * Valida i dati di input e invia al server RMI i dettagli del nuovo operatore per la registrazione.
     */
    private void onRegisterButtonClicked() {
        // Recupera i valori dai campi di input
        String nome = textFieldNome.getText().trim();
        String cognome = textFieldCognome.getText().trim();
        String codiceFiscale = textFieldCodicefiscale.getText().trim();
        String email = textFieldEmail.getText().trim();
        String username = textFieldUsername.getText().trim();
        String password = textFieldPassword.getText().trim();
        String centroMonitoraggio = (String) comboBoxCentromonitoraggio.getSelectedItem();

        // Validazione dei campi obbligatori
        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe l'esecuzione se i campi sono vuoti
        }

        // Controlla se l'email è valida
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Inserire un'email valida!", "Errore", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe l'esecuzione se l'email non è valida
        }

        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Recupera il registro RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub remoto del servizio

            // Invia i dati al server per registrare il nuovo operatore
            boolean success = stub.registerOperator(nome, cognome, codiceFiscale, email, username, password, centroMonitoraggio);

            if (success) {
                JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE); // Messaggio di conferma
                SwingUtilities.getWindowAncestor(jpanel1).dispose(); // Chiude la finestra della GUI
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante la registrazione. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
        }
    }

    /**
     * Restituisce il pannello principale della GUI.
     * Questo metodo è utilizzato per integrare la GUI della classe in altre finestre o contesti.
     *
     * @return Pannello principale della GUI.
     */
    public JPanel getPanel() {
        return jpanel1; // Ritorna il pannello principale della GUI
    }
}