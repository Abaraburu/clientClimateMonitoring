package org.aglia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Classe per la gestione dell'aggiunta di nuovi centri di monitoraggio.
 * Consente agli operatori di selezionare le aree di interesse e di registrare un centro di monitoraggio associato.
 *
 * @author Agliati Lorenzo 753378
 */
public class RegisterCentroMonitoraggio {

    private JButton aggiungiButton; // Pulsante per aggiungere il centro di monitoraggio
    private JTextField textFieldNome; // Campo di input per il nome del centro
    private JTextField textFieldIndirizzo; // Campo di input per l'indirizzo del centro
    private JPanel mainPanel; // Pannello principale della GUI
    private JTable tableAree; // Tabella per visualizzare le aree disponibili
    private JScrollPane tableScrollPane; // Pannello di scorrimento per la tabella

    /**
     * Costruttore della classe RegisterCentroMonitoraggio.
     * Configura la GUI e popola la tabella delle aree di interesse.
     */
    public RegisterCentroMonitoraggio() {
        initializeTable(); // Inizializza la tabella con le aree disponibili

        // Listener per il pulsante di aggiunta
        aggiungiButton.addActionListener(e -> onAddButtonClicked());
    }

    /**
     * Inizializza la tabella con le aree di interesse disponibili.
     * Recupera i dati dal server RMI e li mostra nella tabella.
     */
    private void initializeTable() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al server RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub remoto del servizio

            List<Map<String, String>> areas = stub.getAllData(); // Recupera tutte le aree disponibili

            // Configura le colonne della tabella
            String[] columnNames = {"Seleziona", "ID", "Nome"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Boolean.class; // La prima colonna contiene checkbox
                    }
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Solo la colonna checkbox è modificabile
                }
            };

            // Popola la tabella con i dati delle aree
            for (Map<String, String> area : areas) {
                tableModel.addRow(new Object[] {
                        false, // Checkbox inizialmente deselezionata
                        area.get("id_luogo"), // ID dell'area
                        area.get("nome_ascii") // Nome ASCII dell'area
                });
            }

            tableAree = new JTable(tableModel); // Imposta il modello della tabella
            tableAree.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adatta automaticamente le colonne

            tableScrollPane.setViewportView(tableAree); // Aggiunge la tabella al pannello di scorrimento

        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore nel caricamento delle aree: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Mostra un messaggio di errore
        }
    }

    /**
     * Gestisce l'evento di clic sul pulsante "Aggiungi".
     * Valida i dati di input e invia al server RMI i dettagli del nuovo centro di monitoraggio.
     */
    private void onAddButtonClicked() {
        try {
            // Recupera i valori dai campi di input
            String name = textFieldNome.getText().trim(); // Nome del centro
            String address = textFieldIndirizzo.getText().trim(); // Indirizzo del centro

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nome e indirizzo sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe l'esecuzione se i campi sono vuoti
            }

            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al server RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub remoto del servizio

            // Verifica duplicati
            if (stub.checkDuplicateMonitoringCenter(name, address)) {
                JOptionPane.showMessageDialog(null, "Esiste già un centro di monitoraggio con lo stesso nome o indirizzo.", "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe l'esecuzione se il centro è un duplicato
            }

            // Recupera le aree selezionate nella tabella
            DefaultTableModel model = (DefaultTableModel) tableAree.getModel();
            List<Integer> selectedAreaIds = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean isSelected = (Boolean) model.getValueAt(i, 0); // Controlla se la checkbox è selezionata
                if (Boolean.TRUE.equals(isSelected)) {
                    selectedAreaIds.add(Integer.parseInt(model.getValueAt(i, 1).toString())); // Aggiunge l'ID dell'area selezionata
                }
            }

            if (selectedAreaIds.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Seleziona almeno un'area di interesse.", "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe l'esecuzione se nessuna area è selezionata
            }

            // Invia i dati al server per registrare il centro di monitoraggio
            boolean success = stub.registerMonitoringCenter(name, address, selectedAreaIds);

            if (success) {
                JOptionPane.showMessageDialog(null, "Centro di monitoraggio registrato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE); // Messaggio di conferma
                SwingUtilities.getWindowAncestor(mainPanel).dispose(); // Chiude la finestra
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante la registrazione del centro. Riprova.", "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
        }
    }

    /**
     * Restituisce il pannello principale della GUI.
     * Questo metodo è utilizzato per integrare la GUI della classe in altre finestre o contesti.
     *
     * @return Pannello principale della GUI.
     */
    public JPanel getPanel() {
        return mainPanel; // Ritorna il pannello principale della GUI
    }
}