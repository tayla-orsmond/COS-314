// Tayla Orsmond u21467456
// Main program for solving the Bin Packing Problem using the Iterated Local Search and Tabu Search algorithms
import java.util.ArrayList;
import java.io.IOException;

public class Main {
    // Capacity of the bins
    private static final int CAPACITY = 1000;
    // Test datasets
    private static String[] testDatasets = {
        "Test/Test_1",
        "Test/Test_2",
        "Test_3"
    };
    // Data set paths 
    private static String[] datasets = {
        "Falkenauer/Falkenauer_T",
        "Falkenauer/Falkenauer_U",
        "Hard28",
        "Scholl/Scholl_1",
        "Scholl/Scholl_2",
        "Scholl/Scholl_3",
        "Schwerin/Schwerin_1",
        "Schwerin/Schwerin_2",
        "Waescher",
    };
    
    public static void main(String[] args) {
        // Create a new loader object
        Loader loader = new Loader();
        ILS ils = new ILS(CAPACITY);
        TabuSearch ts = new TabuSearch(CAPACITY);
        try {
            // Loop through the datasets
            for (String dataset : testDatasets) {
                // Load the dataset
                ArrayList<String> pis = loader.loadDataset(dataset);
                // Loop through the PIs (files)
                for (String pi : pis) {
                    // Load the PI
                    ArrayList<String> data = loader.readFile(dataset + "/" + pi);

                    // Solve the bin packing problem for the PI using ILS
                    ils.setPI(pi);
                    ils.setData(data);
                    ils.solve();
                    ils.writeResults("Solutions/ILS/" + dataset + "/SOL_" + pi); // Write the results (all bins & solution & time to solve)
                    System.out.println("[ILS] PI: "+ pi + " -> No of bins: " + ils.getBestSolution() + " Time to Solve: " + ils.getTime()); // Print the best solution & time to solve

                    // Solve the bin packing problem for the PI using Tabu Search
                    ts.setPI(pi);
                    ts.setData(data);
                    ts.solve();
                    ts.writeResults("Solutions/Tabu/" + dataset + "/SOL_" + pi); // Write the results (all bins & solution & time to solve)
                    System.out.println("[TS] PI: "+ pi + " -> No of bins: " + ts.getBestSolution() + " Time to Solve: " + ts.getTime()); // Print the best solution & time to solve
                }
                ils.writeSummary("Solutions/ILS/" + dataset + "/Summary.txt"); // Write the summary (best solution & time to solve) for all PIs
                ts.writeSummary("Solutions/Tabu/" + dataset + "/Summary.txt"); // Write the summary (best solution & time to solve)
            }
        } catch (Exception e) {
            System.out.println("[Main] Error loading datasets: " + e);
            e.printStackTrace();
        }

        Summarize();
    }
    protected static void Summarize() {
        // Create a new summarizer object (Calculates and summarizes the results)
        Summarizer summarizer = new Summarizer();
        // Loop through the datasets
        for (String dataset : testDatasets) {
            // Summarize the dataset
            summarizer.summarize(dataset, "Solutions/ILS/" + dataset + "/Summary.txt", "Solutions/Tabu/" + dataset + "/Summary.txt");
        }
        // Write the summary
        summarizer.writeSummary("Solutions/Summary.txt");
        // Print the summary
        summarizer.printSummary();
    }
}