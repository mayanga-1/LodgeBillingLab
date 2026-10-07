package org.example.lodgebillinglab;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LodgeBillingApp extends Application {


    private static double number(String text) {
        String t = text.trim();
        return t.isEmpty() ? 0 : Double.parseDouble(t);
    }

    private static String check(String guest, String room, String nightsText,
                                String extrasText, String paidText) {
        if (guest.trim().isEmpty()) {
            return "Enter the guest name.";
        }
        if (room == null) {
            return "Choose a room type.";
        }
        try {
            if (Integer.parseInt(nightsText.trim()) < 1) {
                return "Nights must be 1 or more.";
            }
        } catch (NumberFormatException e) {
            return "Nights must be a whole number.";
        }
        try {
            if (number(extrasText) < 0) {
                return "Extras cannot be negative.";
            }
        } catch (NumberFormatException e) {
            return "Extras must be a number.";
        }
        try {
            if (number(paidText) < 0) {
                return "Amount paid cannot be negative.";
            }
        } catch (NumberFormatException e) {
            return "Amount paid must be a number.";
        }
        return null;
    }
    @Override
    public void start(Stage stage) {
        Label title = new Label("Lodge Billing Lab");

        TextField guestField = new TextField();

        ComboBox<String> roomBox = new ComboBox<>(
                FXCollections.observableArrayList(BillingCalculator.ROOM_RATES.keySet()));
        roomBox.setPromptText("Choose a room type");

        TextField nightsField = new TextField();
        TextField extrasField = new TextField();
        TextField paidField = new TextField();

        ComboBox<String> currencyBox = new ComboBox<>(
                FXCollections.observableArrayList(BillingCalculator.CURRENCY_RATES.keySet()));
        currencyBox.setValue("ZMW");

        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPrefRowCount(25);
        notesArea.setPrefHeight(70);
        notesArea.setMaxHeight(70);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(Region.USE_PREF_SIZE);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelColumn, fieldColumn);
        form.addRow(0, new Label("Guest name"), guestField);
        form.addRow(1, new Label("Room type"), roomBox);
        form.addRow(2, new Label("Nights"), nightsField);
        form.addRow(3, new Label("Extras in K"), extrasField);
        form.addRow(4, new Label("Amount paid in K"), paidField);
        form.addRow(5, new Label("Pay in currency"), currencyBox);
        form.addRow(6, new Label("Notes"), notesArea);

        Button calculateButton = new Button("Calculate");
        Button saveButton = new Button("Save bill");
        HBox buttons = new HBox(10, calculateButton, saveButton);

        Label status = new Label();

        Label summary = new Label();

        Runnable update = () -> {
            String room = roomBox.getValue();
            String currency = currencyBox.getValue();
            if (room == null || currency == null) {
                summary.setText("");
                return;
            }
            try {
                int nights = Integer.parseInt(nightsField.getText().trim());
                if (nights < 1) {
                    summary.setText("");
                    return;
                }
                double extras = number(extrasField.getText());
                double paid = number(paidField.getText());

                double roomCharge = BillingCalculator.roomCharge(room, nights);
                double discount = BillingCalculator.discount(room, nights);
                double total = BillingCalculator.total(room, nights, extras);
                double balance = total - paid;

                summary.setText(String.format(
                        "Room charge: K%.2f%nDiscount: -K%.2f%nExtras: K%.2f%nTotal: K%.2f%nTotal in %s: %.2f%nPaid: K%.2f%nBalance: K%.2f",
                        roomCharge, discount, extras, total,
                        currency, BillingCalculator.convert(total, currency),
                        paid, balance));
            } catch (NumberFormatException e) {
                summary.setText("");
            }
        };

        nightsField.textProperty().addListener((obs, oldText, newText) -> update.run());
        extrasField.textProperty().addListener((obs, oldText, newText) -> update.run());
        paidField.textProperty().addListener((obs, oldText, newText) -> update.run());
        roomBox.valueProperty().addListener((obs, oldValue, newValue) -> update.run());
        currencyBox.valueProperty().addListener((obs, oldValue, newValue) -> update.run());
        calculateButton.setOnAction(e -> {
            String problem = check(guestField.getText(), roomBox.getValue(),
                    nightsField.getText(), extrasField.getText(), paidField.getText());
            if (problem != null) {
                status.setText(problem);
                return;
            }
            status.setText("");
            update.run();
        });

        VBox root = new VBox(12, title, form, buttons, summary, status);
        root.setPadding(new Insets(20));

        stage.setTitle("Lodge Billing Lab");
        stage.setScene(new Scene(root, 600, 640));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
