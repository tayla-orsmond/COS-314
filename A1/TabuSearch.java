// Tayla Orsmond u21467456
// Tabu Search to solve the bin packing problem

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class TabuSearch extends Solver {
    public TabuSearch(int capacity) {
        super(capacity);
    }
    public void solve() {
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        //Solve 
        ArrayList<Integer> bin = new ArrayList<Integer>();
    
        for(Integer i : items) {
            //add to bin
            if(canFit(bin, i)) {
                bin.add(i);
            } else {
                //add bin to bins
                bins.add(bin);
                //create new bin
                bin = new ArrayList<Integer>();
                //add to bin
                bin.add(i);
            }
        }
        //add last bin
        bins.add(bin);
        //set best
        best = bins.size();

        //TODO: Actual solution. this is a tester solution to see if the file reading / writing works

        //end timer
        this.time.set(System.currentTimeMillis() - start.get());
    }
}