package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // Importazione necessaria

public class RegisterCentroMonitoraggio {
    private JButton aggiungiButton;
    private JTextField textFieldNome;
    private JTextField textFieldIndirizzo;
    private JPanel mainPanel;
    private JTable tableAree;
    private JScrollPane tableScrollPane; // Deve essere definito nel .form

    public RegisterCentroMonitoraggio() {
        initializeTable();
        aggiungiButton.addActionListener(e -> onAddButtonClicked());
    }

    private void initializeTable() {
        try {
            System.out.println("Connessione al server RMI...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            System.out.println("Recupero delle aree di monitoraggio...");
            List<Map<String, String>> areas = stub.getAllData(); // Recupera tutte le aree senza filtri

            System.out.println("Dati ricevuti: " + areas.size() + " aree trovate.");

            // Configura i dati per la JTable
            String[] columnNames = {"Seleziona", "ID", "Nome"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return Boolean.class; // Colonna checkbox
                    }
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0; // Solo la colonna checkbox è modificabile
                }
            };

            for (Map<String, String> area : areas) {
                tableModel.addRow(new Object[]{
                        false, // Valore iniziale della checkbox
                        area.get("id_luogo"),
                        area.get("nome_ascii")
                });
            }

            // Configura la JTable
            tableAree = new JTable(tableModel);
            tableAree.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // Configura la colonna checkbox
            tableScrollPane.setViewportView(tableAree);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore nel caricamento delle aree: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddButtonClicked() {
        try {
            String name = textFieldNome.getText().trim();
            String address = textFieldIndirizzo.getText().trim();

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nome e indirizzo sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Verifica duplicati
            if (stub.checkDuplicateMonitoringCenter(name, address)) {
                JOptionPane.showMessageDialog(null, "Esiste già un centro di monitoraggio con lo stesso nome o indirizzo.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Procedi con l'aggiunta del centro di monitoraggio
            DefaultTableModel model = (DefaultTableModel) tableAree.getModel();
            List<Integer> selectedAreaIds = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean isSelected = (Boolean) model.getValueAt(i, 0);
                if (Boolean.TRUE.equals(isSelected)) {
                    selectedAreaIds.add(Integer.parseInt(model.getValueAt(i, 1).toString()));
                }
            }

            if (selectedAreaIds.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Seleziona almeno un'area di interesse.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = stub.registerMonitoringCenter(name, address, selectedAreaIds);

            if (success) {
                JOptionPane.showMessageDialog(null, "Centro di monitoraggio registrato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(mainPanel).dispose(); // Chiude la finestra
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}