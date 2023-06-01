// Tayla Orsmond u21467456
// Main

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            e.printStackTrace();
            return;
        }

        // parameterTuneANN(data);
        parameterTuneGP(data);

        // // Training set size
        // final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
        // // ========== Random seed
        // final int seed = Math.abs((int) System.currentTimeMillis()); 
        // // final int seed = 1923801576;
        // Random rng = new Random(seed);
        // // ==========
        // // Preprocess the data
        // System.out.println("[Main] Preprocessing data...");
        // System.out.println("Training set size: " + TRAINING_SIZE + "/286 (" + Math.round(TRAINING_SIZE / 286.0 * 100)  + "%)");
        // Preprocessor preprocessor = new Preprocessor(data);
        // preprocessor.encodeData();
        // preprocessor.splitData(TRAINING_SIZE, rng);

        // // Run the ANN
        // runANN(preprocessor, rng, seed);

        // System.out.println("\nPress enter to run GP...");
        // try {
        //     System.in.read();
        // } catch (Exception e) {
        //     System.out.println("Error: " + e);
        // }  

        // // Run the GP
        // runGP(preprocessor, rng, seed);

        // System.out.println("\nPress enter to exit...");
        // try {
        //     System.in.read();
        // } catch (Exception e) {
        //     System.out.println("Error: " + e);
        // }  
    }

    private static void runANN(Preprocessor preprocessor, Random rng, int seed){
        // Hyperparameters
        final int MAX_EPOCHS = 50;
        final int NO_IMP_EPOCHS = 5;
        final double LEARNING_RATE = 0.07;
        final double ERROR_TOLERANCE = 0.1;
        final int NUM_HIDDEN_NODES = 9;

        // Train the network
        System.out.println("==================================================");
        System.out.println("Learning Rate: " + LEARNING_RATE);
        System.out.println("Max Epochs: " + MAX_EPOCHS);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS);
        System.out.println("Seed: " + seed);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training network... ===\n");

        ANN ann = new ANN(rng, LEARNING_RATE, MAX_EPOCHS, ERROR_TOLERANCE, NO_IMP_EPOCHS, NUM_HIDDEN_NODES);
        ann.trainNetwork(preprocessor.getTrainingSet());
        //Test the network
        System.out.println("\n=== [M] Testing network... ===\n");
        ann.testNetwork(preprocessor.getTestingSet());
    }

    private static void runGP(Preprocessor preprocessor, Random rng, int seed){
        // Hyperparameters
        final int MAX_DEPTH = 5;
        final int TOURNAMENT = 5;
        final double MUTATION_RATE = 0.2;
        final double CROSSOVER_RATE = 0.8;
        final double ERROR_TOLERANCE = 0.01;
        final int NO_IMP_EPOCHS = 5;

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
        GP gp = new GP(rng, MAX_DEPTH, TOURNAMENT, CROSSOVER_RATE, ERROR_TOLERANCE, NO_IMP_EPOCHS);
        gp.evolve(preprocessor.getTrainingSetText());
        DecisionNode bestTree = gp.getBest();

        // Test the GP
        System.out.println("\n=== [M] Testing GP (with best tree)... ===\n");
        String accuracy = gp.test(bestTree, preprocessor.getTestingSetText());
        System.out.println("Accuracy: "+ accuracy);
    }

    public static void parameterTuneANN(ArrayList<String> data){
        // HyperParameters
        int [] MAX_EPOCHS = {10, 25, 50, 100, 200, 500, 1000};
        int[] NO_IMP_EPOCHS = {1, 2, 5, 10, 20, 50, 100};
        double [] LEARNING_RATE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};
        double[] ERROR_TOLERANCE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};
        int[] NUM_HIDDEN_NODES = {1, 2, 5, 9, 15, 20, 51};

        ArrayList<String> results = new ArrayList<>();
        
        // Test each parameter one at a time over 100 runs
        // for(int i = 0; i < NUM_HIDDEN_NODES.length; i++){
            for(int j = 0; j < 100; j++){
                // Training set size
                final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
                // ================================================== Random seed
                final int seed = Math.abs((int) System.currentTimeMillis());
                Random rng = new Random(seed);
                // ==================================================
                // Preprocess the data
                Preprocessor preprocessor = new Preprocessor(data);
                preprocessor.encodeData();
                preprocessor.splitData(TRAINING_SIZE, rng);
                // Train the network
                System.out.println("==================================================");
                // System.out.println("Learning Rate: " + LEARNING_RATE[i]);
                // System.out.println("Max Epochs: " + MAX_EPOCHS[i]);
                // System.out.println("Error Tolerance: " + ERROR_TOLERANCE[i]);
                // System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS[i]);
                // System.out.println("Num Hidden Nodes: " + NUM_HIDDEN_NODES[i]);
                System.out.println("Seed: " + seed);
                // System.out.println("==================================================");
                // Write to file:
                String res = "";
                // res += "==================================================\n";
                // res += "Learning Rate: " + LEARNING_RATE[i] + "\n";
                // res += "Max Epochs: " + MAX_EPOCHS[i] + "\n";
                // res += "Error Tolerance: " + ERROR_TOLERANCE[i] + "\n";
                // res += "No Improvement Epochs: " + NO_IMP_EPOCHS[i] + "\n";
                // res += "Num Hidden Nodes: " + NUM_HIDDEN_NODES[i] + "\n";
                // res += "Seed: " + seed + "\n";
                // res += "---------------------------------------------------\n";
                res += seed + "\t";

                ANN ann = new ANN(rng, LEARNING_RATE[2], MAX_EPOCHS[2], ERROR_TOLERANCE[2], NO_IMP_EPOCHS[3], NUM_HIDDEN_NODES[2]);
                ann.trainNetwork(preprocessor.getTrainingSet());

                //Test the network
                res += ann.testNetwork(preprocessor.getTestingSet());

                results.add(res);
            }
        // }
        String path = "tuning/ann/tune_SEEDS.txt";
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
            for(String res : results){
                writer.write(res);
            }
            // Close the file writer
            writer.close();

        } catch (IOException e) {
            System.out.println("[Main] Error writing to file " + path + ": ");
            e.printStackTrace();
        }   
    }

    public static void parameterTuneGP(ArrayList<String> data){
        // HyperParameters
        final double[] CROSSOVER_RATE = {0.01, 0.1, 0.2, 0.5, 0.7, 0.9, 1.0};
        int[] NO_IMP_GENS = {1, 2, 5, 10, 20, 35, 50};
        final int [] MAX_DEPTH = {3, 4, 5, 6, 7, 8, 9};
        final int [] TOURNAMENT = {2, 5, 10, 15, 20, 50, 70};
        double[] ERROR_TOLERANCE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};


        ArrayList<String> results = new ArrayList<>();
        
        // Test each parameter one at a time over 100 runs
        // for(int i = 0; i < NO_IMP_GENS.length; i++){
            for(int j = 0; j < 100; j++){
                // Training set size
                final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
                // ================================================== Random seed
                final int seed = Math.abs((int) System.currentTimeMillis());
                Random rng = new Random(seed);
                // ==================================================
                // Preprocess the data
                Preprocessor preprocessor = new Preprocessor(data);
                preprocessor.encodeData();
                preprocessor.splitData(TRAINING_SIZE, rng);
                // Train the network
                System.out.println("==================================================");
                // System.out.println("Max Depth: " + MAX_DEPTH[i]);
                // System.out.println("Tournament Size: " + TOURNAMENT[i]);
                // System.out.println("Mutation Rate: " + MUTATION_RATE[i]);
                // System.out.println("Crossover Rate: " + CROSSOVER_RATE[i]);
                // System.out.println("Error Tolerance: " + ERROR_TOLERANCE[i]);
                // System.out.println("No Improvement Generations: " + NO_IMP_GENS[i]);
                System.out.println("Seed: " + seed);
                // System.out.println("==================================================");
                // Write to file:
                // Write to file
                String res = "";
                // res += "==================================================\n";
                // res += "Max Depth: " + MAX_DEPTH[i] + "\n";
                // res += "Tournament Size: " + TOURNAMENT[i] + "\n";
                // res += "Mutation Rate: " + MUTATION_RATE[i] + "\n";
                // res += "Crossover Rate: " + CROSSOVER_RATE[i] + "\n";
                // res += "Error Tolerance: " + ERROR_TOLERANCE[i] + "\n";
                // res += "No Improvement Epochs: " + NO_IMP_EPOCHS[i] + "\n";
                // res += "Seed: " + seed + "\n";
                // res += "---------------------------------------------------\n";
                res += seed + "\t";

                GP gp = new GP(rng, MAX_DEPTH[1], TOURNAMENT[5], CROSSOVER_RATE[6], ERROR_TOLERANCE[3], NO_IMP_GENS[4]);
                gp.evolve(preprocessor.getTrainingSetText());
                DecisionNode best = gp.getBest();

                //Test the algorithm
                res += gp.test(best, preprocessor.getTestingSetText());        

                results.add(res);
            }
        // }
        String path = "tuning/gp/tune_SEEDS.txt";
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
            for(String res : results){
                writer.write(res);
            }
            // Close the file writer
            writer.close();

        } catch (IOException e) {
            System.out.println("[Main] Error writing to file " + path + ": ");
            e.printStackTrace();
        }   
    }
}
