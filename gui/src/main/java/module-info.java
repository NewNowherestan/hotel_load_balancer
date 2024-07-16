module dev.stan.gui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    exports dev.stan.gui;
    opens dev.stan.gui to javafx.fxml;
    
}
