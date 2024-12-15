package org.example;

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

    public Home() {
        // Inizializzazione della GUI
        initializeTable();

        // Impostazione dei pulsanti nascosti
        register.setVisible(false);
        addArea.setVisible(false);
        addMoni.setVisible(false);
        addPara.setVisible(false);

        // Configurazione pulsante di ricerca
        search.addActionListener(e -> cercaAreaGeografica());

        // Configurazione del comportamento per il tasto Invio nella textField1
        textField1.addActionListener(e -> cercaAreaGeografica());

        // Configurazione del pulsante login
        login.addActionListener(e -> {
            JFrame loginFrame = new JFrame("Login Operatore");
            loginFrame.setContentPane(new Login().getPanel());
            loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            loginFrame.pack();
            loginFrame.setVisible(true);
        });
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

    private void cercaAreaGeografica() {
        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            String input = textField1.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Inserire un valore per la ricerca.", "Errore", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Map<String, String>> results;
            if (input.contains(",")) {
                // Ricerca per coordinate
                String[] coords = input.split(",");
                double latitude = Double.parseDouble(coords[0].trim());
                double longitude = Double.parseDouble(coords[1].trim());
                results = stub.searchByCoordinates(latitude, longitude);
            } else {
                // Ricerca per nome o altro criterio
                results = stub.searchByName(input);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nessun risultato trovato.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Aggiornamento della tabella con i risultati
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Nome ASCII", "ID Luogo"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (Map<String, String> row : results) {
                tableModel.addRow(new Object[]{
                        row.get("nome_ascii"),
                        row.get("id_luogo")
                });
            }
            table1.setModel(tableModel);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nella ricerca: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Creazione e visualizzazione della GUI principale
        JFrame frame = new JFrame("Home");
        Home home = new Home();
        frame.setContentPane(home.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}