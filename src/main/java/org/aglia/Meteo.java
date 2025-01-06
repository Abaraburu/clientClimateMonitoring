package org.aglia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

/**
 * Classe per gestire l'interfaccia di visualizzazione dei dati climatici.
 * Include la visualizzazione delle tabelle con parametri climatici, medie, mode e mediane.
 *
 * @author Agliati Lorenzo 753378
 */
public class Meteo {
    private JLabel nomeareageografica; // Nome dell'area geografica selezionata
    private JTable tableAll;          // Tabella per tutti i parametri climatici
    private JTable mediatabella;      // Tabella per le medie dei parametri climatici
    private JTable modatabella;       // Tabella per le mode dei parametri climatici
    private JTable medianatabella;    // Tabella per le mediane dei parametri climatici
    private JPanel jpanel1;           // Pannello principale della GUI

    /**
     * Costruttore della classe Meteo.
     * @param areaName Nome dell'area geografica.
     * @param areaId ID dell'area geografica.
     */
    public Meteo(String areaName, String areaId) {
        nomeareageografica.setText(areaName); // Mostra il nome dell'area selezionata
        populateClimaticDataTable(areaId);  // Popola la tabella con i parametri climatici
        populateAverageTable(areaId);      // Popola la tabella con le medie
        populateModeTable(areaId);         // Popola la tabella con le mode
        populateMedianTable(areaId);       // Popola la tabella con le mediane
        addRowClickListener(areaId);       // Aggiunge il listener per i click sulle righe
    }

    /**
     * Popola la tabella con i parametri climatici per un'area specifica.
     * @param areaId ID dell'area geografica.
     */
    private void populateClimaticDataTable(String areaId) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al servizio RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub

            // Recupera i dati climatici per l'area
            List<Map<String, String>> climaticData = stub.getClimaticDataById(Integer.parseInt(areaId));

            String[] columnNames = {
                    "ID Parametro", "Data Rilevazione", "Ora", "Vento", "Umidità", "Pressione",
                    "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"
            };

            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Impedisce modifiche ai dati
                }
            };

            for (Map<String, String> row : climaticData) {
                System.out.println("DEBUG: Popolamento riga con ID Parametro: " + row.get("id_parametro"));
                tableModel.addRow(new Object[]{
                        row.get("id_parametro"), // ID Parametro
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

            tableAll.setModel(tableModel); // Imposta il modello della tabella
            tableAll.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adatta le colonne automaticamente

            // Nascondi la colonna ID Parametro per l'utente
            tableAll.getColumnModel().getColumn(0).setMinWidth(0);
            tableAll.getColumnModel().getColumn(0).setMaxWidth(0);
            tableAll.getColumnModel().getColumn(0).setWidth(0);

        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore nel caricamento dei dati climatici: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aggiunge un listener per i click sulle righe della tabella.
     * @param areaId ID dell'area geografica.
     */
    private void addRowClickListener(String areaId) {
        tableAll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Singolo clic
                    int selectedRow = tableAll.getSelectedRow();
                    int selectedColumn = tableAll.getSelectedColumn();

                    if (selectedRow != -1 && selectedColumn > 1) { // Escludi colonne non pertinenti
                        try {
                            Object idParametroObj = tableAll.getValueAt(selectedRow, 0); // Ottieni ID Parametro
                            if (idParametroObj != null) {
                                int idParametro = Integer.parseInt(idParametroObj.toString());

                                // Ottieni il nome della colonna e applica il mapping corretto
                                String parameterName = tableAll.getColumnName(selectedColumn).toLowerCase();
                                String parameterNoteColumn = switch (parameterName) {
                                    case "umidità" -> "umidita_nota";
                                    case "altitudine ghiacciai" -> "altitudineghiacciai_nota";
                                    case "massa ghiacciai" -> "massaghiacciai_nota";
                                    default -> parameterName + "_nota";
                                };

                                Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al servizio RMI
                                ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub

                                String comment = stub.getCommentForParameterById(idParametro, parameterNoteColumn);

                                if (comment == null || comment.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Nessun commento per questo parametro.", "Info", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, comment, "Commento", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Nessun ID parametro selezionato.", "Errore", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Stampa l'errore per il debug
                            JOptionPane.showMessageDialog(null, "Errore nel recupero del commento: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    /**
     * Popola la tabella con le medie dei parametri climatici per un'area specifica.
     * @param areaId ID dell'area geografica.
     */
    private void populateAverageTable(String areaId) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al servizio RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub

            Map<String, Double> averages = stub.getAveragesById(Integer.parseInt(areaId)); // Medie ricevute dal server
            System.out.println("DEBUG: Medie ricevute dal server = " + averages);

            // Ordine specificato delle colonne
            String[] columnNames = {"Vento", "Umidità", "Pressione", "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"};
            Object[] rowData = {
                    averages.get("vento"),
                    averages.get("umidita"),
                    averages.get("pressione"),
                    averages.get("temperatura"),
                    averages.get("precipitazioni"),
                    averages.get("altitudineghiacciai"),
                    averages.get("massaghiacciai")
            };

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Impedisce modifiche ai dati
                }
            };

            mediatabella.setModel(tableModel); // Imposta il modello della tabella
            mediatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adatta automaticamente le colonne

        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle medie: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Popola la tabella con le mode dei parametri climatici per un'area specifica.
     * @param areaId ID dell'area geografica.
     */
    private void populateModeTable(String areaId) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al servizio RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub

            Map<String, Integer> modes = stub.getModesById(Integer.parseInt(areaId)); // Mode ricevute dal server
            System.out.println("DEBUG: Modes ricevute dal server = " + modes);

            // Ordine specificato delle colonne
            String[] columnNames = {"Vento", "Umidità", "Pressione", "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"};
            Object[] rowData = {
                    modes.get("vento"),
                    modes.get("umidita"),
                    modes.get("pressione"),
                    modes.get("temperatura"),
                    modes.get("precipitazioni"),
                    modes.get("altitudineghiacciai"),
                    modes.get("massaghiacciai")
            };

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Impedisce modifiche ai dati
                }
            };

            modatabella.setModel(tableModel); // Imposta il modello della tabella
            modatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adatta automaticamente le colonne

        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle mode: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Popola la tabella con le mediane dei parametri climatici per un'area specifica.
     * @param areaId ID dell'area geografica.
     */
    private void populateMedianTable(String areaId) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Connessione al servizio RMI
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService"); // Ottiene lo stub

            Map<String, Double> medians = stub.getMediansById(Integer.parseInt(areaId)); // Mediane ricevute dal server
            System.out.println("DEBUG: Medians ricevute dal server = " + medians);

            // Ordine specificato delle colonne
            String[] columnNames = {"Vento", "Umidità", "Pressione", "Temperatura", "Precipitazioni", "Altitudine Ghiacciai", "Massa Ghiacciai"};
            Object[] rowData = {
                    medians.get("vento"),
                    medians.get("umidita"),
                    medians.get("pressione"),
                    medians.get("temperatura"),
                    medians.get("precipitazioni"),
                    medians.get("altitudineghiacciai"),
                    medians.get("massaghiacciai")
            };

            DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{rowData}, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Impedisce modifiche ai dati
                }
            };

            medianatabella.setModel(tableModel); // Imposta il modello della tabella
            medianatabella.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Adatta automaticamente le colonne

        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore per il debug
            JOptionPane.showMessageDialog(null, "Errore nel calcolo delle mediane: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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