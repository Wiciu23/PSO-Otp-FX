module com.witek {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires commons.math3;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    opens com.witek to javafx.fxml;
    exports com.witek;
    exports com.witek.controller;
    opens com.witek.controller to javafx.fxml;
    exports com.witek.model;
    opens com.witek.view to javafx.fxml;
}