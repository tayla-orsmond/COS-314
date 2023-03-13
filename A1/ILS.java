// Tayla Orsmond u21467456
// Iterated Local Search class to solve the bin packing problem
// 1. Generate an initial solution using a constructive heuristic or random method.
// 	    Initial construction of your node/s (partial) or a complete search space.
// 2. Apply a local search algorithm to improve the solution (perturb).
// 	    Neighbors may not exist but may be perturbed by you
// 3. Obtain a local optimum.
// 	    Search the neighbors to find the best solution of them.
// 4. Perturb the local optimum to obtain a new solution.
// 	    Generate more neighbors to move you to the next point.
// 5. Apply the local search algorithm to the new solution.
// 6. If the new solution is better than the current solution, update the current solution.
// 7. Repeat steps 4-6 until a stopping criterion is met (Acceptance criteria).

import java.util.ArrayList;
import java.util.Collections;

public class ILS extends Solver {
    public ILS(int capacity) {
        super(capacity);
    }

    public void solve () {
        long start = System.currentTimeMillis();
        
        // Solve the bin packing problem using the Iterated Local Search algorithm
        // 1. Start with the best fit heuristic to pack the items into bins (should data be sorted?)
            // - Sort the items by size (descending)(?)
            // - Pack the items into bins using the best fit heuristic (i.e., the bin with the least amount of space left)
            // - Store the solution as the current best solution
        // 2. Apply a local search algorithm to search for the least-filled bin and pack the items from that bin into other bins
            // - Try to move an item from the least-filled bin to another bin
            // - If the item can be moved to another bin, and the new item better fills the bin, move it
            // - If the item cannot be moved to another bin, move to the next item
        // 3. If the least-filled bin is empty, repeat the process for the next least-filled bin
            // - If the new solution is better than the current solution, update the current solution
        // 4. If the least filled bin cannot be emptied, swap an item from the least filled bin with an item in a random bin
            // - If the new solution is better than the current solution, update the current solution
            // - Repeat the process 
        // 5. Repeat steps 2-4 until a stopping criterion is met (Acceptance criteria).
            // - Stop when the best solution found is the same as the optimal solution
            // - Generate a random number between 0 and 1, if the number is less than 0.5, stop
        // ?? May need to keep track of several best solutions to backtrack to if the current solution is not better than the best solution
       
       
        // 1. Generate initial solition
        // Sort the items by size (descending)
        Collections.sort(this.items, Collections.reverseOrder());
        // Pack the items into bins using the best fit heuristic (i.e., the bin with the least amount of space left)
        bestFit();
        // Store the solution as the current best solution
        setBestBins();
        this.best = this.bins.size();
        if(this.best == 1) {
            long end = System.currentTimeMillis();
            this.time = end - start;
            return;
        }
        // 2. Apply a local search algorithm to search for the least-filled bin and pack the items from that bin into other bins
        Boolean repeat = false;
        do {
            do {
                int leastFilledBin = getLeastFilledBin();
                System.out.println("Least filled bin: " + leastFilledBin);
                // Try to move the items from the least-filled bin to another bin/s
                tryEmptyLeastFilledBin(leastFilledBin);
                // If the least-filled bin is empty, repeat the process for the next least-filled bin
                // If the new solution is better than the current solution, update the current solution
                if (this.bins.get(leastFilledBin).isEmpty()) {
                    this.bins.remove(leastFilledBin);
                    setBestBins();
                    this.best = this.bins.size();
                    repeat = true;
                    System.out.println("Empty bin");
                } else {
                    repeat = false;
                    System.out.println("Not empty");
                }
            } while (repeat);
            
            // 3. If the least filled bin cannot be emptied, swap an item from the least filled bin with an item in a random bin
            //Take the largest piece from the lowest filled bin, and exchange with a smaller piece from a random bin (if possible)
            int leastFilledBin = getLeastFilledBin();
            System.out.println("Least filled bin: " + leastFilledBin);
            Boolean shuffled = shuffle(leastFilledBin);
            System.out.println("Shuffled: " + shuffled);
            if(shuffled) {
                repeat = true;
                System.out.println("Shuffle");
            } else {
                repeat = false;
            }
            // If the new solution is better than the current solution, update the current solution
            if (this.bins.size() < this.best || Math.random() < 0.5) {
                setBestBins();
                this.best = this.bins.size();
            }
            // Generate a random number between 0 and 1, if the number is less than 0.4, stop
            if (Math.random() < 0.3) {
                repeat = false;
            }
        } while (repeat);

        long end = System.currentTimeMillis();
        this.time = end - start;
    }

    // Best fit heuristic
    private void bestFit() {
        // Pack the items into bins using the best fit heuristic (i.e., the bin with the least amount of space left)
        for(Integer i: this.items) {
            // Search the bins for the bin that will have the least space left if that item is added to it
            int leastSpaceLeft = this.capacity;
            int leastSpaceLeftBin = 0;
            for(int j = 0; j < this.bins.size(); j++) {
                int space = this.capacity - this.bins.get(j).stream().mapToInt(Integer::intValue).sum();
                if(space >= i && space < leastSpaceLeft){
                    leastSpaceLeft = space;
                    leastSpaceLeftBin = j;
                }
            }
            // If the item can be added to a bin, add it to the bin
            if (leastSpaceLeft != this.capacity) {
                this.bins.get(leastSpaceLeftBin).add(i);
            } else { // If the item cannot be added to a bin, create a new bin and add the item to it
                ArrayList<Integer> bin = new ArrayList<Integer>();
                bin.add(i);
                this.bins.add(bin);
            }
        }
    }

    // Get the least filled bin
    // Search the bins for the bin with the fewest items
    private int getLeastFilledBin() {
        int leastFilledBin = 0;
        int leastFilledBinSize = this.capacity;
        for (ArrayList<Integer> bin : this.bins) {
            int binSize = bin.stream().mapToInt(Integer::intValue).sum();
            if (binSize < leastFilledBinSize) {
                leastFilledBinSize = binSize;
                leastFilledBin = this.bins.indexOf(bin);
            }
        }
        return leastFilledBin;
    }

    // Try to empty the least filled bin
    private void tryEmptyLeastFilledBin(int leastFilledBin) {
        for (Integer item : this.bins.get(leastFilledBin)) {
            // Search the bins for the bin that will have the least space left if that item is added to it
            int leastSpaceLeft = this.capacity;
            int leastSpaceLeftBin = 0;
            for(int j = 0; j < this.bins.size(); j++) {
                // if not the least filled bin
                if (j != leastFilledBin) {
                    int space = this.capacity - this.bins.get(j).stream().mapToInt(Integer::intValue).sum();
                    if(space >= item && space < leastSpaceLeft){
                        leastSpaceLeft = space;
                        leastSpaceLeftBin = j;
                    }
                }
            }
            // If the item can be added to a bin, add it to the bin
            if (leastSpaceLeft != this.capacity) {
                this.bins.get(leastSpaceLeftBin).add(item);
                this.bins.get(leastFilledBin).remove(item);
            }
        }
    }

    // Shuffle
    private Boolean shuffle(int leastFilledBin) {
        // Take the largest piece from the lowest filled bin, and exchange with a smaller piece from a random bin (if possible)
        // Get the largest item from the least filled bin
        int largestItem = this.bins.get(leastFilledBin).stream().mapToInt(Integer::intValue).max().getAsInt();
        System.out.println("Largest item: " + largestItem);
        // Get a random bin
        int randomBin = (int) (Math.random() * this.bins.size());
        System.out.println("Random bin: " + randomBin);
        int smallestItem = 0;
        do {
            randomBin = (int) (Math.random() * this.bins.size());
            // If the random bin is the least filled bin, get another random bin
            while (randomBin == leastFilledBin) {
                randomBin = (int) (Math.random() * this.bins.size());
            }
            // Get the smallest item from the random bin
            smallestItem = this.bins.get(randomBin).stream().mapToInt(Integer::intValue).min().getAsInt();
            System.out.println("Smallest item: " + smallestItem);
            if(Math.random() < 0.3) {
                return false;
            }
            // Ensure that the largest item can fit in the random bin after the smallest item is removed
        } while (this.capacity - this.bins.get(randomBin).stream().mapToInt(Integer::intValue).sum() + smallestItem < largestItem);
        
        
        // If the largest item is larger than the smallest item, swap the items
        if (largestItem > smallestItem) {
            this.bins.get(leastFilledBin).remove(largestItem);
            this.bins.get(leastFilledBin).add(smallestItem);
            this.bins.get(randomBin).remove(smallestItem);
            this.bins.get(randomBin).add(largestItem);
            return true;
        } else {
            return false;
        }
    }
}