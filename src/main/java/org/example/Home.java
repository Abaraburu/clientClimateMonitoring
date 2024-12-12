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

    public Home() {
        // Inizializzazione della GUI
        initializeTable();

        // Impostazione dei pulsanti nascosti
        register.setVisible(false);
        addArea.setVisible(false);
        addMoni.setVisible(false);
        addPara.setVisible(false);
    }

    private void initializeTable() {
        try {
            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Recupero dati dal server
            List<Map<String, String>> data = stub.getAllData();

            // Configurazione del modello della tabella
            String[] columnNames = {"ID Luogo", "Latitudine", "Longitudine"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            for (Map<String, String> row : data) {
                tableModel.addRow(new Object[]{
                        row.get("id_luogo"),
                        row.get("latitudine"),
                        row.get("longitudine")
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

    public static void main(String[] args) {
        // Creazione e visualizzazione della GUI principale
        JFrame frame = new JFrame("Home");
        Home home = new Home();
        frame.setContentPane(home.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 600);
        frame.setVisible(true);
    }
}