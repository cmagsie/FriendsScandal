import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class IdentifyAndPrintConnectors {

    /*
     * This class identifies and prints connectors in a graph.
     *
     * A connector is a person who is connected to at least two other people,
     * and who is not connected to any other person who is connected to more people than them.
     */

    private final Map<String, Set<String>> graph; // The graph of people and their connections.
    private final String filename; // The filename to write the connectors to, or null if the connectors should be printed to the console.
    private int dfsnum; // The current depth-first search number.
    private final Map<String, Integer> dfsnumMap; // A map from people to their depth-first search numbers.
    private final Map<String, Integer> backMap; // A map from people to the back edge number of their lowest back edge.
    private final Set<String> connectors; // The set of connectors.

    public IdentifyAndPrintConnectors(Map<String, Set<String>> graph, String filename) {
        this.graph = graph;
        this.filename = filename;
        dfsnum = 0;
        dfsnumMap = new HashMap<>();
        backMap = new HashMap<>();
        connectors = new HashSet<>();
    }

    /*
     * This method finds and prints the connectors in the graph.
     *
     * @return The set of connectors.
     */
    public Set<String> findAndPrintConnectors() {
        try (PrintWriter out = (filename == null) ? new PrintWriter(System.out) : new PrintWriter(new FileWriter(filename))) {

            for (String v : graph.keySet()) {
                if (!dfsnumMap.containsKey(v)) {
                    dfs(v, v, out);
                }
            }
            for (String connector : connectors) {
                System.out.println(connector);
            }
            return connectors;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        return null;
    }

    /*
     * This recursive function performs a depth-first search of the graph, starting at the given vertex.
     *
     * @param v The vertex to start the depth-first search at.
     * @param parent The parent of the current vertex.
     * @param out The PrintWriter to write the connectors to.
     */
    private void dfs(String v, String parent, PrintWriter out) {
        dfsnum++;
        dfsnumMap.put(v, dfsnum);
        backMap.put(v, dfsnum);
        int children = 0;
        Set<String> neighbors = graph.get(v);
        if (neighbors == null) {
            return;
        }
        for (String w : neighbors) {
            if (!dfsnumMap.containsKey(w)) {
                children++;
                dfs(w, v, out);
                backMap.put(v, Math.min(backMap.get(v), backMap.get(w)));
                if ((!Objects.equals(parent, v) && dfsnumMap.get(v) <= backMap.get(w)) || (Objects.equals(parent, v) && children > 1)) {
                    if (!connectors.contains(v)) {
                        connectors.add(v);
                        out.println(v);
                    }
                }
            } else if (!w.equals(parent)) {
                backMap.put(v, Math.min(backMap.get(v), dfsnumMap.get(w)));
            }
        }
    }
}
