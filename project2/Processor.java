import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class Processor {
    private final List<String> chunks;
    private final int threadCount;
    private int totalWordCount;

    public Processor(String document, int threadCount) {
        this.threadCount = threadCount;
        this.chunks = splitDocument(document, threadCount);
        this.totalWordCount = 0;
    }

    public String getChunk(int index) {
        return chunks.get(index);
    }

    public int countWords(String text) {
        int wordCount = text.split("\\s+").length;
        synchronized (this) {
            totalWordCount += wordCount;
        }
        return wordCount;
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    private List<String> splitDocument(String document, int parts) {
        String[] words = document.split("\\s+");
        List<String> chunks = new ArrayList<>();
        int chunkSize = (int) Math.ceil((double) words.length / parts);
        
        for (int i = 0; i < parts; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int j = start; j < end; j++) {
                chunk.append(words[j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
        }
        return chunks;
    }
    public Map<String, Integer> calculateWordFrequency(String text) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            word = word.toLowerCase(); // Normalize to lowercase
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }
        return wordFrequency;
    }
}
