package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class Meteo {
    private JLabel nomeareageografica; // Nome dell'area geografica selezionata
    private JTable tableAll;          // Tabella per tutti i parametri climatici
    private JTable mediatabella;      // Tabella per le medie dei parametri climatici
    private JTable modatabella;       // Tabella per le mode dei parametri climatici
    private JTable medianatabella;    // Tabella per le mediane dei parametri climatici
    private JPanel jpanel1;        // Pannello principale

    public Meteo(String areaName) {
        nomeareageografica.setText(areaName); // Imposta il nome dell'area selezionata
        populateClimaticDataTable(areaName);  // Popola la tabella con i dati climatici
        populateAverageTable(areaName);      // Popola la tabella con le medie
        populateModeTable(areaName);         // Popola la tabella con le mode
        populateMedianTable(areaName);       // Popola la tabella con le mediane
    }

    private void populateClimaticDataTable(String areaName) {
        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Recupera i dati climatici per l'area selezionata
            List<Map<String, String>> climaticData = stub.getClimaticData(areaName);

            // Configura le intestazioni
            String[] columnNames = {
                    "Data Rilevazione", "Ora", "Vento", "Umidità", "Pressione",
                    "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"
            };

            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Aggiungi i dati climatici
            for (Map<String, String> row : climaticData) {
                tableModel.addRow(new Object[]{
                        row.get("data_di_rilevazione"),
                        row.get("ora"),
                        row.get("vento"),
                        row.get("umidita"),
                        row.get("pressione"),
                        row.get("temperatura"),
                        row.get("precipitazioni"),
                        row.get("altitudineghiacciai"),
                        row.get("massaghiacciai")
                });
            }

            // Imposta il modello nella tabella
            tableAll.setModel(tableModel);
            tableAll.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dei dati climatici: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateAverageTable(String areaName) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            Map<String, Double> averages = stub.getAverages(areaName);

            // Configura il modello per visualizzare i dati in riga
            String[] columnNames = averages.keySet().toArray(new String[0]); // Parametri come intestazioni di colonna
            Object[] rowData = averages.values().toArray(); // Valori come singola riga

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames);

            mediatabella.setModel(tableModel);
            mediatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle medie: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateModeTable(String areaName) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            Map<String, Integer> modes = stub.getModes(areaName);

            // Configura il modello per visualizzare i dati in riga
            String[] columnNames = modes.keySet().toArray(new String[0]); // Parametri come intestazioni di colonna
            Object[] rowData = modes.values().toArray(); // Valori come singola riga

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames);

            modatabella.setModel(tableModel);
            modatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle mode: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateMedianTable(String areaName) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            Map<String, Double> medians = stub.getMedians(areaName);

            // Configura il modello per visualizzare i dati in riga
            String[] columnNames = medians.keySet().toArray(new String[0]); // Parametri come intestazioni di colonna
            Object[] rowData = medians.values().toArray(); // Valori come singola riga

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames);

            medianatabella.setModel(tableModel);
            medianatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle mediane: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return jpanel1;
    }
}