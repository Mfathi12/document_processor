import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainApp extends JFrame {
    private JTextArea inputArea;
    private JSpinner threadCountSpinner;
    private JButton processButton;
    private JButton uploadButton;
    private JTextArea resultArea;

    public MainApp() {
        setTitle("Multi-Threaded Document Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Input Area
        inputArea = new JTextArea();
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input Document"));
        add(inputScroll, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        threadCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        controlPanel.add(new JLabel("Number of Threads:"));
        controlPanel.add(threadCountSpinner);

        uploadButton = new JButton("Upload File");
        uploadButton.addActionListener(this::uploadFile);
        controlPanel.add(uploadButton);

        processButton = new JButton("Process");
        processButton.addActionListener(this::processDocument);
        controlPanel.add(processButton);

        add(controlPanel, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Result"));
        add(resultScroll, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void uploadFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = Files.readString(file.toPath());
                inputArea.setText(content);
                JOptionPane.showMessageDialog(this, "File loaded successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            }
        }
    }

    private void processDocument(ActionEvent event) {
        String document = inputArea.getText();
        if (document.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a document to process.");
            return;
        }

        int wordCount = document.split("\\s+").length;
        int threadCount = (int) threadCountSpinner.getValue();

        if (threadCount > wordCount) {
            JOptionPane.showMessageDialog(this, "Number of threads exceeds the number of words in the document.");
            return;
        }

        Processor processor = new Processor(document, threadCount);

        // Create threads and windows
        List<Thread> threads = new ArrayList<>();
        List<SubWindow> windows = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            SubWindow window = new SubWindow(i + 1);
            windows.add(window);
            int finalI = i;
            Thread thread = new Thread(() -> {
                long startTime = System.currentTimeMillis();
                String chunk = processor.getChunk(finalI);
                int wordCountInChunk = processor.countWords(chunk);
                long endTime = System.currentTimeMillis();
                Map<String, Integer> wordFrequency = processor.calculateWordFrequency(chunk);
                SwingUtilities.invokeLater(() -> window.displayResult(chunk, wordCountInChunk, startTime, endTime, wordFrequency));
            });
            threads.add(thread);
        }

        // Start threads
        threads.forEach(Thread::start);

        // Wait for threads to finish
        new Thread(() -> {
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            SwingUtilities.invokeLater(() -> resultArea.setText("Total Word Count: " + processor.getTotalWordCount()));
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
