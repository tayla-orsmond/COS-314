// Tayla Orsmond u21467456
// Tabu Search class to solve the bin packing problem
// TS Pseudocode:
// Set x = x0 //Initial candidate solution
// Set length(L) = z; //Maximum tabu list length
// Set L = {}; //Initialise tabu list

// repeat
// 	Generate a random neighbor x',
// 		if x' [not in set] L then
// 			if length(L) > z then
// 				Remove oldest solution from L //FIFO queue
// 				Set x' [to be in set] L
// 			end if
// 		end if
		
// 		if x' < x then
// 			x = x',
// 		end if
// until (stopping criteria satisfied)

// return x

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class TabuSearch extends Solver {
    public TabuSearch() {
        super();
    }
    // Algorithm:
    // 1. Order the items in descending order.
    // 2. Pack the items in the bins using best fit OR better fit algo. 
    //      (pack the item in the bin that results in the least space left after packing)
    //      - Record initial solution
    //      - Set Tabu List length
    // 3. *Search for the least filled bin & attempt to empty it by taking those items and repacking them in other bins
    // 4. *Search for the least filled bin & attempt to swap an item from that bin with an item from a random bin
    //      (swap if possible and if the random bin is better filled i.e., leastfilledItem > randomItem)
    //      - If not possible swap with two items from the random bin (if possible and if the random bin is better filled)
    // 5. *Pick a random bin and attempt to empty it by taking those items and repacking them in other bins
    //      - This can be biased towards the back half of the bins since the data is ordered
    //      - Otherwise pick a random bin
    //      - *Can pick x random bins
    // 6. *Pick a random bin and swap it with an item from another random bin if the second random bin is better filled
    // * Choose & Repeat as long as something happens or until a random no. threshold
    // Update TL if solution not in there
    // * Update solution if better (something happens) or if a random no. threshold
    // Record final solution 
    // Record time. 
    public void solve() {
        
    }
}