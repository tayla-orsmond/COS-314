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
import java.util.concurrent.atomic.AtomicLong;

public class ILS extends Solver {
    public ILS(int capacity) {
        super(capacity);
    }

    public void solve () {
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        
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
        //Collections.sort(this.items, Collections.reverseOrder());
        // Pack the items into bins using the best fit heuristic (i.e., the bin with the least amount of space left)
        bestFit();
        // Store the solution as the current best solution
        setBestBins();
        this.best = this.bins.size();
        // 2. Apply a local search algorithm to search for the least-filled bin and pack the items from that bin into other bins
        Boolean repeat = false;
        do {
            do {
                int leastFilledBin = getLeastFilledBin();
                // Try to move the items from the least-filled bin to another bin/s
                tryEmptyLeastFilledBin(leastFilledBin);
                // If the least-filled bin is empty, repeat the process for the next least-filled bin
                // If the new solution is better than the current solution, update the current solution
                if (this.bins.get(leastFilledBin).isEmpty()) {
                    this.bins.remove(leastFilledBin);
                    setBestBins();
                    this.best = this.bins.size();
                    repeat = true;
                } else {
                    repeat = false;
                }
            } while (repeat);
            // 3. If the least filled bin cannot be emptied, swap an item from the least filled bin with an item in a random bin
            //Take the largest piece from the lowest filled bin, and exchange with a smaller piece from a random bin (if possible)
            int leastFilledBin = getLeastFilledBin();
            Boolean shuffled = shuffle(leastFilledBin);
            if(shuffled) {
                repeat = true;
            } else {
                repeat = false;
                // Randomly swap two items in two random bins (if possible & if the new solution is better than the current solution)
                Boolean swapped = swap();
                if(swapped) {
                    repeat = true;
                } else {
                    repeat = false;
                }
            }
            
            // If the new solution is better than the current solution, update the current solution
            if (this.bins.size() <= this.best || Math.random() < 0.4) {
                setBestBins();
                this.best = this.bins.size();
            } else { // backtrack?
                setBins();
                this.best = this.bins.size();
            }
            // Generate a random number between 0 and 1, if the number is less than 0.3, stop
            if (Math.random() < 0.3) {
                repeat = false;
            }

        } while (repeat && this.bins.size() > 1);

        //end timer
        this.time.set(System.currentTimeMillis() - start.get());
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
            if (this.bins.get(leastFilledBin).isEmpty()) {
                break;
            }
        }
    }

    // Shuffle
    private Boolean shuffle(int leastFilledBin) {
        // Take the largest piece from the lowest filled bin, and exchange with a smaller piece from a random bin (if possible)
        // Get the largest item from the least filled bin
        int largestItem = this.bins.get(leastFilledBin).stream().mapToInt(Integer::intValue).max().getAsInt();
        // Get a random bin
        int randomBin = (int) (Math.random() * this.bins.size());
        int smallestItem = 0;
        do {
            randomBin = (int) (Math.random() * this.bins.size());
            // If the random bin is the least filled bin, get another random bin
            while (randomBin == leastFilledBin) {
                randomBin = (int) (Math.random() * this.bins.size());
            }
            // Get the smallest item from the random bin
            smallestItem = this.bins.get(randomBin).stream().mapToInt(Integer::intValue).min().getAsInt();

            // Random chance to not shuffle
            if(Math.random() < 0.3) {
                return false;
            }
            //Ensure that the largest item is larger than the smallest item
        } while (smallestItem >= largestItem);
        
        // Swap the largest item from the least filled bin with the smallest item from the random bin (if possible)
        if(this.bins.get(randomBin).stream().mapToInt(Integer::intValue).sum() - smallestItem  + largestItem <= this.capacity) {
            this.bins.get(leastFilledBin).remove(largestItem);
            this.bins.get(leastFilledBin).add(smallestItem);
            this.bins.get(randomBin).remove(smallestItem);
            this.bins.get(randomBin).add(largestItem);
            return true;
        } else if (this.bins.get(randomBin).size() > 1) {
            // Swap the largest item from the least filled bin with two smaller items from the random bin
            // Get the second smallest item from the random bin
            int secondSmallestItem = Integer.MAX_VALUE;
            for (Integer item : this.bins.get(randomBin)) {
                if (item < secondSmallestItem && item != smallestItem) {
                    secondSmallestItem = item;
                }
            }
            // Ensure that the largest item can fit in the random bin after the smallest two items are removed
            if((secondSmallestItem + smallestItem < largestItem) && this.bins.get(randomBin).stream().mapToInt(Integer::intValue).sum() - smallestItem - secondSmallestItem + largestItem <= this.capacity) {
                this.bins.get(leastFilledBin).remove(largestItem);
                this.bins.get(randomBin).remove(smallestItem);
                this.bins.get(randomBin).remove(secondSmallestItem);
                this.bins.get(leastFilledBin).add(smallestItem);
                this.bins.get(leastFilledBin).add(secondSmallestItem);
                this.bins.get(randomBin).add(largestItem);
                return true;
            }
        }
        return false;
    }

    // Swap items from two random bins (if possible)
    private Boolean swap() {
        // Get two random bins
        int randomBin1 = (int) (Math.random() * this.bins.size());
        int randomBin2 = (int) (Math.random() * this.bins.size());
        // If the random bins are the same, get another random bin
        while (randomBin1 == randomBin2) {
            randomBin2 = (int) (Math.random() * this.bins.size());
        }
        // Get a random item from each bin
        int randomItem1 = this.bins.get(randomBin1).get((int) (Math.random() * this.bins.get(randomBin1).size()));
        int randomItem2 = this.bins.get(randomBin2).get((int) (Math.random() * this.bins.get(randomBin2).size()));
        
        // Swap the items if possible
        if(randomItem1 > randomItem2 && this.bins.get(randomBin2).stream().mapToInt(Integer::intValue).sum() - randomItem2 + randomItem1 <= this.capacity) {
            this.bins.get(randomBin1).remove(randomItem1);
            this.bins.get(randomBin2).remove(randomItem2);
            this.bins.get(randomBin1).add(randomItem2);
            this.bins.get(randomBin2).add(randomItem1);
            return true;
        } else if (randomItem2 > randomItem1 && this.bins.get(randomBin1).stream().mapToInt(Integer::intValue).sum() - randomItem1 + randomItem2 <= this.capacity) {
            this.bins.get(randomBin1).remove(randomItem1);
            this.bins.get(randomBin2).remove(randomItem2);
            this.bins.get(randomBin1).add(randomItem2);
            this.bins.get(randomBin2).add(randomItem1);
            return true;
        }
        return false;
    }
}