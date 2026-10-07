module org.example.lodgebillinglab {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.lodgebillinglab to javafx.fxml, javafx.base;
    exports org.example.lodgebillinglab;
}