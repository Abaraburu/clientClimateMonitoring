package org.example;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Gestisce la registrazione dei parametri climatici tramite interfaccia grafica.
 * Questa classe fornisce una GUI per inserire dati relativi al clima, come temperatura,
 * umidità, pressione, ecc., associati a specifiche località e date.
 *
 * @author Agliati Lorenzo 753378
 */
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

    /**
     * Costruttore che inizializza la GUI, popola il comboBox delle località e inizializza i listener.
     */
    public RegisterParametriClimatici() {
        populateComboBox();
        initializePreviewListeners();

        aggiungiButton.addActionListener(e -> onAddButtonClicked());
    }

    /**
     * Popola il comboBox con le località disponibili per l'utente loggato.
     * La lista delle località viene recuperata da un servizio remoto.
     */
    private void populateComboBox() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            String username = SessionManager.getLoggedInUser();
            List<Map<String, String>> locations = stub.getLocationsForUser(username);

            // Controlla se ci sono aree disponibili
            if (locations.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Attenzione: l'operatore non ha nessun centro di monitoraggio o il centro di monitoraggio non controlla nessuna area di interesse.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe l'esecuzione se non ci sono aree
            }

            // Popola la comboBox con le aree disponibili
            for (Map<String, String> location : locations) {
                comboBoxLuogo.addItem(location.get("nome_ascii"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Errore nel caricamento delle aree di interesse: " + e.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inizializza i listener per la preview dei valori numerici inseriti nei campi di testo.
     */
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

    /**
     * Converte il testo di un JTextField in un intero.
     * @param textField il campo di testo da cui recuperare il numero.
     * @return il valore intero o -1 se il testo non è convertibile in intero.
     */
    private int getIntFromTextField(JTextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gestisce l'evento click del pulsante di aggiunta, validando i campi e inviando i dati al server.
     */
    private void onAddButtonClicked() {
        try {
            // Recupera i valori dai campi di testo
            String username = SessionManager.getLoggedInUser(); // Ottiene l'utente loggato
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

            // Recupera i valori dai campi di testo relativi ai parametri climatici
            String ventoText = textFieldVento.getText().trim();
            String umiditaText = textFieldUmidita.getText().trim();
            String pressioneText = textFieldPressione.getText().trim();
            String temperaturaText = textFieldTemperatura.getText().trim();
            String precipitazioniText = textFieldPrecipitazioni.getText().trim();
            String altitudineText = textFieldAltitudine.getText().trim();
            String massaText = textFieldMassa.getText().trim();

            // Validazione: verifica che i campi contengano solo numeri
            if (!ventoText.matches("\\d+") ||
                    !umiditaText.matches("\\d+") ||
                    !pressioneText.matches("\\d+") ||
                    !temperaturaText.matches("\\d+") ||
                    !precipitazioniText.matches("\\d+") ||
                    !altitudineText.matches("\\d+") ||
                    !massaText.matches("\\d+")) {
                JOptionPane.showMessageDialog(jpanel1,
                        "I campi relativi ai parametri climatici devono contenere solo numeri.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Conversione dei valori
            int vento = Integer.parseInt(ventoText);
            int umidita = Integer.parseInt(umiditaText);
            int pressione = Integer.parseInt(pressioneText);
            int temperatura = Integer.parseInt(temperaturaText);
            int precipitazioni = Integer.parseInt(precipitazioniText);
            int altitudine = Integer.parseInt(altitudineText);
            int massa = Integer.parseInt(massaText);

            // Connessione al server RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ClimateInterface stub = (ClimateInterface) registry.lookup("ClimateService");

            // Verifica se esiste già un parametro per questo luogo, data e ora
            boolean exists = stub.checkExistingClimaticParameter(nomeArea, data, ora);
            if (exists) {
                JOptionPane.showMessageDialog(jpanel1, "Esiste già un parametro climatico per questo luogo, data e ora!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Recupera i commenti (facoltativi)
            String commentoVento = textAreaVentoCommento.getText().trim();
            String commentoUmidita = textAreaUmiditaCommento.getText().trim();
            String commentoPressione = textAreaPressioneCommento.getText().trim();
            String commentoTemperatura = textAreaTemperaturaCommento.getText().trim();
            String commentoPrecipitazioni = textAreaPrecipitazioniCommento.getText().trim();
            String commentoAltitudine = textAreaAltitudine.getText().trim();
            String commentoMassa = textAreaMassa.getText().trim();

            // Invio dei dati al server
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Errore durante l'operazione: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Calcola il punteggio per il vento in base al valore fornito.
     * @param valore il valore del vento.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Calcola il punteggio per l'umidità in base al valore fornito.
     * @param valore il valore dell'umidità.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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
    /**
     * Calcola il punteggio per la pressione in base al valore fornito.
     * @param valore il valore della pressione.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Calcola il punteggio per la temperatura in base al valore fornito.
     * @param valore il valore della temperatura.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Calcola il punteggio per le precipitazioni in base al valore fornito.
     * @param valore il valore delle precipitazioni.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Calcola il punteggio per l'altitudine dei ghiacciai in base al valore fornito.
     * @param altitudineGhiacciai il valore dell'altitudine dei ghiacciai.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Calcola il punteggio per la massa dei ghiacciai in base al valore fornito.
     * @param massaGhiacciai il valore della massa dei ghiacciai.
     * @return il punteggio da 1 (peggiore) a 5 (ottimale).
     */
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

    /**
     * Restituisce il pannello principale della GUI.
     * @return il pannello principale.
     */
    public JPanel getPanel() {
        return jpanel1;
    }
}