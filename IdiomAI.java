import java.io.*;
import java.util.*;

public class IdiomAI {

    static Map<String, String> idioms = new HashMap<>();

    public static void main(String[] args) throws Exception {

    loadIdioms(); // load from file
    Scanner sc = new Scanner(System.in);

    while (true) {
        System.out.println("\n===== Idiom AI Menu =====");
        System.out.println("1. Search Idiom");
        System.out.println("2. Add New Idiom");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        if (choice == 1) {
            searchIdiom(sc);
        } 
        else if (choice == 2) {
            addIdiom(sc);
        } 
        else if (choice == 3) {
            System.out.println("Exiting...");
            break;
        } 
        else {
            System.out.println("Invalid choice!");
        }
    }
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

static void searchIdiom(Scanner sc) {
    System.out.print("Enter half idiom: ");
    String input = sc.nextLine().toLowerCase();

    List<Map.Entry<String, Integer>> list = new ArrayList<>();

    for (String idiom : idioms.keySet()) {
        int dist = levenshtein(input, idiom);
        list.add(new AbstractMap.SimpleEntry<>(idiom, dist));
    }

    list.sort(Comparator.comparingInt(Map.Entry::getValue));

    System.out.println("\nTop Matches:");

    for (int i = 0; i < Math.min(3, list.size()); i++) {
        String idiom = list.get(i).getKey();
        System.out.println((i+1) + ". " + idiom);
        System.out.println("   Meaning: " + idioms.get(idiom));
    }
}

static void addIdiom(Scanner sc) throws Exception {
    System.out.print("Enter idiom: ");
    String idiom = sc.nextLine();

    System.out.print("Enter meaning: ");
    String meaning = sc.nextLine();

    idioms.put(idiom.toLowerCase(), meaning);

    // Save to file
    BufferedWriter bw = new BufferedWriter(new FileWriter("idioms.txt", true));
    bw.write(idiom + " = " + meaning);
    bw.newLine();
    bw.close();

    System.out.println("Idiom added successfully!");
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