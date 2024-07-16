module dev.stan.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.pi4j;
    requires com.pi4j.plugin.mock;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;

    opens dev.stan to javafx.fxml;
    exports dev.stan;
}
