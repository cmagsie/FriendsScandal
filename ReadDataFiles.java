import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ReadDataFiles {
    private static final HashMap<String, Set<String>> emailGraph = new HashMap<>(); // friendship graph

   void readFiles(String directory) {
    File folder = new File(directory);
    File[] files = null;
    try {
        files = folder.listFiles();
    } catch (Exception e) {
        System.out.println("Error reading directory: " + directory);
        e.printStackTrace();
    }

    if (files != null) {
        for (File file : files) {
            if (file.isDirectory()) {
                readFiles(file.getAbsolutePath()); // recursively read subdirectories
            } else if (file.isFile() && file.getName().endsWith(".txt")) {
                extractEmailMetadata(file); // extract email metadata from the file
            }
        }
    }
}


    private void extractEmailMetadata(File file) {
        String messageId = null, from = null, to = null;
        boolean isValid = true;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("Message-ID:")) {
                    messageId = line.substring(12).trim();
                } else if (line.startsWith("From:")) {
                    from = extractEmail(line.substring(5).trim());
                    if (from == null) isValid = false; // invalid sender email
                } else if (line.startsWith("To:")) {
                    String[] tos = line.substring(3).trim().split(",");
                    for (String toi : tos) {
                        if (to == null) isValid = false; // invalid recipient email
                        emailGraph.computeIfAbsent(from, k -> new HashSet<>()).add(toi); // add recipient to sender's friend list
                        emailGraph.computeIfAbsent(toi, k -> new HashSet<>()).add(from); // add sender to recipient's friend list
                    }
                }

                if (!isValid) break; // email is invalid, skip the rest of the file
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        }
    }

    // function to extract a valid email address from a string
    private String extractEmail(String str) {
        // Create a regular expression matcher for the email address pattern.
        Matcher matcher = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b").matcher(str);

        // If the matcher finds an email address, return it.
        if (matcher.find()) {
            return matcher.group();
        } else {
            // Otherwise, return null.
            return null;
        }
    }



    public static void mergeEmailGraph(HashMap<String, Set<String>> emailGraph) {
        HashMap<String, Set<String>> newEmailGraph = new HashMap<>(); // Create a new graph to store the simplified graph.
        for (String key : emailGraph.keySet()) { // Iterate over the keys of the original graph.
            String newKey = key; // Set the new key to the current key.
            Set<String> newValue = new HashSet<>(emailGraph.get(key)); // Set the new value to a copy of the value for the current key.
            for (String otherKey : emailGraph.keySet()) { // Iterate over the keys of the original graph.
                if (!key.equals(otherKey) && emailGraph.get(otherKey).containsAll(emailGraph.get(key))) { // If the current key is not equal to the other key and the value for the other key contains all of the values for the current key.
                    newKey = otherKey; // Set the new key to the other key.
                    newValue.addAll(emailGraph.get(otherKey)); // Add the values for the other key to the new value.
                }
            }
            newEmailGraph.put(newKey, newValue); // Add the new key and value to the new graph.
        }
        emailGraph.clear(); // Clear the original graph.
        emailGraph.putAll(newEmailGraph); // Copy the new graph to the original graph.
    }


        // function to print the friendship graph
        void printFriendshipGraph() {
            // Iterate over the keys of the graph.
            for (String person : emailGraph.keySet()) {
                // Print the person's name and the set of people they are friends with.
                System.out.println(person + " -> " + emailGraph.get(person));
            }
        }

    public static void main(String[] args) {
        ReadDataFiles rdf = new ReadDataFiles();

        String currentDir = System.getProperty("user.dir");

        rdf.readFiles(currentDir +"/maildir");

        mergeEmailGraph(emailGraph);

        IdentifyAndPrintConnectors connectorFinder = new IdentifyAndPrintConnectors(emailGraph, "connectors.txt");

        Set<String> connector = connectorFinder.findAndPrintConnectors();

        ProvideDetailsEachPerson detailsProvider = new ProvideDetailsEachPerson(emailGraph, connector);

        detailsProvider.run();
    }
}
