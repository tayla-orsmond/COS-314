// Tayla Orsmond u21467456
// The Solver parent class for ILS and Tabu Search

// Tayla Orsmond u21467456
// Iterated Local Search class to solve the bin packing problem

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Solver {
    // Variables ====================
    protected int capacity; // The capacity of the bins
    protected String pi; // The name of the PI
    protected ArrayList<Integer> items; // The data of the PI
    protected int optimal; // The optimal solution (for evaluation)
    protected int best; // The best solution found (no. of bins)
    protected ArrayList<ArrayList<Integer>> bestBins; // The best solution found (bins)
    protected AtomicLong time; // The time taken to solve the PI
    protected ArrayList<ArrayList<Integer>> bins; // The bins used to solve the PI
    protected ArrayList<String> existingSummaries; // The summaries of the PIs that have been solved

    // Constructor ====================
    public Solver() {
        this.capacity = 0;
        this.best = Integer.MAX_VALUE;
        this.optimal = 0;
        this.time = new AtomicLong(0);
        this.bins = new ArrayList<ArrayList<Integer>>();
        this.items = new ArrayList<Integer>();
        this.bestBins = new ArrayList<ArrayList<Integer>>();
        this.existingSummaries = new ArrayList<String>();
    }

    // Setters ==================== 
    public void setPI(String pi) {
        this.pi = pi;
    }

    public void setOptimal(int optimal) {
        this.optimal = optimal;
    }

    public void setData(ArrayList<String> data) {
        data.remove(0);//no. of items in the PI
        this.capacity = Integer.parseInt(data.get(0));
        data.remove(0);
        for (String item : data) {
            this.items.add(Integer.parseInt(item));
        }
    }

    public void clear() {
        this.capacity = 0;
        this.best = Integer.MAX_VALUE;
        this.optimal = 0;
        this.time.set(0);
        this.bins.clear();
        this.items.clear();
        this.bestBins.clear();
    }

    // Getters
    public int getBestSolution() {
        return this.best;
    }

    public int getOptimalSolution() {
        return this.optimal;
    }

    public long getTime() {
        return this.time.get();
    }

    public String getBins() {
        String bins = "";
        for (ArrayList<Integer> bin : this.bins) {
            bins += "\n" + bin.toString();
        }
        return bins;
    }

    // Helpers ====================
    /**
     * Get the size of a bin (i.e, the sum of the items in the bin)
     * @param bin The bin to get the size of
     * @return The size of the bin (Integer)
     */
    public Integer sizeOf(int bin){
        return this.bins.get(bin).stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Make a deep copy of the bins to put in bestBins
     * @details This is used to make sure that the bestBins are not changed when the bins are changed
     * @return void
     */
    public void setBestBins() {
        this.bestBins.clear();
        for (ArrayList<Integer> bin : this.bins) {
            ArrayList<Integer> newBin = new ArrayList<Integer>(this.capacity);
            for (int item : bin) {
                newBin.add(item);
            }
            this.bestBins.add(newBin);
        }
    }
    
    /**
     * Make a deep copy of the bestBins to put in bins
     * @details This is used to backtrack to the bestBins (i.e., previous best solution)
     * @return void
     */
    public void setBins() {
        this.bins.clear();
        for (ArrayList<Integer> bin : this.bestBins) {
            ArrayList<Integer> newBin = new ArrayList<Integer>(this.capacity);
            for (int item : bin) {
                newBin.add(item);
            }
            this.bins.add(newBin);
        }
    }

    /**
     * Best fit all items
     * @details This is used to pack all items from the PI into bins using the best fit heuristic
     * @return void
     */
    protected void bestFitAll(){
        for(Integer item : this.items){
            bestFit(item);
        }
    }

    /**
     * Best fit heuristic
     * @details This is used to pack an item into a bin using the best fit heuristic
     * @param item The item to pack
     * @return void
     */
    protected void bestFit(Integer item){
        // 2. Pack items according to best fit heuristic (pack the item in the bin that results in the least space left after packing)
        int bestBin = -1;
        int bestSpace = this.capacity;
        // Get the bin that will have the least space left after packing the item
        for(ArrayList<Integer> bin : this.bins){
            int space = this.capacity - bin.stream().mapToInt(Integer::intValue).sum() - item;
            if(space >= 0 && space < bestSpace){// item can fit & fits better than previous best
                bestBin = this.bins.indexOf(bin);
                bestSpace = space;
            }
        }

        if(bestBin == -1){ //No bin found
            //Create new bin
            ArrayList<Integer> newBin = new ArrayList<>(this.capacity);
            newBin.add(item);
            this.bins.add(newBin);
        } else {
            //Add item to bin
            this.bins.get(bestBin).add(item);
        }
    }
    
    /**
     * Select a bin to work on based on the picked bin selection heuristic
     * @details This is used to select a bin to work on (a random bin or the least filled bin)
     * @param picked The bin selection heuristic to use
     * @return The bin index to work on (Integer)
     */
    protected int getBin(char picked){
        switch(picked){
            case 'L':
                return leastFilledBin();
            case 'R':
                Boolean bias = Math.random() < 0.6; //Bias towards the back half of the bins
                if(bias){
                    return (int)(Math.random() * (this.bins.size() / 2)) + (this.bins.size() / 2);
                }
                return (int)(Math.random() * this.bins.size());
            default:
                return 0;
        }
    }

    /**
     * Get the least filled bin
     * @details This is used to get the bin with the least amount of items in it
     * @return The bin index of the least filled bin (Integer)
     */
    protected int leastFilledBin(){
        int leastFilledBin = 0;
        int leastFilled = sizeOf(leastFilledBin);
        for(int i = 1; i < this.bins.size(); i++){
            if(sizeOf(i) < leastFilled){
                leastFilledBin = i;
                leastFilled = sizeOf(i);
            }
        }
        return leastFilledBin;
    }

    /**
     * Try to empty a bin
     * @details This is used to try to empty a bin by packing all items in the bin into other bins
     * @param binToEmpty The bin to empty (int)
     * @return void
     */
    protected void tryEmptyBin(int binToEmpty){
        //copy & remove the bin we are trying to empty
        ArrayList<Integer> bin = new ArrayList<>(this.bins.get(binToEmpty));
        this.bins.remove(binToEmpty);
        for(Integer item : bin){
            bestFit(item);
        }
    }

    /**
     * Try to swap an item from a bin with an item from a random bin
     * @details This is used to try to swap an item from a bin with an item from a random bin
     * @param pickedBin The bin to swap an item from (int)
     * @param randomBin The bin to swap an item from (int)
     * @return void
     */
    protected void trySwap(int pickedBin, int randomBin){
        // 4. & 6. Attempt to swap an item from the bin with an item from a random bin (if possible)
        int randomItem = (int)(Math.random() * this.bins.get(randomBin).size());
        int pickedItem = (int)(Math.random() * this.bins.get(pickedBin).size());
        if(pickedItem > randomItem && sizeOf(randomBin) + pickedItem <= this.capacity && sizeOf(pickedBin) + randomItem <= this.capacity){
            //swap
            Integer temp = this.bins.get(randomBin).get(randomItem);
            this.bins.get(randomBin).set(randomItem, this.bins.get(pickedBin).get(pickedItem));
            this.bins.get(pickedBin).set(pickedItem, temp);
        }
    }

    /**
     * Write the results of this PI to a file and add a summary of them to the existing summaries 
     * @param path The path to the file to write to
     * @return void
     */
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
                writer.write(" (Sub Optimal) - off by: " + (this.best - this.optimal));
            }
            writer.write("\nTime to Solve: " + this.getTime());
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

    /**
     * Write the summary of all PI's to a file using the existing summaries
     * @param path The path to the file to write to
     * @return void
     */
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