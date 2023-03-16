// Tayla Orsmond u21467456
// The Summarizer class is used to calculate & summarize the results of the ILS and Tabu Search algorithms

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Summarizer {
    private ArrayList<String> summaries;
    private Long totalTimeForILS;
    private Long totalTimeForTabu;
    private Integer totalPIsTested;

    Summarizer() {
        summaries = new ArrayList<String>();
        totalTimeForILS = 0L;
        totalTimeForTabu = 0L;
        totalPIsTested = 0;
    }

    // Summarize the dataset results for each algorithm (ILS & Tabu Search)
    public void summarize(String dataset, String ITSPath, String TabuPath) {
        try {
            // Read the results for the ILS algorithm
            Loader loader = new Loader();
            ArrayList<String> res = loader.readFile(ITSPath);
            // Calculate results for the ILS algorithm
            String summary = "Dataset: " + dataset;
            summary += calculateSummary(res, "ILS");
            // Clear the results
            res.clear();

            // Read the results for the Tabu Search algorithm
            res = loader.readFile(TabuPath);
            // Calculate results for the Tabu Search algorithm
            summary += calculateSummary(res, "Tabu");
            // Clear the results
            res.clear();

            // Add the results to the summaries
            summaries.add(summary);
        } catch (IOException e) {
            System.out.println("[Summarizer] Error reading SUMMARY files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Calculate the results per dataset for each algorithm (ILS & Tabu Search)
    private String calculateSummary(ArrayList<String> results, String algorithm) {
        Integer totalPIs = 0;
        Integer totalOptimal = 0;
        Integer totalNearOptimal = 0;
        Integer totalSubOptimal = 0;
        Long totalTime = 0L;
        Long avgTime = 0L;

        // Loop through the results
        for (String result : results) {
            // Split the result into an array
            String[] splitResult = result.split(":");
            // Check if the result is optimal
            if (splitResult[0].compareTo("O") == 0) {
                totalOptimal++;
            } else if (splitResult[0].compareTo("N") == 0) {
                totalNearOptimal++;
            } else if (splitResult[0].compareTo("S") == 0) {
                totalSubOptimal++;
            }
            // Add the time to the total time
            totalTime += Long.parseLong(splitResult[2]);
            // Increment the total number of PIs
            totalPIs++;
        }
        // Calculate the average time
        avgTime = totalTime / totalPIs;
        // Add the total time to the total time for the algorithm
        if (algorithm.compareTo("ILS") == 0) {
            totalTimeForILS += totalTime;
        } else if (algorithm.compareTo("Tabu") == 0) {
            totalTimeForTabu += totalTime;
        }
        // Add the total number of PIs to the total number of PIs tested
        totalPIsTested += totalPIs;
        // Return the summary of the results
        return "\n\t" + algorithm + " [Optimal: " + totalOptimal + " NearOptimal: " + totalNearOptimal + " SubOptimal: " + totalSubOptimal + " Total: " + totalPIs + " AvgTime: " + avgTime + "ms]";
    }

    // Write the results to a file
    public void writeSummary(String path) {
        // Write the summaries to the file
        try {
            File file = new File(path);
            // If the file doesn't exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write("Summaries of the results for the ILS and Tabu Search algorithms: ");
            // Write the total time for the ILS algorithm
            writer.write("\nTotal Time for ILS: " + totalTimeForILS + "ms");
            // Write the total time for the Tabu Search algorithm
            writer.write("\nTotal Time for Tabu: " + totalTimeForTabu + "ms");
            // Write the total number of PIs tested
            writer.write("\nTotal PIs Tested: " + totalPIsTested / 2);
            // Loop through the summaries
            for (String summary : summaries) {
                // Write the summary to the file
                writer.write("\n" + summary);
            }           
            // Close the writer
            writer.close();
        } catch (IOException e) {
            System.out.println("[Summarizer] Error writing summary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Print the results to the console
    public void printSummary() {
        // Print the total time for the ILS algorithm
        System.out.println("Total Time for ILS: " + totalTimeForILS + "ms");
        // Print the total time for the Tabu Search algorithm
        System.out.println("Total Time for Tabu: " + totalTimeForTabu + "ms");
        // Print the total number of PIs tested
        System.out.println("Total PIs Tested: " + totalPIsTested / 2);
        // Loop through the summaries
        for (String summary : summaries) {
            // Print the summary to the console
            System.out.println(summary);
        }
    }
}
