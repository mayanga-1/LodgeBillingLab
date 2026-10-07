package org.example.lodgebillinglab;

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
        notesArea.setPrefRowCount(25);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
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

        VBox root = new VBox(12, title, form, buttons, status);
        root.setPadding(new Insets(20));

        stage.setTitle("Lodge Billing Lab");
        stage.setScene(new Scene(root, 600, 560));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
