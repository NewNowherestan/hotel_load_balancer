package dev.stan.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class StatusController {
    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @FXML private TextArea logTextArea;
    @FXML private TextArea statusTextArea;
    @FXML private ListView<String> sysParamsListView;
    @FXML private ListView<String> ioListView;
    @FXML private LineChart<String, Number> chart;

    @FXML
    private void initialize() {
        logTextArea.clear();
    }

    @FXML
    private void onClick(MouseEvent event) {
        logger.info("Button clicked", event);
    }

    public void updateIOView(List<String[]> io) {
        Platform.runLater(() -> {
            ioListView.getItems().clear();
            ObservableList<String> items = FXCollections.observableArrayList();

            io.sort((a, b) -> a[0].compareTo(b[0]));
            for (String[] ioItem : io) {
                items.add(String.join("\t", ioItem));
            }
            ioListView.setItems(items);

        });
    }

    public void updateSysParams(List<String[]> statusParams) {
        Platform.runLater(() -> {
            sysParamsListView.getItems().clear();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (String[] statusParam : statusParams) {
                items.add(statusParam[1] + "\t -\t" + statusParam[0]);
            }
            sysParamsListView.setItems(items);
        });
    }

    public void appendLogMessage(String message) {
        Platform.runLater(() -> {
            String lastLine = getLastLine(logTextArea);
            String messageWithoutTimestamp = strip(message);
            String lastLineWithoutTimestamp = strip(lastLine);

            if (lastLineWithoutTimestamp.equals(messageWithoutTimestamp)) {
                String updatedLine = incrementCount(lastLine);
                replaceLastLine(logTextArea, updatedLine);
            } else {
                logTextArea.appendText(message);
            }
            limitLines(logTextArea, 50);

            statusTextArea.clear();
            statusTextArea.setText(message);
        });
    }

    private String getLastLine(TextArea textArea) {
        int lastLineIndex = textArea.getParagraphs().size() - 2;
        if (lastLineIndex >= 0) {
            return textArea.getParagraphs().get(lastLineIndex).toString();
        }
        return "";
    }

    private String strip(String message) {
        return message.replaceFirst("^\\d{2}:\\d{2}:\\d{2}\\.\\d{3} \\[.*?\\] ", "")
                .replaceFirst(" \\(x\\d+\\)$", "")
                .trim();
    }

    private String incrementCount(String line) {
        if (line.endsWith(")")) {
            int startIndex = line.lastIndexOf("(x") + 2;
            int endIndex = line.lastIndexOf(")");
            int count = Integer.parseInt(line.substring(startIndex, endIndex));
            count++;
            return line.substring(0, startIndex - 2) + "(x" + count + ")";
        } else {
            return line + " (x2)";
        }
    }

    private void replaceLastLine(TextArea textArea, String newLine) {
        int lastLineIndex = textArea.getParagraphs().size() - 1;
        if (lastLineIndex >= 0) {
            String text = textArea.getText();
            int start = text.lastIndexOf('\n', text.length() - 2) + 1;
            textArea.replaceText(start, text.length(), newLine + "\n");
        }
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