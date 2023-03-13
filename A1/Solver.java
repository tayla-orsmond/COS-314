// Tayla Orsmond u21467456
// The Solver parent class for ILS and Tabu Search

// Tayla Orsmond u21467456
// Iterated Local Search class to solve the bin packing problem

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Solver {
    // Variables
    protected int capacity; // The capacity of the bins
    protected String pi; // The name of the PI
    protected ArrayList<Integer> items; // The data of the PI
    protected int optimal; // The optimal solution
    protected int best; // The best solution found
    protected long time; // The time to solve the PI
    protected ArrayList<ArrayList<Integer>> bins; // The bins used to solve the PI
    protected ArrayList<String> existingSummaries;

    // Constructor
    public Solver(int capacity) {
        this.capacity = capacity;
        this.best = Integer.MAX_VALUE;
        this.optimal = 0;
        this.time = 0;
        this.bins = new ArrayList<ArrayList<Integer>>();
        this.items = new ArrayList<Integer>();
        this.existingSummaries = new ArrayList<String>();
    }

    // Setters
    public void setPI(String pi) {
        this.pi = pi;
    }

    public void setData(ArrayList<String> data) {
        this.optimal = Integer.parseInt(data.get(0));
        data.remove(0);
        for (String item : data) {
            this.items.add(Integer.parseInt(item));
        }
    }

    // Getters
    public int getBestSolution() {
        return this.best;
    }

    public long getTime() {
        return this.time;
    }

    public String getBins() {
        String bins = "";
        for (ArrayList<Integer> bin : this.bins) {
            bins += "\n" + bin.toString();
        }
        return bins;
    }
    // Helpers
    // Check if a bin can fit a new item
    public boolean canFit(ArrayList<Integer> bin, int item) {
        int total = 0;
        for (int i : bin) {
            total += i;
        }
        if (total + item <= this.capacity) {
            return true;
        }
        return false;
    }
    // Write the results to a file for this PI
    public void writeResults(String path) {
        try {
            // Create a new file
            File file = new File(path);
            // If the file doesn't exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // Create a new file writer
            FileWriter writer = new FileWriter(file);
            // Write the results to the file
            writer.write("PI: " + this.pi);
            writer.write("\nSolution: " + this.best + " / " + this.optimal);
            if (this.best <= this.optimal) {
                writer.write(" (Optimal)");
            } else if (this.best == this.optimal + 1) {
                writer.write(" (Near Optimal)");
            } else {
                writer.write(" (Sub Optimal)");
            }
            writer.write("\nTime to Solve: " + this.time);
            writer.write("\nBins: {");
            writer.write(this.getBins());
            writer.write("\n}");
            // Close the file writer
            writer.close();

            // Create a new summary string
            String summary = "";
            if (this.best <= this.optimal) {
                summary = "O:";
            } else if (this.best == this.optimal + 1) {
                summary = "N:";
            } else {
                summary = "S:";
            }
            summary += this.getBestSolution() + ":" + this.getTime();
            // Add the summary to the existing summaries
            existingSummaries.add(summary);

        } catch (IOException e) {
            System.out.println("[Solver] Error writing to file " + path + ": ");
            e.printStackTrace();
        }
    }

    // Write the summary (add the summary) to the summary file for that dataset
    public void writeSummary(String path) {
        try {
            // Create a new file
            File file = new File(path);
            // If the file doesn't exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // Create a new file writer
            FileWriter writer = new FileWriter(file);
        
            // Write the summary to the file
            for (String line : existingSummaries) {
                writer.write(line + "\n");
            }
            // Close the file writer
            writer.close();
            existingSummaries.clear();

        } catch (IOException e) {
            System.out.println("[Solver] Error writing to SUMMARY file " + path + ": ");
            e.printStackTrace();
        }
    }
}