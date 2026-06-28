import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class IdiomUI {

    static Map<String, String> idioms = new HashMap<>();

    public static void main(String[] args) throws Exception {

        loadIdioms();

        JFrame frame = new JFrame("Idiom AI");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter half idiom:");
        JTextField inputField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add Idiom");
        JButton clearButton = new JButton("Clear");
        JTextArea resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);

        frame.add(label);
        frame.add(inputField);
        frame.add(searchButton);
        frame.add(addButton);
        frame.add(clearButton);
        frame.add(new JScrollPane(resultArea));

        clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        inputField.setText("");
        resultArea.setText("");
        }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idiom = JOptionPane.showInputDialog("Enter idiom:");
                String meaning = JOptionPane.showInputDialog("Enter meaning:");

                if (idiom != null && meaning != null) {
                     idioms.put(idiom.toLowerCase(), meaning);

                      try {
                         BufferedWriter bw = new BufferedWriter(new FileWriter("idioms.txt", true));
                         bw.write(idiom + " = " + meaning);
                         bw.newLine();
                         bw.close();
                       } catch (Exception ex) {
                         ex.printStackTrace();
                      }

                     JOptionPane.showMessageDialog(null, "Idiom added!");
               }
           }
       });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String input = inputField.getText().toLowerCase();
                java.util.List<Map.Entry<String, Integer>> list = new ArrayList<>();

                for (String idiom : idioms.keySet()) {
                    int dist = levenshtein(input, idiom);
                    list.add(new AbstractMap.SimpleEntry<>(idiom, dist));
                }

                list.sort(Comparator.comparingInt(Map.Entry::getValue));

                StringBuilder output = new StringBuilder();
                output.append("Top Matches:\n\n");

                for (int i = 0; i < Math.min(3, list.size()); i++) {
                    String idiom = list.get(i).getKey();
                    output.append((i+1) + ". " + idiom + "\n");
                    output.append("   Meaning: " + idioms.get(idiom) + "\n\n");
                }

                resultArea.setText(output.toString());
            }
        });

        frame.setVisible(true);
    }

    static void loadIdioms() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("idioms.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("=");
            idioms.put(parts[0].trim().toLowerCase(), parts[1].trim());
        }

        br.close();
    }

    public static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length()+1][b.length()+1];

        for (int i = 0; i <= a.length(); i++)
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    int cost = (a.charAt(i-1) == b.charAt(j-1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                        Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1),
                        dp[i-1][j-1] + cost
                    );
                }
            }
        return dp[a.length()][b.length()];
    }
}