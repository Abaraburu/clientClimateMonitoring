package org.aglia;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe per la gestione dell'aggiunta di nuove aree di interesse.
 * Consente agli operatori di inserire i dettagli di una nuova area e inviarli al server.
 *
 * @author Agliati Lorenzo 753378
 */
public class RegisterAreaInteresse {

    private JTextField textFieldNome; // Campo di input per il nome dell'area
    private JTextField textFieldNomeASCII; // Campo di input per il nome ASCII dell'area
    private JTextField textFieldStato; // Campo di input per lo stato dell'area
    private JTextField textFieldStatoCodice; // Campo di input per il codice dello stato
    private JTextField textFieldLatitudine; // Campo di input per la latitudine
    private JTextField textFieldLongitudine; // Campo di input per la longitudine
    private JButton aggiungiButton; // Pulsante per aggiungere l'area
    private JPanel jpanel1; // Pannello principale della GUI

    /**
     * Costruttore della classe RegisterAreaInteresse.
     * Configura la GUI e i listener per la gestione dell'aggiunta delle aree.
     */
    public RegisterAreaInteresse() {
        // Configura il listener per il pulsante di aggiunta
        aggiungiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddButtonClicked(); // Gestisce l'evento di clic sul pulsante
            }
        });
    }

    /**
     * Metodo invocato quando si clicca sul pulsante "Aggiungi".
     * Valida i campi di input e invia i dati al server per registrare una nuova area di interesse.
     */
    private void onAddButtonClicked() {
        // Recupera i valori inseriti dall'utente
        String nome = textFieldNome.getText().trim(); // Nome dell'area
        String nomeASCII = textFieldNomeASCII.getText().trim(); // Nome ASCII dell'area
        String stato = textFieldStato.getText().trim(); // Stato dell'area
        String statoCodice = textFieldStatoCodice.getText().trim(); // Codice dello stato
        String latitudineStr = textFieldLatitudine.getText().trim(); // Valore della latitudine
        String longitudineStr = textFieldLongitudine.getText().trim(); // Valore della longitudine

        // Validazione dei campi obbligatori
        if (nome.isEmpty() || nomeASCII.isEmpty() || stato.isEmpty() || statoCodice.isEmpty() || latitudineStr.isEmpty() || longitudineStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe l'esecuzione se i campi sono vuoti
        }

        // Verifica che il codice dello stato sia valido
        if (statoCodice.length() > 2 || !statoCodice.matches("[A-Za-z]{1,2}")) {
            JOptionPane.showMessageDialog(null, "Il codice dello stato deve contenere al massimo due lettere!", "Errore", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe l'esecuzione se il codice dello stato non è valido
        }

        try {
            // Converte latitudine e longitudine in valori numerici
            double latitudine = Double.parseDouble(latitudineStr); // Conversione della latitudine
            double longitudine = Double.parseDouble(longitudineStr); // Conversione della longitudine

            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Recupera il registro RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub remoto del servizio

            // Invio dei dati al server per registrare l'area
            boolean success = stub.registerArea(nome, nomeASCII, stato, statoCodice, latitudine, longitudine);

            if (success) {
                JOptionPane.showMessageDialog(null, "Area di interesse aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE); // Messaggio di conferma
                SwingUtilities.getWindowAncestor(jpanel1).dispose(); // Chiude la finestra della GUI
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta dell'area. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Latitudine e Longitudine devono essere valori numerici!", "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore per valori non numerici
        } catch (Exception ex) {
            ex.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore di comunicazione con il server: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore per problemi di comunicazione
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