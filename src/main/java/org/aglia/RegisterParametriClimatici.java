package org.aglia;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterParametriClimatici {
    private JComboBox<String> comboBoxLuogo;
    private JTextField textFieldData;
    private JTextArea textAreaVentoCommento;
    private JTextArea textAreaUmiditaCommento;
    private JTextArea textAreaPressioneCommento;
    private JTextArea textAreaTemperaturaCommento;
    private JTextArea textAreaPrecipitazioniCommento;
    private JButton aggiungiButton;
    private JPanel jpanel1;
    private JTextField textFieldVento;
    private JTextField textFieldUmidita;
    private JTextField textFieldPressione;
    private JTextField textFieldTemperatura;
    private JTextField textFieldPrecipitazioni;
    private JTextField textFieldOra;
    private JTextField textFieldAltitudine;
    private JTextField textFieldMassa;
    private JTextArea textAreaAltitudine;
    private JTextArea textAreaMassa;
    private JLabel ventoNum;
    private JLabel umiditaNum;
    private JLabel pressioneNum;
    private JLabel temperaturaNum;
    private JLabel precipitazioniNum;
    private JLabel altitudineNum;
    private JLabel massaNum;

    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    private static final String TIME_PATTERN = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

    public RegisterParametriClimatici() {
        populateComboBox();
        initializePreviewListeners();

        aggiungiButton.addActionListener(e -> onAddButtonClicked());
    }

    private void populateComboBox() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            String username = SessionManager.getLoggedInUser();
            List<Map<String, String>> locations = stub.getLocationsForUser(username);

            for (Map<String, String> location : locations) {
                comboBoxLuogo.addItem(location.get("nome_ascii"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore nel caricamento delle aree di interesse: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializePreviewListeners() {
        textFieldVento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumVento(getIntFromTextField(textFieldVento));
                ventoNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldUmidita.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumUmidita(getIntFromTextField(textFieldUmidita));
                umiditaNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldPressione.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumPressione(getIntFromTextField(textFieldPressione));
                pressioneNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldTemperatura.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumTemperatura(getIntFromTextField(textFieldTemperatura));
                temperaturaNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldPrecipitazioni.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumPrecipitazioni(getIntFromTextField(textFieldPrecipitazioni));
                precipitazioniNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldAltitudine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumAltitudineGhiacciai(getIntFromTextField(textFieldAltitudine));
                altitudineNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });

        textFieldMassa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int value = calcolaNumMassaGhiacciai(getIntFromTextField(textFieldMassa));
                massaNum.setText(value == -1 ? "N/A" : String.valueOf(value));
            }
        });
    }

    private int getIntFromTextField(JTextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void onAddButtonClicked() {
        try {
            // Recupera i valori dai campi
            String username = SessionManager.getLoggedInUser(); // Ottieni l'username
            String nomeArea = (String) comboBoxLuogo.getSelectedItem();
            String data = textFieldData.getText().trim();
            String ora = textFieldOra.getText().trim();

            // Validazione dei campi obbligatori
            if (username == null || username.isEmpty()) {
                JOptionPane.showMessageDialog(jpanel1, "Utente non autenticato!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nomeArea == null || nomeArea.isEmpty()) {
                JOptionPane.showMessageDialog(jpanel1, "Selezionare un'area.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (data.isEmpty() || !Pattern.matches(DATE_PATTERN, data)) {
                JOptionPane.showMessageDialog(jpanel1, "Inserire una data valida nel formato dd/mm/aaaa.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ora.isEmpty() || !Pattern.matches(TIME_PATTERN, ora)) {
                JOptionPane.showMessageDialog(jpanel1, "Inserire un'ora valida nel formato hh:mm.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica che tutti i campi numerici obbligatori siano compilati
            if (textFieldVento.getText().trim().isEmpty() ||
                    textFieldUmidita.getText().trim().isEmpty() ||
                    textFieldPressione.getText().trim().isEmpty() ||
                    textFieldTemperatura.getText().trim().isEmpty() ||
                    textFieldPrecipitazioni.getText().trim().isEmpty() ||
                    textFieldAltitudine.getText().trim().isEmpty() ||
                    textFieldMassa.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(jpanel1, "Tutti i campi relativi ai parametri climatici devono essere compilati!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Verifica se esiste già un parametro per questo luogo, data e ora
            boolean exists = stub.checkExistingClimaticParameter(nomeArea, data, ora);
            if (exists) {
                JOptionPane.showMessageDialog(jpanel1, "Esiste già un parametro climatico per questo luogo, data e ora!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Conversione dei valori
            int vento = calcolaNumVento(getIntFromTextField(textFieldVento));
            int umidita = calcolaNumUmidita(getIntFromTextField(textFieldUmidita));
            int pressione = calcolaNumPressione(getIntFromTextField(textFieldPressione));
            int temperatura = calcolaNumTemperatura(getIntFromTextField(textFieldTemperatura));
            int precipitazioni = calcolaNumPrecipitazioni(getIntFromTextField(textFieldPrecipitazioni));
            int altitudine = calcolaNumAltitudineGhiacciai(getIntFromTextField(textFieldAltitudine));
            int massa = calcolaNumMassaGhiacciai(getIntFromTextField(textFieldMassa));

            // Recupera i commenti (facoltativi)
            String commentoVento = textAreaVentoCommento.getText().trim();
            String commentoUmidita = textAreaUmiditaCommento.getText().trim();
            String commentoPressione = textAreaPressioneCommento.getText().trim();
            String commentoTemperatura = textAreaTemperaturaCommento.getText().trim();
            String commentoPrecipitazioni = textAreaPrecipitazioniCommento.getText().trim();
            String commentoAltitudine = textAreaAltitudine.getText().trim();
            String commentoMassa = textAreaMassa.getText().trim();

            // Invio al server
            boolean success = stub.addClimaticParameters(
                    username, nomeArea, data, ora,
                    vento, umidita, pressione, temperatura,
                    precipitazioni, altitudine, massa,
                    commentoVento.isEmpty() ? null : commentoVento,
                    commentoUmidita.isEmpty() ? null : commentoUmidita,
                    commentoPressione.isEmpty() ? null : commentoPressione,
                    commentoTemperatura.isEmpty() ? null : commentoTemperatura,
                    commentoPrecipitazioni.isEmpty() ? null : commentoPrecipitazioni,
                    commentoAltitudine.isEmpty() ? null : commentoAltitudine,
                    commentoMassa.isEmpty() ? null : commentoMassa
            );

            if (success) {
                JOptionPane.showMessageDialog(null, "Parametri climatici aggiunti con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.getWindowAncestor(jpanel1).dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Errore durante l'aggiunta dei parametri.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(jpanel1, "Tutti i campi relativi ai parametri climatici devono contenere valori numerici validi.", "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Errore di comunicazione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private int calcolaNumVento(int valore) {
        if (valore >= 0 && valore <= 10) {
            return 5;
        } else if (valore <= 20) {
            return 4;
        } else if (valore <= 30) {
            return 3;
        } else if (valore <= 50) {
            return 2;
        } else {
            return 1;
        }
    }

    private int calcolaNumUmidita(int valore) {
        if (valore >= 0 && valore <= 10) {
            return 1;
        } else if (valore <= 20) {
            return 2;
        } else if (valore <= 25) {
            return 3;
        } else if (valore <= 30) {
            return 4;
        } else if (valore <= 40) {
            return 5;
        } else if (valore <= 60) {
            return 4;
        } else if (valore <= 70) {
            return 3;
        } else if (valore <= 90) {
            return 2;
        } else {
            return 1;
        }
    }

    //Come scritto sul pdf con le specifiche per il progetto "indicano l’intensità del fenomeno su una scala che va da 1 (critico) a 5 (ottimale)", ho usato 5 come la * migliore per l'essere umano e 1 la peggiore
    private int calcolaNumPressione(int valore) {
        if (valore >= 900 && valore <= 950) {
            return 1;
        } else if (valore <= 980) {
            return 2;
        } else if (valore <= 990) {
            return 3;
        } else if (valore <= 1000) {
            return 4;
        } else if (valore <= 1010) {
            return 5;
        } else if (valore <= 1020) {
            return 4;
        } else if (valore <= 1030) {
            return 3;
        } else if (valore <= 1050) {
            return 2;
        } else {
            return 1;
        }
    }

    private int calcolaNumTemperatura(int valore) {
        if (valore <= -30) {
            return 1;
        } else if (valore <= 5) {
            return 2;
        } else if (valore <= 18) {
            return 3;
        } else if (valore <= 20) {
            return 4;
        } else if (valore <= 22) {
            return 5;
        } else if (valore <= 25) {
            return 4;
        } else if (valore <= 30) {
            return 3;
        } else if (valore <= 37) {
            return 2;
        } else {
            return 1;
        }
    }

    private int calcolaNumPrecipitazioni(int valore) {
        if (valore >= 0 && valore <= 5) {
            return 5;
        } else if (valore <= 10) {
            return 4;
        } else if (valore <= 20) {
            return 3;
        } else if (valore <= 50) {
            return 2;
        } else {
            return 1;
        }
    }

    public static int calcolaNumAltitudineGhiacciai(int altitudineGhiacciai) {
        if (altitudineGhiacciai <= 500) {
            return 1;
        } else if (altitudineGhiacciai <= 1000) {
            return 2;
        } else if (altitudineGhiacciai <= 2000) {
            return 3;
        } else if (altitudineGhiacciai <= 3000) {
            return 4;
        } else {
            return 5;
        }
    }

    public static int calcolaNumMassaGhiacciai(int massaGhiacciai) {
        if (massaGhiacciai <= 100) {
            return 1;
        } else if (massaGhiacciai <= 500) {
            return 2;
        } else if (massaGhiacciai <= 1000) {
            return 3;
        } else if (massaGhiacciai <= 2000) {
            return 4;
        } else {
            return 5;
        }
    }

    public JPanel getPanel() {
        return jpanel1;
    }
}