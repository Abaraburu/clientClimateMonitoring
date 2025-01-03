package org.example;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class Home {
    private JPanel panel1;
    private JButton login;
    private JTextField textField1;
    private JButton search;
    private JTable table1;
    private JButton register;
    private JButton addArea;
    private JButton addMoni;
    private JButton addPara;
    private JScrollPane scrollPane;
    private JSlider slider1;
    private JLabel raggio;

    private boolean isLoggedIn = false;

    public Home() {
        // Inizializzazione della GUI
        initializeTable();

        // Raggio coordinate
        initializeSlider();

        // Impostazione dei pulsanti nascosti
        //hideOperatorButtons(); RIMOSSO MOMENTANEAMENTE PER VELOCIZZARE IL PROCESSO DI TEST E DEVELOPING DELL APPLICAZIONE, DA RIAGGIUNGERE IN VERSIONE FINALE

        // Configurazione pulsante di ricerca
        search.addActionListener(e -> cercaAreaGeografica());

        // Configurazione del comportamento per il tasto Invio nella textField1
        textField1.addActionListener(e -> cercaAreaGeografica());

        // Configurazione del pulsante login/logout
        login.addActionListener(e -> {
            if (isLoggedIn) {
                performLogout();
            } else {
                performLogin();
            }
        });

        register.addActionListener(e -> openRegisterOperatoreForm());

        addMoni.addActionListener(e -> openRegisterMonitoraggioForm());
    }

    private void performLogin() {
        JFrame loginFrame = new JFrame("Login Operatore");
        loginFrame.setContentPane(new Login(this).getPanel());
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.pack();
        loginFrame.setVisible(true);
        loginFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    private void openRegisterOperatoreForm() {
        JFrame registerFrame = new JFrame("Registrazione Operatore");
        registerFrame.setContentPane(new RegisterOperatore().getPanel());
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.pack();
        registerFrame.setVisible(true);
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    private void openRegisterMonitoraggioForm() {
        JFrame registerFrame = new JFrame("Aggiungi Centro di Monitoraggio");
        try {
            RegisterCentroMonitoraggio registerCentro = new RegisterCentroMonitoraggio();
            registerFrame.setContentPane(registerCentro.getPanel());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'apertura della finestra: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.pack();
        registerFrame.setVisible(true);
        registerFrame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
    }

    public void onLoginSuccess() {
        isLoggedIn = true;
        login.setText("Logout"); // Cambia il testo del pulsante
        showOperatorButtons(); // Mostra i pulsanti per gli operatori
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler effettuare il logout?", "Conferma Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            login.setText("Login Operatore"); // Ripristina il testo del pulsante
            hideOperatorButtons(); // Nasconde i pulsanti per gli operatori
            JOptionPane.showMessageDialog(null, "Logout effettuato con successo.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void hideOperatorButtons() {
        register.setVisible(false);
        addArea.setVisible(false);
        addMoni.setVisible(false);
        addPara.setVisible(false);
    }

    public void showOperatorButtons() {
        register.setVisible(true);
        addArea.setVisible(true);
        addMoni.setVisible(true);
        addPara.setVisible(true);
    }

    private void initializeTable() {
        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Recupero dati minimali dal server
            List<Map<String, String>> data = stub.getMinimalLocationData();

            // Configurazione del modello della tabella
            String[] columnNames = {"Nome ASCII", "ID Luogo"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Map<String, String> row : data) {
                tableModel.addRow(new Object[]{
                        row.get("nome_ascii"),
                        row.get("id_luogo")
                });
            }

            // Impostazione della tabella GUI
            table1.setModel(tableModel);
            table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // Aggiunta della tabella a uno JScrollPane
            scrollPane.setViewportView(table1);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dei dati: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeSlider() {
        slider1.addChangeListener(e -> {
            int value = slider1.getValue();
            String formattedValue = String.format("%02d", value);
            raggio.setText("Raggio, da 0km a 50km: " + formattedValue + " km");
        });
    }

    private void cercaAreaGeografica() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            String input = textField1.getText().trim();
            List<Map<String, String>> results;

            if (input.isEmpty()) {
                // Recupera tutti i dati ordinati alfabeticamente
                results = stub.getMinimalLocationData();
            } else if (input.contains(",")) {
                String[] coords = input.split(",");
                double latitude = Double.parseDouble(coords[0].trim());
                double longitude = Double.parseDouble(coords[1].trim());
                double radius = slider1.getValue();
                results = stub.searchByCoordinates(latitude, longitude, radius);
            } else {
                results = stub.searchByName(input);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nessun risultato trovato.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Nome ASCII", "ID Luogo", "Distanza (km)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Map<String, String> row : results) {
                tableModel.addRow(new Object[]{
                        row.get("nome_ascii"),
                        row.get("id_luogo"),
                        row.get("distance") != null ? row.get("distance") : ""
                });
            }
            table1.setModel(tableModel);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nella ricerca: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            // Imposta il tema FlatDarkLaf
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore durante l'impostazione del tema: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Home");
            Home home = new Home();
            frame.setContentPane(home.panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
            frame.setVisible(true);
        });
    }
}