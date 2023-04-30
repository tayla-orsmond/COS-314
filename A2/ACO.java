
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
    private final int numAnts;
    private final int numIterations;
    private final Double alpha; // pheromone weight (1 > alpha > 0)
    private final Double beta; // heuristic weight (1 > beta > 0)
    private final Double rho; // pheromone evaporation rate (1 > rho > 0)
    private final Double tau0; // initial pheromone value
    private final Double tauMax; // maximum pheromone value 
    private final Double tauMin; // minimum pheromone value

    private Double[] pheromones; // pheromone trails
    private Double[] heuristics; // heuristic information
    private Boolean[][] solutions; // solutions constructed by each ant
    private Double[] fitnesses; // fitnesses of each solution

    // Constructor
    public ACO(){
        super();
        this.numAnts = 100;
        this.numIterations = 200;
        this.alpha = 0.4;
        this.beta = 0.5;
        this.rho = 0.5;
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
        this.pheromones = new Double[this.numItems];
        this.heuristics = new Double[this.numItems];
        this.solutions = new Boolean[this.numAnts][this.numItems];
        this.fitnesses = new Double[this.numAnts];
    }

    public void initialise(){
        // Initialise pheromones and heuristics
        for (int i = 0; i < this.numItems; i++){
            this.pheromones[i] = this.tau0;
            this.heuristics[i] = this.items.get(i).getValue() / this.items.get(i).getWeight();
        }
        // Initialise solutions
        for (int i = 0; i < this.numAnts; i++){
            Arrays.fill(this.solutions[i], false);
        }
    }

    // Check if ant is at capacity
    public Double weight(int ant){
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
        // Initialise solution
        Arrays.fill(this.solutions[ant], false);
        // Start at a random item
        int randy = (int) (Math.random() * this.numItems);
        this.solutions[ant][randy] = true;

        // Add items to solution
        for (int i = 0; i < this.numItems && this.weight(ant) < this.capacity; i++){
            // (Re-)Calculate probability of adding item
            Double[] probabilities = new Double[this.numItems];
            Double sum = 0.0;
            for (int j = 0; j < this.numItems; j++){
                if (!this.solutions[ant][j] && this.weight(ant) + this.items.get(j).getWeight() <= this.capacity){
                    probabilities[j] = Math.pow(this.pheromones[j], this.alpha) * Math.pow(this.heuristics[j], this.beta);
                    sum += probabilities[j];
                } else {
                    probabilities[j] = 0.0;
                }
            }
            // Normalise probabilities
            for (int j = 0; j < this.numItems && sum != 0; j++){
                probabilities[j] /= sum;
            }
            // Choose item to add
            // Using roulette wheel selection
            Double rand = Math.random();
            Double cumulative = 0.0;
            for (int j = 0; j < this.numItems; j++){
                cumulative += probabilities[j];
                if (rand < cumulative){
                    this.solutions[ant][j] = true;
                    break;
                }
            }
        }
    }

    // Evaluate the solution
    public void evaluateSolution(int ant){
        // Calculate value of solution
        Double value = calculateFitness(this.solutions[ant]);
        this.fitnesses[ant] = value;
    }

    // Update the pheromone trails based on the quality of the solution
    public void updatePheromones(int ant){
        // Update pheromones
        for (int i = 0; i < this.numItems; i++){
            if (this.solutions[ant][i]){
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.fitnesses[ant];
            } else {
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.fitnesses[ant] / 5;
            }
        }
    }

    // Update the pheromone trails globally
    public void updatePheromones(){
        // Find best solution
        int best = 0;
        for (int i = 0; i < this.numAnts; i++){
            if (this.fitnesses[i] > this.fitnesses[best]){
                best = i;
            }
        }
        // Update pheromones
        for (int i = 0; i < this.numItems; i++){
            if (this.solutions[best][i]){
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.fitnesses[best];
            } else {
                this.pheromones[i] = (1 - this.rho) * this.pheromones[i] + this.fitnesses[best] / 5;
            }
            // Make sure pheromones are within bounds
            if (this.pheromones[i] < this.tauMin){
                this.pheromones[i] = this.tauMin;
            } else if (this.pheromones[i] > this.tauMax){
                this.pheromones[i] = this.tauMax;
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
            if (this.fitnesses[i] > this.fitnesses[best]){
                best = i;
            }
        }

        // Set solution
        this.bestSolution = this.solutions[best];
        this.bestFitness = this.fitnesses[best];

        // Stop timer
        this.time.set(System.currentTimeMillis() - this.time.get());
    }
}
