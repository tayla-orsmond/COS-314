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
    private ArrayList<ArrayList<ArrayList<Integer>>> tabuList;
    private Integer tabuListLength = 0;
    public TabuSearch() {
        super();
        this.tabuList = new ArrayList<ArrayList<ArrayList<Integer>>>();
    }

    public void clear() {
        super.clear();
        this.tabuList.clear();
    }

    public void setData(ArrayList<String> data) {
        super.setData(data);
        this.tabuListLength = this.items.size();
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
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        // 1.
        Collections.sort(this.items, Collections.reverseOrder());
        // 2.
        bestFitAll();
        setBestBins();
        this.best = this.bins.size(); //Initial best no. of bins
        // 3.-6.
        Boolean repeat = false;
        do{
            //Choose whether to pick a random bin or the least filled bin
            char picked;
            if(Math.random() < 0.6){
                picked = 'L'; //least-filled bin
            } else {
                picked = 'R'; //random bin
            }
            // 3. & 5. Attempt to empty the bin
            int pickedBin;
            do {
                pickedBin = getBin(picked);
                int PBSize = sizeOf(pickedBin);
                tryEmptyBin(pickedBin);
                //check if bins are in the tabu list
                Boolean inList = false;
                if (isTabu()) {
                    inList = true;
                    repeat = false;
                }
                if(!inList){
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
                    } else {
                        repeat = false;
                    }
                    this.tabuList.add(this.bins);
                    if(this.tabuList.size() > this.tabuListLength){
                        this.tabuList.remove(0);
                    }
                }
            } while(repeat);

            // 4. & 6. Attempt to swap an item from the bin with an item from a random bin (if possible)
            do {
                pickedBin = getBin(picked);
                int PBSize = sizeOf(pickedBin);
                int randomBin = (int)(Math.random() * this.bins.size());
                int RBSize = sizeOf(randomBin);
                trySwap(pickedBin, randomBin);
                //check if bins are in the tabu list
                Boolean inList = false;
                if (isTabu()) {
                    inList = true;
                    repeat = false;
                }
                if(!inList){
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
                    } else {
                        repeat = false;
                    }
                    this.tabuList.add(this.bins);
                    if(this.tabuList.size() > this.tabuListLength){
                        this.tabuList.remove(0);
                    }
                }
            } while(repeat);
        } while(repeat || Math.random() > 0.4);
        this.time.set(System.currentTimeMillis() - start.get());
    }

    private Boolean isTabu(){
        for(ArrayList<ArrayList<Integer>> tabu : this.tabuList){
            if(compare(tabu, this.bins)){
                return true;
            }
        }
        return false;
    }

    private Boolean compare(ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b){
        if(a.size() != b.size()){
            return false;
        }
        for(int i = 0; i < a.size(); i++){
            if(a.get(i).size() != b.get(i).size()){
                return false;
            }
            for(int j = 0; j < a.get(i).size(); j++){
                if(a.get(i).get(j) != b.get(i).get(j)){
                    return false;
                }
            }
        }
        return true;
    }
}