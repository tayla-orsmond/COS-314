package gp;
// Tayla Orsmond u21467456
// A class that represents a genetic program for evolving decision trees
// The GP is seeded from main and uses a random forest approach
// The GP uses the same training and test sets as the ANN

import java.lang.reflect.Array;

// Gp Psuedocode
/*
 * Create an initial population of programs
    Execute each program and establish the fitness
    while termination condition not met do {
        Select fitter programs to participate in reproduction
        Create new programs using genetic operators and update the population
            //crossover, mutation & reproduction
        Execute each new program and establish the fitness
    } end while
    return best program
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class GP {
    private ArrayList<String> functionalSet = new ArrayList<>();
    private ArrayList<String> terminalSet = new ArrayList<>();

    private DecisionNode best; // The best decision tree
    private double bestFitness; // The fitness of the best decision tree
    private ArrayList<DecisionNode> population; // The population of decision trees

    private int minDepth = 2; // The minimum depth of the decision trees
    private int maxDepth; // The maximum depth of the decision trees
    private int maxForestSize = 100; // The number of trees in the forest
    private int maxGenerations = 50; // The maximum number of generations
    private int tournamentSize; // The size of the tournament for selection

    private double mutationRate; // The mutation rate
    private double crossoverRate; // The crossover rate

    private double errorTolerance; // The error tolerance
    private int noImpEpochs; // The number of generations to wait before stopping if no improvement is made

    Random rng; // The random number generator

    /**
     * Constructor
     * @param rng The random number generator
     * @param maxDepth The maximum depth of the decision trees
     * @param mutationRate The mutation rate
     * @param crossoverRate The crossover rate
     * @param errorTolerance The error tolerance
     * @param noImpEpochs The number of generations to wait before stopping if no improvement is made
     */
    public GP(Random rng, int maxDepth, int tournamentSize, double mutationRate, double crossoverRate, double errorTolerance, int noImpEpochs){
        this.rng = rng;
        this.maxDepth = maxDepth;
        this.tournamentSize = tournamentSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.errorTolerance = errorTolerance;
        this.noImpEpochs = noImpEpochs;
        this.population = new ArrayList<DecisionNode>();

        // Add the functional set
        functionalSet.add("age");
        functionalSet.add("menopause");
        functionalSet.add("tumor-size");
        functionalSet.add("inv-nodes");
        functionalSet.add("node-caps");
        functionalSet.add("deg-malig");
        functionalSet.add("breast");
        functionalSet.add("breast-quad");
        functionalSet.add("irradiat");

        // Add the terminal set
        terminalSet.add("no-recurrence-events");
        terminalSet.add("recurrence-events");
    }

    // Getters
    public DecisionNode getBest(){
        return best;
    }

    public double getBestFitness(){
        return bestFitness;
    }

    /**
     * Run the GP 
     * Uses the GP algorithm to evolve a decision tree for the given training set
     * @param trainingSet The training set
     */
    public void evolve(ArrayList<String[]> trainingSet){
        // Create an initial population of programs (random forest)
        for(int i = 0; i < maxForestSize; i++){
            // Deep Copy the functional set
            ArrayList<String> attributes = new ArrayList<String>();
            for(String attribute : functionalSet){
                attributes.add(attribute);
            }
            // Select random attribute from functional set
            int index = rng.nextInt(attributes.size());
            // Create root node
            DecisionNode root = new DecisionNode(attributes.get(index), false);
            // Remove attribute from functional set
            attributes.remove(index);
            // Populate all children
            for(int j = 0; j < root.getNumChildren(); j++){
                if(maxDepth == 1 || rng.nextDouble() < 0.5){
                    // Select a random attribute from the terminal set
                    root.addChild(new DecisionNode(terminalSet.get(rng.nextInt(terminalSet.size())), true));
                } else {
                    root.addChild(new DecisionNode(attributes.get(rng.nextInt(attributes.size())), false));
                }
            }      
            // For all children that are not terminal, create the tree
            for(DecisionNode child : root.getChildren()){
                if(!child.isTerminal()){
                    createRandomTree(child, maxDepth, new ArrayList<String>(attributes));
                }
            }
            population.add(root);
        }

        // Execute each program and establish the fitness
        for(DecisionNode tree : population){
            tree.setFitness(test(tree, trainingSet));
        }
        
        // Set the best tree and fitness
        population.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        best = population.get(0);
        bestFitness = best.getFitness();

        // while termination condition not met do {
        int generation = 0;
        int noImprovement = 0;
        while(generation < maxGenerations && noImprovement < noImpEpochs){
            ArrayList<DecisionNode> newGeneration = new ArrayList<DecisionNode>();
            double oldAvgFitness =  population.stream().mapToDouble(DecisionNode::getFitness).average().getAsDouble();

            // Elitism
            newGeneration.add(best);
            population.remove(best);
            
            // shuffle the population
            Collections.shuffle(population, rng);

            // split population into those to undergo crossover and those to undergo mutation
            while(newGeneration.size() < maxForestSize){
                // Select fitter programs to participate in reproduction (tournament selection)
                DecisionNode parent1 = selectParent();
                DecisionNode parent2 = selectParent();

                // Create new programs using genetic operators and update the population
                if(rng.nextDouble() < crossoverRate){ // crossover
                    DecisionNode[] children = crossover(parent1, parent2);
                    newGeneration.add(children[0]);
                    newGeneration.add(children[1]);
                } else { // mutation
                    newGeneration.add(mutate(parent1));
                    newGeneration.add(mutate(parent2));
                }
            }

            // Execute each new program and establish the fitness
            for(DecisionNode tree : newGeneration){
                tree.setFitness(test(tree, trainingSet));
            }

            // Set the best tree and fitness
            newGeneration.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
            System.out.println("Fitnesses ");
            for(DecisionNode tree : newGeneration){
                System.out.print(tree.getFitness() + " ");
            }
            System.out.println();
            if(newGeneration.get(0).getFitness() - bestFitness > errorTolerance){
                best = newGeneration.get(0);
                bestFitness = best.getFitness();
                noImprovement = 0;
            } else {
                noImprovement++;
            }

            // If the average fitness of the new generation is more, then replace the old generation (generational replacement)
            if(newGeneration.stream().mapToDouble(DecisionNode::getFitness).average().getAsDouble() >= oldAvgFitness){
                population = newGeneration;
            }

            generation++;

            System.out.println("Generation: " + generation + " Best Fitness: " + bestFitness);
            // System.out.println("Best Tree: \n" + best.toString());
            // System.out.println("Best Tree depth: "+ best.getTreeDepth(0));
        }
    }

    /**
     * Create a random decision tree using the grow method
     * @param node The root node
     * @param maxDepth The maximum depth of the tree
     * @param attributes The available attributes to use
     * @return A random decision tree
     */
    private DecisionNode createRandomTree(DecisionNode node, int maxDepth, ArrayList<String> attributes){
        // If the max depth has been reached, return the node
        if(maxDepth <= 1){
            return node;
        }
        // Remove the node's attribute from the available attributes
        attributes.remove(node.getValue());
        
        // Populate all children
        for(int j = 0; j < node.getNumChildren(); j++){
            if(maxDepth == 2 || rng.nextDouble() < 0.5){
                // Select a random attribute from the terminal set
                node.addChild(new DecisionNode(terminalSet.get(rng.nextInt(terminalSet.size())), true));
            } else {
                node.addChild(new DecisionNode(attributes.get(rng.nextInt(attributes.size())), false));
            }
        }      
        // For all children that are not terminal, create the tree
        for(DecisionNode child : node.getChildren()){
            if(!child.isTerminal()){
                createRandomTree(child, maxDepth-1, new ArrayList<String>(attributes));
            }
        }

        return node;
    }
    

    /**
     * Select a parent using tournament selection
     * @return The selected parent
     */
    private DecisionNode selectParent(){
        // Select random programs from the population
        ArrayList<DecisionNode> tournament = new ArrayList<DecisionNode>();
        for(int i = 0; i < tournamentSize; i++){
            tournament.add(population.get(rng.nextInt(population.size())));
        }
        // Return the fittest program
        tournament.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        return tournament.get(0);
    }

    /**
     * Crossover two parents to produce two children using the subtree crossover method
     * @param parent1 The first parent
     * @param parent2 The second parent
     * @return The two children
     */
    private DecisionNode[] crossover(DecisionNode parent1, DecisionNode parent2){
        // Save the roots of the parents
        DecisionNode[] children = new DecisionNode[2];
        children[0] = parent1;
        children[1] = parent2;

        // Select a random node from each parent
        DecisionNode node1 = parent1; 
        int node1Index = 0;
        DecisionNode node2 = parent2;
        int node2Index = 0;

        // Select a random depth for the crossover point
        int depth = rng.nextInt(maxDepth - minDepth) + minDepth;
        // Traverse the parents to the crossover point
        for(int i = 1; i < depth && !node1.isTerminal(); i++){
            parent1 = node1;
            node1Index = rng.nextInt(node1.getNumChildren());
            node1 = node1.getChild(node1Index);
        }
        for(int i = 1; i < depth && !node2.isTerminal(); i++){
            parent2 = node2;
            node2Index = rng.nextInt(node2.getNumChildren());
            node2 = node2.getChild(node2Index);
        }

        // Swap the subtrees
        parent1.addChild(node2, node1Index);
        parent2.addChild(node1, node2Index);

        // prune the children
        children[0].prune(maxDepth, rng);
        children[1].prune(maxDepth, rng);

        return children;
    }

    /**
     * Mutate a parent using the grow / shrink mutation method
     * @param parent The parent to mutate
     * @return The mutated child
     */
    private DecisionNode mutate(DecisionNode parent){
        // Save the root of the parent
        DecisionNode child = parent;
        // Select a random node from the parent
        DecisionNode node = parent;
        int nodeIndex = 0;

        // Select a random depth for the mutation point
        int depth = rng.nextInt(maxDepth - minDepth) + minDepth;
        // Deep Copy the functional set
        ArrayList<String> attributes = new ArrayList<String>();
        for(String attribute : functionalSet){
            attributes.add(attribute);
        }
        // Traverse the parent to the mutation point
        for(int i = 1; i < depth && !node.isTerminal(); i++){
            parent = node;
            nodeIndex = rng.nextInt(node.getNumChildren());
            node = node.getChild(nodeIndex);
            // remove the attribute from the list of available attributes
            attributes.remove(node.getValue());
        }

        // Decide whether to grow or shrink the subtree
        if(rng.nextInt() < 0.5){
            // Grow the subtree
            parent.setTerminal(true);
            DecisionNode subtreeRoot = new DecisionNode(attributes.get(rng.nextInt(attributes.size())), false);
            createRandomTree(subtreeRoot, maxDepth - depth, attributes);
            parent.addChild(subtreeRoot, nodeIndex);
        } else {
            // Shrink the subtree
            parent.addChild(new DecisionNode(terminalSet.get(rng.nextInt(terminalSet.size())), true), nodeIndex);
        }

        // prune the child
        child.prune(maxDepth, rng);

        return child;
    }

    /**
     * Test a decision tree using the training set
     * @param tree The decision tree to test
     * @param trainingSet The training set to test the tree on
     * @return The fitness of the tree
     */
    public double test(DecisionNode tree, ArrayList<String[]> trainingSet){
        // Test the tree on the training set
        int correct = 0;
        for(String[] instance : trainingSet){
            if(tree.classify(Arrays.copyOfRange(instance, 1, instance.length)).compareTo(instance[0]) == 0){
                correct++;
            }
        }
        return (double)correct / trainingSet.size();
    }

}
