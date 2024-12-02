import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;

public class PlagiarismDetectorGUI extends JFrame {

    private static final double SIMILARITY_THRESHOLD = 70.0;
    private JTextField file1Field, file2Field;
    private JLabel resultLabel;
    private File file1, file2;

    public PlagiarismDetectorGUI() {
        // Set up the main frame with higher resolution and enhanced styling
        setTitle("Plagiarism Detector");
        setSize(600, 400);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame

        // Configure a layout with padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10)  // Padding between elements

        // File selection fields and buttons with enhanced styling
        file1Field = new JTextField(25);
        file2Field = new JTextField(25);
        JButton selectFile1Button = new JButton("Browse...");
        JButton selectFile2Button = new JButton("Browse...");
        JButton detectButton = new JButton("Check Similarity");
        resultLabel = new JLabel("Similarity: N/A", JLabel.CENTER);

        // Customize fonts and colors for a modern look
        Font font = new Font("Arial", Font.PLAIN, 16);
        file1Field.setFont(font);
        file2Field.setFont(font);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setForeground(Color.BLUE);
        selectFile1Button.setFont(font);
        selectFile2Button.setFont(font);
        detectButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Set background color for detect button to improve visual appeal
        detectButton.setBackground(new Color(72, 128, 240));
        detectButton.setForeground(Color.WHITE);

        // Add action listeners
        selectFile1Button.addActionListener(e -> file1 = chooseFile(file1Field));
        selectFile2Button.addActionListener(e -> file2 = chooseFile(file2Field));
        detectButton.addActionListener(e -> detectPlagiarism());

        // Add labels for better guidance
        JLabel file1Label = new JLabel("Select First File:");
        file1Label.setFont(font);
        JLabel file2Label = new JLabel("Select Second File:");
        file2Label.setFont(font);

        // Arrange components with GridBag layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(file1Label, gbc);
        
        gbc.gridx = 1;
        add(file1Field, gbc);

        gbc.gridx = 2;
        add(selectFile1Button, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(file2Label, gbc);
        
        gbc.gridx = 1;
        add(file2Field, gbc);

        gbc.gridx = 2;
        add(selectFile2Button, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(detectButton, gbc);

        gbc.gridy = 3;
        add(resultLabel, gbc);

        setVisible(true);
    }

    private File chooseFile(JTextField fileField) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
            return selectedFile;
        }
        return null;
    }

    private void detectPlagiarism() {
        if (file1 == null || file2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both files to compare.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double similarityPercentage = calculateSimilarityPercentage(file1, file2);
            resultLabel.setText(String.format("Similarity: %.2f%%", similarityPercentage));

            if (similarityPercentage >= SIMILARITY_THRESHOLD) {
                JOptionPane.showMessageDialog(this, "Plagiarism detected between the files.", "Result", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No plagiarism detected between the files.", "Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateSimilarityPercentage(File file1, File file2) throws IOException {
        Map<String, Integer> wordFreqMap1 = buildWordFrequencyMap(file1);
        Map<String, Integer> wordFreqMap2 = buildWordFrequencyMap(file2);

        double dotProduct = calculateDotProduct(wordFreqMap1, wordFreqMap2);
        double magnitude1 = calculateMagnitude(wordFreqMap1);
        double magnitude2 = calculateMagnitude(wordFreqMap2);

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        double cosineSimilarity = (dotProduct / (magnitude1 * magnitude2)) * 100;
        return cosineSimilarity;
    }

    private Map<String, Integer> buildWordFrequencyMap(File file) throws IOException {
        Map<String, Integer> wordFreqMap = new HashMap<>();
        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));

        Scanner scanner = new Scanner(content);
        while (scanner.hasNext()) {
            String word = scanner.next().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            if (!word.isEmpty()) {
                wordFreqMap.put(word, wordFreqMap.getOrDefault(word, 0) + 1);
            }
        }
        scanner.close();

        return wordFreqMap;
    }

    private double calculateDotProduct(Map<String, Integer> map1, Map<String, Integer> map2) {
        double dotProduct = 0.0;
        for (String word : map1.keySet()) {
            int freq1 = map1.getOrDefault(word, 0);
            int freq2 = map2.getOrDefault(word, 0);
            dotProduct += freq1 * freq2;
        }
        return dotProduct;
    }

    private double calculateMagnitude(Map<String, Integer> map) {
        double magnitude = 0.0;
        for (int freq : map.values()) {
            magnitude += freq * freq;
        }
        return Math.sqrt(magnitude);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismDetectorGUI::new);
    }
}
