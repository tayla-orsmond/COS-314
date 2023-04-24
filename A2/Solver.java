
//Tayla Orsmond u21467456
//Parent Solver class to solve instances of the knapsack problem
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class Solver {
    protected String instanceName;

    protected int capacity;
    protected int numItems;
    protected ArrayList<Item> items;

    protected Double optimalFitness;
    protected Double bestFitness;
    protected Boolean[] bestSolution;


    protected ArrayList<String> summaries;
    protected AtomicLong time;

    // Constructors
    public Solver() {
        this.items = new ArrayList<>();
        this.summaries = new ArrayList<>();
        this.time = new AtomicLong(0);
    }

    // Setters
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setOptimal(Double optimalFitness) {
        this.optimalFitness = optimalFitness;
    }

    public void setItems(ArrayList<String> data) {
        //First line is item count and capacity respectively
        this.numItems = Integer.parseInt(data.get(0).split(" ")[0]);
        this.capacity = Integer.parseInt(data.get(0).split(" ")[1]);
        data.remove(0);
        //Loop through the rest of the data and add the items to the arraylist (value, weight)
        for (int i = 2; i < data.size(); i++) {
            String[] itemData = data.get(i).split(" ");
            this.items.add(new Item(Double.parseDouble(itemData[0]), Double.parseDouble(itemData[1])));
        }
    }

    // Getters
    public String getInstanceName() {
        return instanceName;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Double getOptimalFitness() {
        return optimalFitness;
    }

    public Double getBestFitness() {
        return bestFitness;
    }

    public Boolean[] getBestSolution() {
        return bestSolution;
    }

    public ArrayList<String> getSummaries() {
        return summaries;
    }

    public AtomicLong getTime() {
        return time;
    }

    // Helpers
    public void clear() {
        this.instanceName = null;
        this.capacity = 0;
        this.numItems = 0;
        this.optimalFitness = 0.0;
        this.bestFitness = 0.0;
        this.bestSolution = null;
        this.items.clear();
        this.time.set(0);
    }

    protected Double calculateFitness(Boolean[] individual) {
        //Calculate the fitness of the individual
        Double fitness = 0.0;
        Double weight = 0.0;
        for (int i = 0; i < individual.length; i++) {
            if (individual[i]) {
                fitness += items.get(i).getValue();
                weight += items.get(i).getWeight();
            }
        }
        //Check if the individual is valid
        if (weight > capacity) {
            fitness = 0.0;
        }
        //Check if the individual is the best
        if (fitness > bestFitness) {
            bestFitness = fitness;
            bestSolution = individual;
        }
        return fitness;
    }

    protected Boolean[] createRandomIndividual() {
        // Create a new random individual
        Boolean[] individual = new Boolean[this.numItems];
        for (int i = 0; i < this.numItems; i++) {
            individual[i] = Math.random() < 0.5;
        }
        return individual;
    }

    // Write results for the instance to file and console
    public void writeResults(String path) {
        String res = "";
        String summary = "";

        res += "Instance: " + this.instanceName;
        summary += "\n" + this.instanceName + ":";

        res += "\nBest Fitness: " + this.bestFitness + " / Optimal: " + this.optimalFitness;
        summary += this.bestFitness + ":" + this.optimalFitness + ":";

        if(this.bestFitness == this.optimalFitness){
            res += " (Optimal)";
            summary += "Optimal";
        } else {
            res += " (Not Optimal)";
            summary += "Not Optimal";
        }

        res += "\nBest Solution: " + Arrays.toString(this.bestSolution);
        res += "\n[";
        for(int i = 0; i < this.numItems; i++){
            if(this.bestSolution[i]){
                res += "\n\t( " + this.items.get(i).getValue() + ", " + this.items.get(i).getWeight() + " )";
            }
        }
        res += "\n]";

        res += "\nTime: " + this.time + "ms";
        summary += ":" + this.time + "ms";


        // Add the summary to the arraylist
        this.summaries.add(summary);
        // Print to console
        System.out.println(summary + " Full version available in file: " + path);

        // Write to file
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
            writer.write(res);
            // Close the file writer
            writer.close();

        } catch (IOException e) {
            System.out.println("[Solver] Error writing to file " + path + ": ");
            e.printStackTrace();
        }
    }
    
    // Write the summaries for the algorithm to file
    public void summarize(String path, String algorithm){
        String res = "";
        int totalOptimal = 0;
        Long totalTime = 0L;

        res += "\nAlgorithm: " + algorithm;
        res += "\nInstance : Best Fitness : Optimal Fitness : Optimal? : Time";
        res += "--------------------------------------------------------------";

        for(String summary : this.summaries){
            res += summary;
            String[] splitSummary = summary.split(":");
            if(splitSummary[3].equals("Optimal")){
                totalOptimal++;
            }
            totalTime += Long.parseLong(splitSummary[4].replace("ms", ""));
        }

        res += "\n--------------------------------------------------------------";
        res += "\nTotal Instances: " + this.summaries.size();
        res += "\nTotal Optimal: " + totalOptimal + " / " + this.summaries.size();
        res += "\n % Optimal: " + (totalOptimal / this.summaries.size()) * 100 + "%";
        res += "\nTotal Time: " + totalTime + "ms";
        res += "\nAverage Time: " + totalTime / this.summaries.size() + "ms";

        // Write to file
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
            writer.write(res);
            // Close the file writer
            writer.close();

        } catch (IOException e) {
            System.out.println("[Solver] Error writing to file " + path + ": ");
            e.printStackTrace();
        } finally {
            System.out.println("Summary available in file: " + path);
        }
    }
}
