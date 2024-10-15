package dev.stan.gui;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class SimpleController {
    private static final Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @FXML private TextArea logTextArea;
    @FXML private TextArea statusTextArea;
    @FXML private TableView<String> sysParamsableView;
    @FXML private TableView<String> pinsTableView;
    @FXML private LineChart<String, Number> chart;

    @FXML
    private void initialize() {
        logTextArea.clear();
    }

    @FXML
    private void onClick(MouseEvent event) {
        logger.info("Button clicked", event);
    }

    public void appendLogMessage(String message) {
        Platform.runLater(() -> {
            logTextArea.appendText(message + "\n");
            limitLines(logTextArea, 50);
        });
    }

    private void limitLines(TextArea textArea, int maxLines) {
        int lines = textArea.getParagraphs().size();
        if (lines > maxLines) {
            int excessLines = lines - maxLines;
            String text = textArea.getText();
            int pos = 0;
            for (int i = 0; i < excessLines; i++) {
                pos = text.indexOf('\n', pos) + 1;
            }
            textArea.replaceText(0, pos, "");
            textArea.positionCaret(textArea.getLength());
        }
    }

}