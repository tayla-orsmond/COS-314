// Tayla Orsmond u21467456
// Iterated Local Search class to solve the bin packing problem

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class ILS extends Solver {
    public ILS() {
        super();
    }

    // Algorithm:
    // 1. Order the items in descending order.
    // 2. Pack the items in the bins using best fit OR better fit algo. 
    //      (pack the item in the bin that results in the least space left after packing)
    //      - Record initial solution
    // 3. *Search for the least filled bin & attempt to empty it by taking those items and repacking them in other bins
    //      - Repeat as long as a bin can be emptied
    //      - Record better solutions
    // 4. *Pick a random bin and attempt to empty it by taking those items and repacking them in other bins
    //      - This can be biased towards the back half of the bins since the data is ordered
    //      - Otherwise pick a random bin
    // 4. *Search for the least filled bin & attempt to swap an item from that bin with an item from a random bin
    //      (swap if possible and if the random bin is better filled i.e., leastfilledItem > randomItem)
    //      - Repeat as long as a swap can be made
    // 5. *Pick a random bin and attempt to swap an item from that bin with an item from a random bin
    //      (swap if possible and if the second random bin is better filled)
    // * Choose & Repeat as long as something happens or until a random no. threshold is reached
    // * Update solution if better (something happens) or if a random no. threshold is reached
    // Record final solution 
    // Record time.

    /**
     * Method to solve the bin packing problem using Iterated Local Search
     * @details Algorithm found in the report or in the comments above
     * @return void
     */
    public void solve(){
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        // 1.
        Collections.sort(this.items, Collections.reverseOrder()); 
        // 2.
        bestFitAll();
        setBestBins();
        this.best = this.bins.size(); //Initial best no. of bins
        // 3.-6.
        Boolean repeat = false;
        Boolean repeatOverall = false;
        do{
            repeatOverall = false;
            //Choose whether to pick a random bin or the least filled bin
            char picked;
            if(Math.random() > 0.4){
                picked = 'L'; //least-filled bin
            } else {
                picked = 'R'; //random bin
            }
            // 3. & 4. Attempt to empty the bin
            int pickedBin;
            do {
                pickedBin = getBin(picked);
                int PBSize = sizeOf(pickedBin);
                tryEmptyBin(pickedBin);
                //check if bin still exists
                try{
                    this.bins.get(pickedBin);
                } catch (IndexOutOfBoundsException e){
                    pickedBin = -1;
                }
                if (this.bins.size() < this.best || (pickedBin != -1 && sizeOf(pickedBin) < PBSize) ) {
                    setBestBins();
                    this.best = this.bins.size(); //New no.of bins
                    repeat = true;
                    repeatOverall = true;
                } else {
                    setBins(); // Backtrack
                    repeat = false;
                }
            } while(repeat);

            // 5. & 6. Attempt to swap an item from the bin with an item from a random bin (if possible)
            do {
                pickedBin = getBin(picked);
                int PBSize = sizeOf(pickedBin);
                int randomBin = (int)(Math.random() * this.bins.size());
                int RBSize = sizeOf(randomBin);
                trySwap(pickedBin, randomBin);
                //check if bin still exists
                try{
                    this.bins.get(pickedBin);
                } catch (IndexOutOfBoundsException e){
                    pickedBin = -1;
                }
                try{
                    this.bins.get(randomBin);
                } catch (IndexOutOfBoundsException e){
                    randomBin = -1;
                }
                if (this.bins.size() < this.best || (pickedBin != -1 && sizeOf(pickedBin) < PBSize) || (randomBin != -1 && sizeOf(randomBin) < RBSize) ) {
                    setBestBins();
                    this.best = this.bins.size(); //New no.of bins
                    repeat = true;
                    repeatOverall = true;
                } else {
                    setBins(); // Backtrack
                    repeat = false;
                }
            } while(repeat);
        } while(repeatOverall || Math.random() > 0.1);
        this.time.set(System.currentTimeMillis() - start.get());
    }
}