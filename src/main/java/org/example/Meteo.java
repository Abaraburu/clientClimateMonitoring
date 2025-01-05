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
    private JPanel jpanel1;           // Pannello principale

    public Meteo(String areaName) {
        nomeareageografica.setText(areaName); // Imposta il nome dell'area selezionata
        populateClimaticDataTable(areaName);  // Popola la tabella con i dati climatici
        populateAverageTable(areaName);      // Popola la tabella con le medie
        populateModeTable(areaName);         // Popola la tabella con le mode
        populateMedianTable(areaName);       // Popola la tabella con le mediane
        addRowClickListener(areaName);       // Aggiunge il listener per i click sulle righe
    }

    private void populateClimaticDataTable(String areaName) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            List<Map<String, String>> climaticData = stub.getClimaticData(areaName);

            String[] columnNames = {
                    "Data Rilevazione", "Ora", "Vento", "Umidità", "Pressione",
                    "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"
            };

            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Impedisce modifiche ai dati
                }
            };

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

            tableAll.setModel(tableModel);
            tableAll.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dei dati climatici: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRowClickListener(String areaName) {
        tableAll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Singolo clic
                    int selectedRow = tableAll.getSelectedRow();
                    int selectedColumn = tableAll.getSelectedColumn();

                    if (selectedRow != -1 && selectedColumn > 1) { // Escludi colonne non pertinenti
                        String parameter = tableAll.getColumnName(selectedColumn).toLowerCase();
                        String noteColumn = parameter + "_nota";
                        try {
                            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");
                            String comment = stub.getCommentForParameter(areaName, noteColumn);

                            if (comment == null || comment.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Nessun commento", "Info", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, comment, "Commento - " + parameter, JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Errore nel recupero del commento: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void populateAverageTable(String areaName) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            Map<String, Double> averages = stub.getAverages(areaName);

            String[] columnNames = averages.keySet().toArray(new String[0]);
            Object[] rowData = averages.values().toArray();

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

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

            String[] columnNames = modes.keySet().toArray(new String[0]);
            Object[] rowData = modes.values().toArray();

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

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

            String[] columnNames = medians.keySet().toArray(new String[0]);
            Object[] rowData = medians.values().toArray();

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

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