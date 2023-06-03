package gp;
// Tayla Orsmond u21467456
// A class that represents a genetic program for evolving decision trees
// The GP is seeded from main and uses a random forest approach
// The GP uses the same training and test sets as the ANN

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GP {
    private DecisionNode best; // The best decision tree
    private double bestFitness; // The fitness of the best decision tree
    private ArrayList<DecisionNode> population; // The population of decision trees

    private int minDepth = 2; // The minimum depth of the decision trees
    private int maxDepth; // The maximum depth of the decision trees
    private int maxForestSize = 100; // The number of trees in the forest
    private int maxGenerations = 50; // The maximum number of generations
    private int tournamentSize; // The size of the tournament for selection

    private double crossoverRate; // The crossover rate

    private double errorTolerance; // The error tolerance
    private int noImpGens; // The number of generations to wait before stopping if no improvement is made

    Random rng; // The random number generator

    /**
     * Constructor
     * @param rng The random number generator
     * @param maxDepth The maximum depth of the decision trees
     * @param mutationRate The mutation rate
     * @param crossoverRate The crossover rate
     * @param errorTolerance The error tolerance
     * @param noImpGens The number of generations to wait before stopping if no improvement is made
     */
    public GP(Random rng, int maxDepth, int tournamentSize, double crossoverRate, double errorTolerance, int noImpGens){
        this.rng = rng;
        this.maxDepth = maxDepth;
        this.tournamentSize = tournamentSize;
        this.crossoverRate = crossoverRate;
        this.errorTolerance = errorTolerance;
        this.noImpGens = noImpGens;
        this.population = new ArrayList<DecisionNode>();
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
    public String evolve(ArrayList<String[]> trainingSet){
        // Create an initial population of programs (random forest)
        for(int i = 0; i < maxForestSize; i++){
            // Create root node
            DecisionNode root = new DecisionNode(false, rng);
            // Get the root value (taken value)
            ArrayList<String> takenValues = new ArrayList<String>();
            takenValues.add(root.getValue());
            // Create random tree
            createRandomTree(root, maxDepth, new ArrayList<>(takenValues));
            // Add tree to population
            population.add(root);
        }

        // Execute each program and establish the fitness
        for(DecisionNode tree : population){
            test(tree, trainingSet, false, false);
        }
        
        // Set the best tree and fitness
        population.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        best = population.get(0);
        bestFitness = best.getFitness();

        // while termination condition not met do
        int generation = 0;
        int noImprovement = 0;
        while(generation < maxGenerations && noImprovement < noImpGens){
            ArrayList<DecisionNode> newGeneration = new ArrayList<>();
            double oldAvgFitness =  population.stream().mapToDouble(DecisionNode::getFitness).average().getAsDouble();
            // Elitism
            newGeneration.add(best);
            population.remove(best);
            // shuffle the population
            Collections.shuffle(population, rng);

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
                test(tree, trainingSet, false, false);
            }

            // Set the best tree and fitness
            newGeneration.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
            if(newGeneration.get(0).getFitness() - bestFitness > errorTolerance){
                best = newGeneration.get(0);
                bestFitness = best.getFitness();
                noImprovement = 0;
            } else {
                noImprovement++;
            }

            // If the average fitness of the new generation is more, then replace the old generation (generational replacement)
            double avgFitness = newGeneration.stream().mapToDouble(DecisionNode::getFitness).average().getAsDouble();
            if(avgFitness >= oldAvgFitness){
                population = newGeneration;
            }

            generation++;

            //System.out.println("Generation: " + generation + " Best Fitness: " + bestFitness + " Avg Fitness: " + avgFitness + " Best Tree Depth: " + best.getTreeDepth(0));
        }
        // System.out.println("Best Tree: ");
        // best.printTree(0);
        // System.out.println();

        return test(best, trainingSet, true, false);
    }

    /**
     * Create a random decision tree using the grow method
     * @param node The root node
     * @param maxDepth The maximum depth of the tree
     * @param attributes The available attributes to use
     * @return A random decision tree
     */
    private void createRandomTree(DecisionNode node, int depth, ArrayList<String> takenValues){
        // Populate the children
        for(int j = 0; j < node.getNumChildren(); j++){
            boolean isTerminal = rng.nextDouble() < 0.5 || depth == 1;
            node.addChild(new DecisionNode(isTerminal, rng, takenValues));
        }
        // For each child that isn't a terminal node, populate its children
        for(DecisionNode child : node.getChildren()){
            if(!child.isTerminal()){
                takenValues.add(child.getValue());
                createRandomTree(child, depth -1, new ArrayList<String>(takenValues));
                takenValues.remove(child.getValue());
            }
        }
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
        // find the depth of the smaller tree
        int treeDepth = Math.min(parent1.getTreeDepth(0), parent2.getTreeDepth(0));
        // Select a random depth for the crossover point
        if((treeDepth - minDepth) <= 0){
            treeDepth = maxDepth;
        }
        int depth = rng.nextInt(treeDepth - minDepth) + minDepth;
        // Traverse the first parent to the crossover point
        DecisionNode node1 = parent1;
        int index1 = 0;
        DecisionNode p1 = parent1;
        for(int i = 0; i < depth && !node1.isTerminal(); i++){
            p1 = node1;
            index1 = rng.nextInt(node1.getNumChildren());
            node1 = node1.getChild(index1);
        }
        // Traverse the second parent to the crossover point
        DecisionNode node2 = parent2;
        int index2 = 0;
        DecisionNode p2 = parent2;
        for(int i = 0; i < depth && !node2.isTerminal(); i++){
            p2 = node2;
            index2 = rng.nextInt(node2.getNumChildren());
            node2 = node2.getChild(index2);
        }
        // Swap the children at the crossover point
        p1.addChild(node2, index1);
        p2.addChild(node1, index2);

        // Prune the children to ensure they are valid
        parent1.prune(maxDepth, rng);
        parent2.prune(maxDepth, rng);

        return new DecisionNode[]{parent1, parent2};
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
        ArrayList<String> takenValues = new ArrayList<>();
        takenValues.add(node.getValue());
        // Traverse the parent to the mutation point
        for(int i = 0; i < depth && !node.isTerminal(); i++){
            parent = node;
            nodeIndex = rng.nextInt(node.getNumChildren());
            node = node.getChild(nodeIndex);
            // remove the attribute from the list of available attributes
            takenValues.add(node.getValue());
        }

        // Decide whether to grow or shrink the subtree
        if(rng.nextInt() < 0.5){
            // grow the subtree
            if(node.setFunctional(rng)){ // if terminal successfully changed to functional
                createRandomTree(node, maxDepth - depth, new ArrayList<>(takenValues));
            } else { // if already functional, shrink the subtree
                node.setTerminal(rng);
                node.setFunctional(rng);
                createRandomTree(node, maxDepth - depth, new ArrayList<>(takenValues));                
            }
        } else {
            // shrink the subtree
            node.setTerminal(rng);
        }
        
        // prune the child
        child.prune(maxDepth, rng);

        return child;
    }

    /**
     * Test a decision tree using the training set
     * @param tree The decision tree to test
     * @param testingSet The training set to test on
     * @param print Whether to print the results
     * @param test Whether this is the testing or training set
     */
    public String test(DecisionNode tree, ArrayList<String[]> testingSet, Boolean print, Boolean test){
        // Test the tree on the training set
        int correct = 0;
        int falsePos = 0;
        int falseNeg = 0;
        int truePos = 0;
        int trueNeg = 0;
        for(String[] instance : testingSet){
            String outputClass = tree.classify(Arrays.copyOfRange(instance, 1, instance.length));
            String targetClass = instance[0];
            if(outputClass.equals(targetClass)) {
                correct++;
                if(outputClass.equals("recurrence-events")){
                    truePos++;
                } else {
                    trueNeg++;
                }
            } else {
                if(outputClass.equals("recurrence-events")){
                    falsePos++;
                } else {
                    falseNeg++;
                }
            }
        }
        // Calculate the precision
        double precision = (double) truePos / (truePos + falsePos);
        // Calculate the recall
        double recall = (double) truePos / (truePos + falseNeg);
        // Calculate the F-measure
        double fMeasure = 2 * ((precision * recall) / (precision + recall));
        // Calculate the accuracy
        double accuracy = (double) correct / testingSet.size() * 100;
        String res = "";
        res += testingSet.size() + " \t";
        res += correct + " \t";
        res += accuracy + "% \t";
        // res += truePos + " \t" + trueNeg + " \t" + falsePos + " \t" + falseNeg + " \t";
        res += precision + " \t" + recall + " \t" + fMeasure + "\n";

        // if(print){
        //     // Print the accuracy & F-measure of the network
        //     if(test){
        //         System.out.println("[TEST SET] =======================================");
        //     } else {
        //         System.out.println("[TRAIN SET] ======================================");
        //     }
        //     System.out.println("Accuracy: " + accuracy + "%");
        //     System.out.println("Precision: " + precision);
        //     System.out.println("Recall: " + recall);
        //     System.out.println("F-Measure: " + fMeasure);
        //     System.out.println("[Correct: " + correct + "]");
        //     System.out.println("[TruePos: " + truePos + " \tTrueNeg: "+ trueNeg + "]");
        //     System.out.println("[FalsePos: " + falsePos + " \tFalseNeg: "+ falseNeg + "]");
        //     System.out.println("==================================================");
        // }

        tree.setFitness(accuracy);
        return res;
    }

}
