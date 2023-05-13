# FriendsScandal

The class ReadDataFiles reads email files from a given directory and extracts metadata from them to construct a friendship graph. The friendship graph is stored in a HashMap<String, Set<String>>. The code uses regular expressions to extract valid email addresses from the metadata and also recursively reads subdirectories.

The mergeEmailGraph method takes the friendship graph and it simplifies it by merging nodes that have identical sets of friends. The resulting graph is stored back into the original emailGraph object.

The printFriendshipGraph method prints out the resulting friendship graph.

The main method creates an instance of ReadDataFiles, reads email files from the maildir directory, merges the friendship graph, identifies the connectors (people who bridge disconnected subgraphs), prints out the details for each person in the graph, and writes the connectors to a file called connectors.txt.
  
 The running time for this program will be O(n^2).
