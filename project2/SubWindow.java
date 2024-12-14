import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SubWindow extends JFrame {
    private JTextArea outputArea;

    public SubWindow(int threadId) {
        setTitle("Thread " + threadId);
        setSize(400, 300);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Thread Output"));
        add(outputScroll, BorderLayout.CENTER);

        setVisible(true);
    }

    public void displayResult(String chunk, int wordCount, long startTime, long endTime, Map<String, Integer> wordFrequency) {
    StringBuilder freqOutput = new StringBuilder();
    wordFrequency.entrySet().stream()
            .limit(5) 
            .forEach(entry -> freqOutput.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));

    outputArea.setText(
            "Chunk:\n" + chunk +
            "\n\nWord Count: " + wordCount +
            "\nTop Words:\n" + freqOutput +
            "\nStart Time: " + startTime +
            "\nEnd Time: " + endTime +
            "\nDuration: " + (endTime - startTime) + " ms");
}

}
