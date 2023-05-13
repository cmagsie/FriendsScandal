import java.util.*;

public class ProvideDetailsEachPerson {

    // This class provides details for each person in a graph.

    private final Map<String, Set<String>> graph; // The graph of people and their connections.
    private final Set<String> connectors; // The set of connectors, which are people who can connect any two people.
    private final Map<String, Set<String>> teams; // The map of teams, where each key is a person and each value is the set of people on that person's team.

    // This constructor initializes the class with the given graph and set of connectors.
    public ProvideDetailsEachPerson(Map<String, Set<String>> graph, Set<String> connectors) {
        this.graph = graph;
        this.connectors = connectors;
        this.teams = new HashMap<>();
        for (String v : graph.keySet()) {
            // This code finds the team for the given person.
            Set<String> team = new HashSet<>();
            dfs(v, team, new HashSet<>());
            if (!team.isEmpty()) {
                teams.put(v, team);
            }
        }
    }

    // This recursive function performs a depth-first search of the graph, starting at the given vertex.
    private void dfs(String v, Set<String> team, Set<String> visited) {
        // Mark the given vertex as visited.
        visited.add(v);
        // Add the given vertex to the team.
        team.add(v);
        // Get the set of neighbors of the given vertex.
        Set<String> neighbors = graph.get(v);
        // If there are no neighbors, return.
        if (neighbors == null) {
            return;
        }
        // For each neighbor of the given vertex,
        for (String w : neighbors) {
            // If the neighbor has not been visited and is not a connector,
            if (!visited.contains(w) && !connectors.contains(w)) {
                // Perform a recursive call on the neighbor.
                dfs(w, team, visited);
            }
        }
    }

    // This method prints the details for the given person.
    public void run() {
        // Create a scanner object.
        Scanner scanner = new Scanner(System.in);
        // Keep looping until the user enters "EXIT".
        while (true) {
            // Print a prompt for the user to enter an email address.
            System.out.print("Email address of the individual (or EXIT to quit): ");
            // Get the user's input.
            String email = scanner.nextLine().trim();
            // If the user entered "EXIT", break out of the loop.
            if (email.equalsIgnoreCase("exit")) {
                break;
            }
            // Get the set of people who the user has sent messages to.
            Set<String> senders = graph.containsKey(email) ? graph.get(email) : new HashSet<>();
            // Get the set of people who have sent messages to the user.
            Set<String> receivers = new HashSet<>();
            for (String v : graph.keySet()) {
                // If the user has received messages from the given person,
                if (graph.get(v).contains(email)) {
                    // Add the given person to the set of receivers.
                    receivers.add(v);
                }
            }
            // Get the set of people who are on the user's team.
            Set<String> team = teams.getOrDefault(email, new HashSet<>());
            // Print the number of people who the user has sent messages to.
            System.out.printf("%s has sent messages to %d others\n", email, senders.size());
            // Print the number of people who have sent messages to the user.
            System.out.printf("%s has received messages from %d others\n", email, receivers.size());
            // Print the number of people who are on the user's team.
            System.out.printf("%s is in a team with %d individuals\n", email, team.size());
        }
        // Close the scanner object.
        scanner.close();
    }
}
