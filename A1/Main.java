// Tayla Orsmond u21467456
// Main program for solving the Bin Packing Problem using the Iterated Local Search and Tabu Search algorithms
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    // Dataset paths 
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
        System.out.println("========== COS 314 - Assignment 1 - Bin Packing Problem ==========");
        // Create a new loader object
        Loader loader = new Loader();
        System.out.println("========== Running ILS... ==========");
        runILS(loader);
        System.out.println("\n========== Running Tabu Search... ==========");
        runTS(loader);
        System.out.println("\n========== Summarizing results... ==========");
        summarize();
        System.out.println("The results for every PI have been written to the Solutions folder, as well as a summary for each dataset & a summary_report of all results.");
        //Allow user to press enter to exit
        System.out.println("\nPress enter to exit...");
        try {
            System.in.read();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    /**
     * Runs the Iterated Local Search algorithm on the datasets
     * @param loader The loader object
     * @return void
     */
    protected static void runILS(Loader loader) {
        ILS ils = new ILS();
        try {
            // Load the optimal solutions
            HashMap<String, Integer> optima = loader.loadOptima("Optima.txt");
            // Loop through the datasets
            for (String dataset : datasets) {
                // Load the dataset
                ArrayList<String> pis = loader.loadDataset(dataset);
                // Loop through the PIs (files)
                System.out.print("-");
                for (String pi : pis) {
                    System.out.print("\r\\");
                    // Load the PI
                    ArrayList<String> data = loader.readFile(dataset + "/" + pi);
                    // Get the optimal solution for the PI
                    int optimal = optima.get(pi.substring(0, pi.length() - 4));
                    // Solve the bin packing problem for the PI using ILS
                    ils.clear(); // Clear the ILS object
                    ils.setPI(pi);
                    ils.setData(data);
                    System.out.print("\r|");
                    ils.setOptimal(optimal);
                    ils.solve();
                    System.out.print("\r/");
                    ils.writeResults("Solutions/ILS/" + dataset + "/SOL_" + pi); // Write the results (all bins & solution & time to solve)
                    //System.out.println("[ILS] PI: "+ pi.substring(0, pi.length() - 4) + " -> No of bins: " + ils.getBestSolution() + " Time to Solve: " + ils.getTime()); // Print the best solution & time to solve
                    System.out.print("\r-");
                }
                System.out.print("\r");
                ils.writeSummary("Solutions/ILS/" + dataset + "/Summary.txt"); // Write the summary (best solution & time to solve for all PIs)
            }
        } catch (Exception e) {
            System.out.println("[Main] Error loading datasets: " + e);
            e.printStackTrace();
        }
    }
    /**
     * Runs the Tabu Search algorithm on the datasets
     * @param loader The loader object
     * @return void
     */
    protected static void runTS(Loader loader) {
        TabuSearch ts = new TabuSearch();
        try {
            // Load the optimal solutions
            HashMap<String, Integer> optima = loader.loadOptima("Optima.txt");
            // Loop through the datasets
            for (String dataset : datasets) {
                // Load the dataset
                ArrayList<String> pis = loader.loadDataset(dataset);
                // Loop through the PIs (files)
                System.out.print("-");
                for (String pi : pis) {
                    System.out.print("\r\\");
                    // Load the PI
                    ArrayList<String> data = loader.readFile(dataset + "/" + pi);
                    // Get the optimal solution for the PI
                    int optimal = optima.get(pi.substring(0, pi.length() - 4));

                    // Solve the bin packing problem for the PI using Tabu Search
                    ts.clear(); // Clear the Tabu Search object
                    ts.setPI(pi);
                    ts.setData(data);
                    System.out.print("\r|");
                    ts.setOptimal(optimal);
                    ts.solve();
                    System.out.print("\r/");
                    ts.writeResults("Solutions/Tabu/" + dataset + "/SOL_" + pi); // Write the results (all bins & solution & time to solve)
                    //System.out.println("[TS] PI: "+ pi.substring(0, pi.length() - 4) + " -> No of bins: " + ts.getBestSolution() + " Time to Solve: " + ts.getTime()); // Print the best solution & time to solve
                    System.out.print("\r-");
                }
                System.out.print("\r");
                ts.writeSummary("Solutions/Tabu/" + dataset + "/Summary.txt"); // Write the summary (best solution & time to solve for all PIs)
            }
        } catch (Exception e) {
            System.out.println("[Main] Error loading datasets: " + e);
            e.printStackTrace();
        }
    }
    /**
     * Summarizes the results of the ILS and Tabu Search algorithms using the summarizer class
     * @details Compares the results of the ILS and Tabu Search algorithms and writes the results to a file
     * @return void
     */
    protected static void summarize() {
        // Create a new summarizer object (Calculates and summarizes the results)
        Summarizer summarizer = new Summarizer();
        // Loop through the datasets
        for (String dataset : datasets) {
            // Summarize the dataset
            summarizer.summarize(dataset, "Solutions/ILS/" + dataset + "/Summary.txt", "Solutions/Tabu/" + dataset + "/Summary.txt");
        }
        // Write the summary
        summarizer.writeSummary("Solutions/Summary_Report.txt");
        // Print the summary
        summarizer.printSummary();
    }
}