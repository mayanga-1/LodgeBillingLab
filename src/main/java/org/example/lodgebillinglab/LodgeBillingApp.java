package org.example.lodgebillinglab;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LodgeBillingApp extends Application {

    private final ObservableList<Bill> bills = FXCollections.observableArrayList();
    private int nextBillNumber = 1;

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
    private static TableColumn<Bill, Number> moneyColumn(String title, String property) {
        TableColumn<Bill, Number> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("K%.2f", value.doubleValue()));
            }
        });
        return column;
    }
    @Override
    @SuppressWarnings("unchecked")
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
        notesArea.setPrefColumnCount(25);
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

        Label summary = new Label("Press Calculate to see the bill.");
        Label totals = new Label("");
        totals.setStyle("-fx-font-weight: bold;");

        summary.setMinWidth(240);
        summary.setMinHeight(Region.USE_PREF_SIZE);
        totals.setMinHeight(Region.USE_PREF_SIZE);

        HBox summaryBox = new HBox(60, summary, totals);

        Runnable update = () -> {
            String room = roomBox.getValue();
            String currency = currencyBox.getValue();
            if (room == null || currency == null) {
                summary.setText("");
                totals.setText("");
                return;
            }
            try {
                int nights = Integer.parseInt(nightsField.getText().trim());
                if (nights < 1) {
                    summary.setText("");
                    totals.setText("");
                    return;
                }
                double extras = number(extrasField.getText());
                double paid = number(paidField.getText());

                double roomCharge = BillingCalculator.roomCharge(room, nights);
                double discount = BillingCalculator.discount(room, nights);
                double total = BillingCalculator.total(room, nights, extras);
                double balance = total - paid;

                summary.setText(String.format(
                        "Room charge: K%.2f%nDiscount: -K%.2f%nExtras: K%.2f",
                        roomCharge, discount, extras));

                totals.setText(String.format(
                        "Total (%s): %.2f%nPaid: K%.2f%nBalance: K%.2f",
                        currency, BillingCalculator.convert(total, currency),
                        paid, balance));

            }catch (NumberFormatException e) {
                summary.setText("");
                totals.setText("");
            }
        };

       Runnable resetSummary = () -> { summary.setText("Press calculate to see the bill");
       totals.setText("");};
       nightsField.textProperty().addListener((obs, oldText, newText) -> resetSummary.run());
       extrasField.textProperty().addListener((obs, oldText, newText) -> resetSummary.run());
       paidField.textProperty().addListener((obs, oldText, newText) -> resetSummary.run());
       roomBox.valueProperty().addListener((obs, oldText, newText) -> resetSummary.run());
       currencyBox.valueProperty().addListener((obs, oldText, newText) -> resetSummary.run());
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
        Runnable clearForm = () -> {
            guestField.clear();
            nightsField.clear();
            extrasField.clear();
            paidField.clear();
            notesArea.clear();
            roomBox.setValue(null);
            currencyBox.setValue("ZMW");
            guestField.requestFocus();
        };

        Runnable newGuest = () -> {
            clearForm.run();
            status.setText("");
        };
        saveButton.setOnAction(e -> {
            String problem = check(guestField.getText(), roomBox.getValue(),
                    nightsField.getText(), extrasField.getText(), paidField.getText());
            if (problem != null) {
                status.setText(problem);
                return;
            }


            String room = roomBox.getValue();
            int nights = Integer.parseInt(nightsField.getText().trim());
            double total = BillingCalculator.total(room, nights, number(extrasField.getText()));
            double paid = number(paidField.getText());
            String id = String.format("B%03d", nextBillNumber++);

            bills.add(new Bill(id, guestField.getText().trim(), room, nights,
                    total, paid, currencyBox.getValue()));
            status.setText("Saved " + id + ". Balance: " + String.format("K%.2f", total - paid));

            clearForm.run();
        });

        TableView<Bill> table = new TableView<>(bills);

        TableColumn<Bill, String> idCol = new TableColumn<>("Bill");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Bill, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guest"));
        TableColumn<Bill, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        TableColumn<Bill, Number> nightsCol = new TableColumn<>("Nights");
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("nights"));
        TableColumn<Bill, String> currencyCol = new TableColumn<>("Pay in");
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));

        table.getColumns().addAll(idCol, guestCol, roomCol, nightsCol,
                moneyColumn("Total", "total"),
                moneyColumn("Paid", "paid"),
                moneyColumn("Balance", "balance"),
                currencyCol);
        table.setPrefHeight(180);

        Button deleteButton = new Button("Delete selected bill");
        deleteButton.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Select a bill to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm deletion");
            confirm.setHeaderText("Delete bill " + selected.getId() + " for " + selected.getGuest() + "?");
            ButtonType deleteType = new ButtonType("Delete");
            confirm.getButtonTypes().setAll(deleteType, ButtonType.CANCEL);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == deleteType) {
                bills.remove(selected);
                status.setText("Deleted " + selected.getId() + ".");
            }
        });

        Button newGuestButton = new Button("New guest");
        newGuestButton.setOnAction(e -> newGuest.run());

        Button receiptButton = new Button("View receipt");
        receiptButton.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Select a bill to view its receipt.");
                return;
            }

            double roomCharge = BillingCalculator.roomCharge(selected.getRoomType(), selected.getNights());
            double discount = BillingCalculator.discount(selected.getRoomType(), selected.getNights());
            double extras = selected.getTotal() - roomCharge + discount;
            String currency = selected.getCurrency();

            Label receiptText = new Label(String.format(
                    "LODGE BILLING RECEIPT%n%nBill: %s%nGuest: %s%nRoom: %s x %d nights%n%n"
                            + "Room charge: K%.2f%nDiscount: -K%.2f%nExtras: K%.2f%n"
                            + "Total (%s): %.2f%nPaid: K%.2f%nBalance: K%.2f%n%n"
                            + "Exchange rates are practice rates, not live rates.",
                    selected.getId(), selected.getGuest(), selected.getRoomType(), selected.getNights(),
                    roomCharge, discount, extras,
                    currency, BillingCalculator.convert(selected.getTotal(), currency),
                    selected.getPaid(), selected.getBalance()));

            Stage receiptStage = new Stage();
            Button printButton = new Button("Print");

            printButton.setOnAction(p -> {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null && job.showPrintDialog(receiptStage)) {
                    if (job.printPage(receiptText)) {
                        job.endJob();
                        status.setText("Receipt for " + selected.getId() + " sent to printer.");
                    }
                }
            });

            VBox receiptBox = new VBox(15, receiptText, printButton);
            receiptBox.setPadding(new Insets(25));
            receiptStage.setTitle("Receipt " + selected.getId());
            receiptStage.setScene(new Scene(receiptBox, 380, 420));
            receiptStage.show();
        });


        MenuItem newGuestItem = new MenuItem("New guest");
        newGuestItem.setOnAction(e -> newGuest.run());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        Menu fileMenu = new Menu("File", null, newGuestItem, exitItem);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> {
            Alert about = new Alert(Alert.AlertType.INFORMATION);
            about.setTitle("About");
            about.setHeaderText("Lodge Billing Lab");
            about.setContentText("ICT261 Advanced Java Programming\nMulungushi University");
            about.showAndWait();
        });
        Menu helpMenu = new Menu("Help", null, aboutItem);

        MenuBar menuBar = new MenuBar(fileMenu, helpMenu);
        ToolBar toolBar = new ToolBar(newGuestButton, receiptButton, deleteButton);

        VBox content = new VBox(12, title, form, buttons, summaryBox, status, table);
        content.setPadding(new Insets(20));

        BorderPane root = new BorderPane(content);
        root.setTop(new VBox(menuBar, toolBar));


        stage.setTitle("Lodge Billing Lab");
        stage.setScene(new Scene(root, 760, 770));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
