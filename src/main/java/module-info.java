module tactical.blue {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    opens tactical.blue to javafx.fxml;
    exports tactical.blue;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.base;
}
