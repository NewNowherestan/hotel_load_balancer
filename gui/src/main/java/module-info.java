module dev.stan.gui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.slf4j;
    requires dev.stan.logging;


    exports dev.stan.gui;
    opens dev.stan.gui to javafx.fxml;
    
}
