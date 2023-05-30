// Tayla Orsmond u21467456
// Main

import java.util.ArrayList;
import java.util.Random;

import ann.ANN;
import gp.DecisionNode;
import gp.GP;

public class Main {    
    public static void main(String[] args) {
        System.out.println("========== COS 314 - Assignment 3 - ANNs & Decision Trees ==========");
        System.out.println("==========                Tayla Orsmond u21467456         ==========");
        // Load the data
        System.out.println("[Main] Loading data...");
        Loader loader = new Loader();
        ArrayList<String> data = new ArrayList<String>();
        try{
            data = loader.readFile("breast-cancer.data");
        } catch (Exception e) {
            System.out.println("[Main]: Error reading file");
            return;
        }

        // Training set size
        final int TRAINING_SIZE = (int) Math.floor(0.7 * 286); // 70% of the data
        // ========== Random seed
        final int seed = Math.abs((int) System.currentTimeMillis()); 
        Random rng = new Random(seed);
        // ==========
        // Preprocess the data
        System.out.println("[Main] Preprocessing data...");
        System.out.println("Training set size: " + TRAINING_SIZE + "/286 (" + Math.floor(TRAINING_SIZE / 286.0 * 100)  + "%)");
        Preprocessor preprocessor = new Preprocessor(data);
        preprocessor.encodeData();
        preprocessor.splitData(TRAINING_SIZE, rng);

        // Run the ANN
        //runANN(preprocessor, rng, seed);

        // System.out.println("\nPress enter to run GP...");
        // try {
        //     System.in.read();
        // } catch (Exception e) {
        //     System.out.println("Error: " + e);
        // }  

        runGP(preprocessor, rng, seed);
    }

    private static void runANN(Preprocessor preprocessor, Random rng, int seed){
        // Hyperparameters
        final double LEARNING_RATE = 0.07;
        final int MAX_EPOCHS = 50;
        final double ERROR_TOLERANCE = 0.1;
        final int NO_IMP_EPOCHS = 5;

        // Train the network
        System.out.println("==================================================");
        System.out.println("Learning Rate: " + LEARNING_RATE);
        System.out.println("Max Epochs: " + MAX_EPOCHS);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS);
        System.out.println("Seed: " + seed);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training network... ===\n");

        ANN ann = new ANN(rng, LEARNING_RATE, MAX_EPOCHS, ERROR_TOLERANCE, NO_IMP_EPOCHS);
        ann.trainNetwork(preprocessor.getTrainingSet());
        //Test the network
        System.out.println("\n=== [M] Testing network... ===\n");
        ann.testNetwork(preprocessor.getTestingSet());
    }

    private static void runGP(Preprocessor preprocessor, Random rng, int seed){
        // Hyperparameters
        final int MAX_DEPTH = 5;
        final int TOURNAMENT = 10;
        final double MUTATION_RATE = 0.2;
        final double CROSSOVER_RATE = 0.8;
        final double ERROR_TOLERANCE = 0.01;
        final int NO_IMP_EPOCHS = 10;

        // Train the GP
        System.out.println("==================================================");
        System.out.println("Max Depth: " + MAX_DEPTH);
        System.out.println("Tournament Size: " + TOURNAMENT);
        System.out.println("Mutation Rate: " + MUTATION_RATE);
        System.out.println("Crossover Rate: " + CROSSOVER_RATE);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS);
        System.out.println("Seed: " + seed);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training GP (evolving trees)... ===\n");
        GP gp = new GP(rng, MAX_DEPTH, TOURNAMENT, MUTATION_RATE, CROSSOVER_RATE, ERROR_TOLERANCE, NO_IMP_EPOCHS);
        gp.evolve(preprocessor.getTrainingSetText());
        DecisionNode bestTree = gp.getBest();

        // Test the GP
        System.out.println("\n=== [M] Testing GP (with best tree)... ===\n");
        double accuracy = gp.test(bestTree, preprocessor.getTestingSetText());
        System.out.println("Accuracy: "+ accuracy * 100 + "%");
    }
}
