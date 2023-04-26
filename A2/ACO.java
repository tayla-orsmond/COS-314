
// Tayla Orsmond u21467456
// Ant Colony Optimisation class to solve instances of the knapsack problem using an ant colony optimisation algorithm
/**
 * ACO pseudocode
 * Initialize pheromone trails and parameters
    while stopping criteria not met do {
        for each ant do {
            Construct a solution using pheromone trails and heuristic information
            Evaluate the solution
            Update the pheromone trails based on the quality of the solution
        } //end for
        Update the pheromone trails globally
    } // end while
    Return the best solution found
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ACO extends Solver {
    private int numAnts;
    private int numIterations;
    private final Double alpha; // pheromone weight
    private final Double beta; // heuristic weight
    private final Double rho; // pheromone evaporation rate
    private final Double q0; // probability of choosing the best heuristic value
    private final Double tau0; // initial pheromone value
    private final Double tauMax; // maximum pheromone value
    private final Double tauMin; // minimum pheromone value

    private Double[] pheromones; // pheromone trails
    private Double[] heuristics; // heuristic information
    private Boolean[][] solutions; // solutions constructed by each ant
    private Double[] values;

    // Constructor
    public ACO(){
        super();
        this.numAnts = this.numItems;
        this.numIterations = 100 * this.numItems;
        this.alpha = 1.0;
        this.beta = 1.0;
        this.rho = 0.1;
        this.q0 = 0.9;
        this.tau0 = 0.1;
        this.tauMax = 1.0;
        this.tauMin = 0.1;
    }

    // Helpers
    public void clear(){
        super.clear();
        this.pheromones = null;
        this.heuristics = null;
        this.solutions = null;
    }

    public void setItems(ArrayList<String> data){
        super.setItems(data);
        this.numAnts = this.numItems;
        this.numIterations = 10 * this.numItems;
        this.pheromones = new Double[this.numItems];
        this.heuristics = new Double[this.numItems];
        this.solutions = new Boolean[this.numAnts][this.numItems];
        this.values = new Double[this.numAnts];
    }

    public void initialise(){
        // Initialise pheromones and heuristics
        for (int i = 0; i < this.numItems; i++){
            this.pheromones[i] = this.tau0;
            this.heuristics[i] = this.items.get(i).getValue() / this.items.get(i).getWeight();
        }

        // Initialize solutions
        for (int i = 0; i < this.numAnts; i++){
            for (int j = 0; j < this.numItems; j++){
                this.solutions[i][j] = false;
            }
        }
    }

    // Check if ant is at capacity
    public Double capacity(int ant){
        Double weight = 0.0;
        for (int i = 0; i < this.numItems; i++){
            if (this.solutions[ant][i]){
                weight += this.items.get(i).getWeight();
            }
        }
        return weight;
    }

    // Construct a solution using pheromone trails and heuristic information
    public void constructSolution(int ant){
        // Add items to solution
        for (int i = 0; i < this.numItems; i++){
            // Calculate probability of choosing item i
            Double[] probabilities = new Double[this.numItems];
            Double sum = 0.0;
            for (int j = 0; j < this.numItems; j++){
                if (!this.solutions[ant][j]){
                    probabilities[j] = Math.pow(this.pheromones[j], this.alpha) * Math.pow(this.heuristics[j], this.beta);
                    sum += probabilities[j];
                }
            }
            // Normalise probabilities
            for (int j = 0; j < this.numItems; j++){
                if (!this.solutions[ant][j]){
                    probabilities[j] /= sum;
                }
            }

            // Choose item i
            // Using roulette wheel selection
            Double q = Math.random();
            if (q < this.q0){
                // Choose item with highest probability
                Double max = 0.0;
                int index = 0;
                for(int j = 0; j < this.numItems; j++){
                    if (!this.solutions[ant][j] && capacity(ant) + this.items.get(j).getWeight() <= this.capacity){
                        if (probabilities[j] > max){
                            max = probabilities[j];
                            index = j;
                        }
                    }
                }
                if(max > 0.0){
                    this.solutions[ant][index] = true;
                }
            } else {
                // Choose item with random probability
                Double r = Math.random();
                Double cumulativeProbability = 0.0;
                for (int j = 0; j < this.numItems; j++){
                    if (!this.solutions[ant][j]){
                        cumulativeProbability += probabilities[j] / sum;
                        if (r < cumulativeProbability && capacity(ant) + this.items.get(j).getWeight() <= this.capacity){
                            this.solutions[ant][j] = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    // Evaluate the solution
    public void evaluateSolution(int ant){
        // Calculate value of solution
        Double value = calculateFitness(this.solutions[ant]);
        this.values[ant] = value;
    }

    // Update the pheromone trails based on the quality of the solution
    public void updatePheromones(int ant){
        // Update pheromones
        for (int i = 0; i < this.numItems; i++){
            if (this.solutions[ant][i]){
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.items.get(i).getValue() / this.items.get(i).getWeight();
            } else {
                this.pheromones[i] = ((1 - this.rho) * this.pheromones[i]) + this.values[ant] / this.items.get(i).getWeight() / 10;
            }
        }
    }

    // Update the pheromone trails globally
    public void updatePheromones(){
        // Find best solution
        int best = 0;
        for (int i = 0; i < this.numAnts; i++){
            if (this.values[i] > this.values[best]){
                best = i;
            }
        }

        // Update pheromones
        for (int i = 0; i < this.numItems; i++){
            if (this.solutions[best][i]){
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.values[best];
            } else {
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i];
            }
        }
    }

    // Solve the problem
    public void solve(){
        // Start timer
        this.time.set(System.currentTimeMillis());
        
        // Initialise
        this.initialise();

        // Solve
        for (int i = 0; i < this.numIterations; i++){
            for (int j = 0; j < this.numAnts; j++){
                this.constructSolution(j);
                this.evaluateSolution(j);
                this.updatePheromones(j);
            }
            this.updatePheromones();
        }

        // Find best solution
        int best = 0;
        for (int i = 0; i < this.numAnts; i++){
            if (this.values[i] > this.values[best]){
                best = i;
            }
        }

        // Set solution
        this.bestSolution = this.solutions[best];
        this.bestFitness = this.values[best];

        // Stop timer
        this.time.set(System.currentTimeMillis() - this.time.get());
    }
}
