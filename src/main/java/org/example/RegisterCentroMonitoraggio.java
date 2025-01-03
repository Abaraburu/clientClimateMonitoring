package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class RegisterCentroMonitoraggio {
    private JButton aggiungiButton;
    private JTextField textFieldNome;
    private JTextField textFieldIndirizzo;
    private JPanel mainPanel;
    private JTable tableAree;
    private JScrollPane tableScrollPane;

    public RegisterCentroMonitoraggio() {
        initializeTable();
    }

    private void initializeTable() {
        try {
            System.out.println("Connessione al server RMI...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            System.out.println("Recupero delle aree di monitoraggio...");
            List<Map<String, String>> areas = stub.getMonitoringAreas();

            System.out.println("Dati ricevuti: " + areas.size() + " aree trovate.");

            // Configura i dati per la JTable
            String[] columnNames = {"ID", "Nome"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            for (Map<String, String> area : areas) {
                tableModel.addRow(new Object[]{
                        area.get("id_luogo"),
                        area.get("nome_ascii")
                });
            }

            // Configura la JTable
            tableAree = new JTable(tableModel);
            tableAree.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tableAree.setEnabled(false); // Disabilita l'editing

            // Imposta la JTable nel JScrollPane definito nel .form
            tableScrollPane.setViewportView(tableAree);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento delle aree: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}