package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

/**
 * Classe principale per l'applicazione "Climate Monitoring".
 * Fornisce l'interfaccia grafica e gestisce l'interazione dell'utente e degli operatori.
 *
 * @author Agliati Lorenzo 753378
 */
public class clientCM {
    private JPanel panel1; // Pannello principale della GUI
    private JButton login; // Pulsante per il login/logout
    private JTextField textField1; // Campo di input per la ricerca di aree geografiche
    private JButton search; // Pulsante per effettuare la ricerca
    private JTable table1; // Tabella per visualizzare i risultati delle ricerche
    private JButton register; // Pulsante per registrare un nuovo operatore
    private JButton addArea; // Pulsante per aggiungere un'area di interesse
    private JButton addMoni; // Pulsante per aggiungere un centro di monitoraggio
    private JButton addPara; // Pulsante per aggiungere parametri climatici
    private JScrollPane scrollPane; // ScrollPane per contenere la tabella
    private JSlider slider1; // Slider per selezionare il raggio di ricerca
    private JLabel raggio; // Etichetta per visualizzare il raggio selezionato

    private boolean isLoggedIn = false; // Indica se l'utente è autenticato

    /**
     * Costruttore della classe clientCM.
     * Inizializza la GUI e configura i listener degli eventi.
     */
    public clientCM() {
        initializeTable(); // Configura la tabella iniziale con i dati
        initializeSlider(); // Configura lo slider per il raggio di ricerca
        hideOperatorButtons(); // Nasconde i pulsanti riservati agli operatori

        // Listener per il pulsante di ricerca
        search.addActionListener(e -> cercaAreaGeografica()); // Effettua la ricerca geografica

        // Listener per il tasto Invio nel campo di ricerca
        textField1.addActionListener(e -> cercaAreaGeografica()); // Permette di avviare la ricerca premendo Invio

        // Listener per il pulsante login/logout
        login.addActionListener(e -> {
            if (isLoggedIn) {
                performLogout(); // Effettua il logout se l'utente è autenticato
            } else {
                performLogin(); // Mostra la finestra di login
            }
        });

        // Configura i pulsanti per aprire le finestre di registrazione o aggiunta
        register.addActionListener(e -> openRegisterOperatoreForm()); // Apre la finestra di registrazione per operatori
        addMoni.addActionListener(e -> openRegisterMonitoraggioForm()); // Apre la finestra per aggiungere un centro di monitoraggio
        addArea.addActionListener(e -> openRegisterAreaForm()); // Apre la finestra per aggiungere un'area di interesse
        addPara.addActionListener(e -> openRegisterParametriForm()); // Apre la finestra per aggiungere parametri climatici
    }

    /**
     * Esegue l'operazione di login mostrando la finestra di autenticazione.
     */
    private void performLogin() {
        JFrame loginFrame = new JFrame("Login Operatore"); // Crea il frame per il login
        loginFrame.setContentPane(new Login(this).getPanel()); // Imposta il contenuto del frame
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        loginFrame.pack(); // Adatta le dimensioni del frame
        loginFrame.setVisible(true); // Mostra il frame
        loginFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Mostra la finestra di registrazione per un nuovo operatore.
     */
    private void openRegisterOperatoreForm() {
        JFrame registerFrame = new JFrame("Registrazione Operatore"); // Frame per registrazione operatori
        registerFrame.setContentPane(new RegisterOperatore().getPanel()); // Imposta il contenuto del frame
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        registerFrame.pack(); // Adatta le dimensioni del frame
        registerFrame.setVisible(true); // Mostra il frame
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Mostra la finestra per aggiungere un nuovo centro di monitoraggio.
     */
    private void openRegisterMonitoraggioForm() {
        JFrame registerFrame = new JFrame("Aggiungi Centro di Monitoraggio"); // Frame per centri di monitoraggio
        registerFrame.setContentPane(new RegisterCentroMonitoraggio().getPanel()); // Imposta il contenuto del frame
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        registerFrame.pack(); // Adatta le dimensioni del frame
        registerFrame.setVisible(true); // Mostra il frame
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Mostra la finestra per aggiungere un'area di interesse.
     */
    private void openRegisterAreaForm() {
        JFrame registerFrame = new JFrame("Aggiungi Area di Interesse"); // Frame per aree di interesse
        registerFrame.setContentPane(new RegisterAreaInteresse().getPanel()); // Imposta il contenuto del frame
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        registerFrame.pack(); // Adatta le dimensioni del frame
        registerFrame.setVisible(true); // Mostra il frame
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Mostra la finestra per aggiungere parametri climatici.
     */
    private void openRegisterParametriForm() {
        JFrame registerFrame = new JFrame("Aggiungi Parametri Climatici"); // Frame per parametri climatici
        registerFrame.setContentPane(new RegisterParametriClimatici().getPanel()); // Imposta il contenuto del frame
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        registerFrame.pack(); // Adatta le dimensioni del frame
        registerFrame.setVisible(true); // Mostra il frame
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Azione da eseguire dopo un login corretto.
     * Mostra i pulsanti riservati agli operatori.
     */
    public void onLoginSuccess() {
        isLoggedIn = true; // Imposta lo stato come autenticato
        login.setText("Logout"); // Modifica il testo del pulsante
        showOperatorButtons(); // Mostra i pulsanti per operatori
    }

    /**
     * Esegue l'operazione di logout, reimpostando lo stato dell'applicazione.
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler effettuare il logout?", "Conferma Logout", JOptionPane.YES_NO_OPTION); // Mostra un popup di conferma
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.setLoggedInUser(null); // Reimposta l'utente autenticato
            isLoggedIn = false; // Aggiorna lo stato dell'autenticazione
            login.setText("Login Operatore"); // Modifica il testo del pulsante
            hideOperatorButtons(); // Nasconde i pulsanti per operatori
            JOptionPane.showMessageDialog(null, "Logout effettuato con successo.", "Logout", JOptionPane.INFORMATION_MESSAGE); // Messaggio di conferma
        }
    }

    /**
     * Nasconde i pulsanti riservati agli operatori.
     */
    public void hideOperatorButtons() {
        //register.setVisible(false); // Nasconde il pulsante di registrazione |Il bottone per registrare nuovi operatori è da ora sempre disponibile
        addArea.setVisible(false); // Nasconde il pulsante per aggiungere aree
        addMoni.setVisible(false); // Nasconde il pulsante per aggiungere centri
        addPara.setVisible(false); // Nasconde il pulsante per aggiungere parametri
    }

    /**
     * Mostra i pulsanti riservati agli operatori.
     */
    public void showOperatorButtons() {
        //register.setVisible(true); // Mostra il pulsante di registrazione |Il bottone per registrare nuovi operatori è da ora sempre disponibile
        addArea.setVisible(true); // Mostra il pulsante per aggiungere aree
        addMoni.setVisible(true); // Mostra il pulsante per aggiungere centri
        addPara.setVisible(true); // Mostra il pulsante per aggiungere parametri
    }

    /**
     * Inizializza la tabella per mostrare i risultati delle ricerche.
     */
    private void initializeTable() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Recupera il registro RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub del servizio
            List<Map<String, String>> data = stub.getMinimalLocationData(); // Recupera i dati minimali

            String[] columnNames = {"Nome ASCII", "ID Luogo"}; // Nomi delle colonne della tabella
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Rende la tabella non modificabile
                }
            };

            for (Map<String, String> row : data) {
                tableModel.addRow(new Object[]{row.get("nome_ascii"), row.get("id_luogo")}); // Aggiunge righe alla tabella
            }

            table1.setModel(tableModel); // Imposta il modello della tabella
            table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Configura la tabella per adattare le colonne

            table1.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) { // Controlla se si tratta di un doppio clic
                        int selectedRow = table1.getSelectedRow(); // Ottiene la riga selezionata
                        if (selectedRow != -1) {
                            String areaName = table1.getValueAt(selectedRow, 0).toString(); // Nome area
                            String areaId = table1.getValueAt(selectedRow, 1).toString(); // ID area
                            openMeteoForm(areaName, areaId); // Mostra i dettagli meteo dell'area selezionata
                        }
                    }
                }
            });

            scrollPane.setViewportView(table1); // Aggiunge la tabella al pannello di scorrimento
        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore nella console
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dei dati: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Mostra un popup di errore
        }
    }

    /**
     * Mostra la finestra con i dettagli meteo di un'area selezionata.
     *
     * @param areaName Nome dell'area.
     * @param areaId ID dell'area.
     */
    private void openMeteoForm(String areaName, String areaId) {
        JFrame meteoFrame = new JFrame("Meteo - " + areaName); // Frame per i dettagli meteo
        meteoFrame.setContentPane(new Meteo(areaName, areaId).getPanel()); // Imposta il contenuto del frame
        meteoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configura la chiusura del frame
        meteoFrame.pack(); // Adatta le dimensioni del frame
        meteoFrame.setVisible(true); // Mostra il frame
        meteoFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    /**
     * Configura lo slider per selezionare il raggio di ricerca.
     */
    private void initializeSlider() {
        slider1.addChangeListener(e -> {
            int value = slider1.getValue(); // Ottiene il valore corrente dello slider
            String formattedValue = String.format("%02d", value); // Formatta il valore con due cifre utilizzando String.format
            raggio.setText("Raggio: " + formattedValue + " km"); // Aggiorna l'etichetta con il valore del raggio
        });
    }

    /**
     * Effettua la ricerca di aree geografiche in base all'input fornito.
     */
    private void cercaAreaGeografica() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Recupera il registro RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub del servizio

            String input = textField1.getText().trim(); // Ottiene il testo inserito dall'utente
            List<Map<String, String>> results; // Lista per memorizzare i risultati della ricerca

            if (input.isEmpty()) {
                results = stub.getMinimalLocationData(); // Recupera tutti i dati se l'input è vuoto
            } else if (input.contains(",")) {
                // Divide l'input per virgola per ottenere le coordinate
                String[] coords = input.split(",");
                double latitude = Double.parseDouble(coords[0].trim()); // Estrae la latitudine
                double longitude = Double.parseDouble(coords[1].trim()); // Estrae la longitudine
                double radius = slider1.getValue(); // Ottiene il valore del raggio dallo slider
                results = stub.searchByCoordinates(latitude, longitude, radius); // Cerca per coordinate
            } else {
                results = stub.searchByName(input); // Cerca per nome dell'area geografica
            }

            // Verifica se sono stati trovati risultati
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nessun risultato trovato.", "Info", JOptionPane.INFORMATION_MESSAGE); // Messaggio di informazione
                return;
            }

            // Modello della tabella per visualizzare i risultati
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Nome ASCII", "ID Luogo", "Distanza (km)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Rende la tabella non modificabile
                }
            };

            // Aggiunge i risultati alla tabella
            for (Map<String, String> row : results) {
                tableModel.addRow(new Object[]{
                        row.get("nome_ascii"), // Nome ASCII dell'area
                        row.get("id_luogo"), // ID univoco dell'area
                        row.get("distance") != null ? row.get("distance") : "" // Distanza (se disponibile)
                });
            }
            table1.setModel(tableModel); // Imposta il modello per la tabella

        } catch (Exception e) {
            e.printStackTrace(); // Stampa lo stack trace per debug
            JOptionPane.showMessageDialog(null, "Errore nella ricerca: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Mostra un popup di errore
        }
    }

    /**
     * Metodo principale per avviare l'applicazione.
     * @param args Argomenti della riga di comando.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf()); // Imposta il tema grafico FlatDarkLaf
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace(); // Stampa lo stack trace per debug
            JOptionPane.showMessageDialog(null, "Errore durante l'impostazione del tema: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); // Messaggio di errore
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("clientCM"); // Crea il frame principale
            clientCM home = new clientCM(); // Istanzia la classe clientCM
            frame.setContentPane(home.panel1); // Imposta il pannello principale
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura l'azione di chiusura
            frame.pack(); // Adatta le dimensioni del frame al contenuto
            frame.setSize(1000, 600); // Imposta una dimensione fissa per la finestra
            frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
            frame.setVisible(true); // Mostra il frame
        });
    }
}