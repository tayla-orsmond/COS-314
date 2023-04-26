//Tayla Orsmond u21467456
//Genetic Algorithm class to solve instances of the knapsack problem using a genetic algorithm
/* Basic GA pseudocode
    Create initial population // randomly create individuals
    Calculate fitness of all individuals 
    while termination condition not met do {
        Select fitter individuals for reproduction
        Recombine individuals 
            // Crossover = crossover rate / probability (do we apply crossover (yes/no))
        Mutate individuals 
            // Mutation types / methods - must mutate offspring (different from others)
            // Probability tells you if you do / don't but generally you do
            // Genrally lower than crossover otherwise we get just randomness 
        Evaluate fitness of all individuals
        Generate a new population 
            // Steady-state (slow) - replace individuals 
            // OR complete overhaul of population (faster)
    } end while
    return best individual
*/
import java.util.ArrayList;

public class GA extends Solver{
    private ArrayList<Boolean[]> population;
    private ArrayList<Double> fitnesses;
    private final Double crossoverRate;
    private final Double mutationRate;
    private int populationSize;
    private int numGenerations;
    private final int numElite;
    private final int tournamentSize;

    // Constructors
    public GA() {
        super();
        this.population = new ArrayList<>();
        this.fitnesses = new ArrayList<>();
        this.crossoverRate = 0.7;
        this.mutationRate = 0.3;
        this.populationSize = items.size(); // number of items
        this.numGenerations = 10 * items.size();
        this.numElite = 1;
        this.tournamentSize = 2;
    }
    
    // Helpers
    public void clear(){
        super.clear();
        this.population.clear();
        this.fitnesses.clear();
    }

    public void setItems(ArrayList<String> data) {
        super.setItems(data);
        this.populationSize = items.size(); // number of items
        this.numGenerations = 10 * items.size();
    }

    // Create population of random individuals
    private void createPopulation() {
        for (int i = 0; i < this.populationSize; i++) {
            Boolean[] individual = createRandomIndividual();
            this.population.add(individual);
        }
    }

    //Calculate fitness of all individuals
    private void calculateFitnesses() {
        this.fitnesses.clear();
        Double fitness;
        for (Boolean[] individual : this.population) {
            fitness = calculateFitness(individual);
            this.fitnesses.add(fitness);
        }     
    }

    // Calculate average fitness of population
    private Double calculateAverageFitness(ArrayList<Boolean[]> population) {
        Double totalFitness = 0.0;
        for (Boolean[] individual : population) {
            totalFitness += calculateFitness(individual);
        }
        return totalFitness / population.size();
    }

    // Select parent for reproduction
    // Use tournament selection
    private Boolean[] selectParent() {
        // Create tournament
        ArrayList<Boolean[]> tournament = new ArrayList<>();
        ArrayList<Double> tournamentFitnesses = new ArrayList<>();
        for (int i = 0; i < this.tournamentSize; i++) {
            int randomIndex = (int) (Math.random() * this.population.size());
            tournament.add(this.population.get(randomIndex));
            tournamentFitnesses.add(this.fitnesses.get(randomIndex));
        }

        // Select fittest individual from tournament
        int bestIndex = 0;
        for (int i = 0; i < tournamentFitnesses.size(); i++) {
            if (tournamentFitnesses.get(i) > tournamentFitnesses.get(bestIndex)) {
                bestIndex = i;
            }
        }
        return tournament.get(bestIndex);
    }

    // Crossover parents
    private ArrayList<Boolean[]> crossover(Boolean[] parent1, Boolean[] parent2) {
        // Create children
        Boolean[] child1 = new Boolean[this.numItems];
        Boolean[] child2 = new Boolean[this.numItems];

        // Select crossover point
        int crossoverPoint = (int) (Math.random() * this.numItems);

        // Swap genes
        for (int i = 0; i < this.numItems; i++) {
            if (i < crossoverPoint) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            } else {
                child1[i] = parent2[i];
                child2[i] = parent1[i];
            }
        }

        // Return children
        ArrayList<Boolean[]> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children;
    }

    // Mutate child
    private void mutate(Boolean[] child) {
        // Select gene to mutate
        int geneIndex = (int) (Math.random() * this.numItems);

        // Mutate gene
        child[geneIndex] = !child[geneIndex];
    }

    // Method
    public void solve(){
        // Start timer
        this.time.set(System.currentTimeMillis());

        // Create initial population
        createPopulation();

        // Calculate fitness of all individuals
        calculateFitnesses();

        // Loop through generations
        for (int i = 0; i < this.numGenerations; i++) {
            // Select fitter individuals for reproduction
            ArrayList<Boolean[]> newPopulation = new ArrayList<>();

            // Add elite individuals to new population
            for (int j = 0; j < this.numElite; j++) {
                //find the best individual
                int bestIndex = 0;
                for (int k = 0; k < this.fitnesses.size(); k++) {
                    if (this.fitnesses.get(k) > this.fitnesses.get(bestIndex) && !newPopulation.contains(this.population.get(k))) {
                        bestIndex = k;
                    }
                }
                //add the best individual to the new population
                newPopulation.add(this.population.get(bestIndex));
            }

            // Generate children for new population
            while (newPopulation.size() < this.populationSize) {
                // Select parents
                Boolean[] parent1 = selectParent();
                Boolean[] parent2 = selectParent();

                Boolean[] child;
                Boolean[] child2;

                // Crossover parents
                if (Math.random() < this.crossoverRate) {
                    ArrayList<Boolean[]> children = crossover(parent1, parent2);
                    // split children into two children
                    child = children.get(0);
                    child2 = children.get(1);
                } else {
                    child = parent1;
                    child2 = parent2;
                }
                
                // Mutate children
                if (Math.random() < this.mutationRate) {
                    mutate(child);
                    mutate(child2);
                }

                // Add children to new population
                newPopulation.add(child);
                newPopulation.add(child2);
            }

            // Evaluate fitness of all individuals
            Double oldfitness = calculateAverageFitness(this.population);
            Double newFitness = calculateAverageFitness(newPopulation);

            // Generate a new population (generational replacement)
            if(newFitness > oldfitness){
                this.population = newPopulation;
                calculateFitnesses();
            }
        }

        // Stop timer
        this.time.set(System.currentTimeMillis() - this.time.get());
    }  

}
