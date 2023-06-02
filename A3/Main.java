// Tayla Orsmond u21467456
// Main

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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

        // Training set size
        final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
        ArrayList<String> results = new ArrayList<>();
        results.add("Seed: \t GP Performance[train]: \t GP Performance[test]: \t ANN Performance[train]: \t ANN Performance[test]:\n");

        for(int i = 0; i < 100; i++){
            // ========== Random seed
            final int seed = (int) Math.abs(System.currentTimeMillis());
            Random rng = new Random(seed);
            String res = seed + " \t";
            AtomicBoolean yes = new AtomicBoolean(false);
            // final int ANNseed = 1994978327;
            // final int GPseed = 2019712204;
            // Random ANNrng = new Random(ANNseed);
            // Random GPrng = new Random(GPseed);
            // ==========
            // Preprocess the data
            // System.out.println("[Main] Preprocessing data...");
            // System.out.println("Training set size: " + TRAINING_SIZE + "/286 (" + Math.round(TRAINING_SIZE / 286.0 * 100)  + "%)");
            System.out.println("Seed: " + seed);
            Preprocessor preprocessor = new Preprocessor(data);
            preprocessor.encodeData();
            preprocessor.splitData(TRAINING_SIZE, rng);
            if(!yes.getAndSet(true)){
                res += runGP(preprocessor, rng);
            }
            if(yes.getAndSet(false)){
                res += runANN(preprocessor, rng);
            }
            res += "\n";
            results.add(res);
        }

        String path = "tuning/tune_SEEDS.txt";
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
        
        // System.out.println("[1] : Run ANN" + "\n[2] : Run GP" + "\n[3] : Exit"); 
        // int choice = 0;
        // Scanner sc = new Scanner(System.in);
        // choice = sc.nextInt();
        // if(choice == 1){
        //     runANN(preprocessor, rng);
        // } else if(choice == 2){
        //     runGP(preprocessor, rng);
        // } else {
        //     System.out.println("Invalid choice");
        // }
        // System.out.println("Exiting...");
    }

    private static String runANN(Preprocessor preprocessor, Random rng){
        // Hyperparameters
        final int MAX_EPOCHS = 25;
        final int NO_IMP_EPOCHS = 10;
        final double LEARNING_RATE = 0.05;
        final double ERROR_TOLERANCE = 0.05;
        final int NUM_HIDDEN_NODES = 5;
        String result = "";
        // Train the network
        // System.out.println("ANN ==============================================");
        // System.out.println("Learning Rate: " + LEARNING_RATE);
        // System.out.println("Max Epochs: " + MAX_EPOCHS);
        // System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        // System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS);
        // System.out.println("Seed: " + seed);
        // System.out.println("==================================================");
        // System.out.println("\n === [M] Training network... ===\n");

        ANN ann = new ANN(rng, LEARNING_RATE, MAX_EPOCHS, ERROR_TOLERANCE, NO_IMP_EPOCHS, NUM_HIDDEN_NODES);
        result += ann.trainNetwork(preprocessor.getTrainingSet());
        //Test the network
        // System.out.println("\n=== [M] Testing network... ===\n");
        result += ann.testNetwork(preprocessor.getTestingSet());
        return result;
    }

    private static String runGP(Preprocessor preprocessor, Random rng){
        // Hyperparameters
        final int MAX_DEPTH = 4;
        final int TOURNAMENT = 50;
        final double CROSSOVER_RATE = 1.0;
        final double ERROR_TOLERANCE = 0.05;
        final int NO_IMP_GENS = 20;
        String result = "";
        // Train the GP
        // System.out.println("GP ===============================================");
        // System.out.println("Max Depth: " + MAX_DEPTH);
        // System.out.println("Tournament Size: " + TOURNAMENT);
        // System.out.println("Crossover Rate: " + CROSSOVER_RATE);
        // System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        // System.out.println("No Improvement Generations: " + NO_IMP_GENS);
        // System.out.println("Seed: " + seed);
        // System.out.println("==================================================");
        // System.out.println("\n === [M] Training GP (evolving trees)... ===\n");
        GP gp = new GP(rng, MAX_DEPTH, TOURNAMENT, CROSSOVER_RATE, ERROR_TOLERANCE, NO_IMP_GENS);
        result += gp.evolve(preprocessor.getTrainingSetText());
        DecisionNode bestTree = gp.getBest();

        // Test the GP
        // System.out.println("\n=== [M] Testing GP (with best tree)... ===\n");
        result += gp.test(bestTree, preprocessor.getTestingSetText(), true, true);
        return result;
    }

    // public static void parameterTuneANN(ArrayList<String> data){
    //     // HyperParameters
    //     int [] MAX_EPOCHS = {10, 25, 50, 100, 200, 500, 1000};
    //     int[] NO_IMP_EPOCHS = {1, 2, 5, 10, 20, 50, 100};
    //     double [] LEARNING_RATE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};
    //     double[] ERROR_TOLERANCE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};
    //     int[] NUM_HIDDEN_NODES = {1, 2, 5, 9, 15, 20, 51};

    //     ArrayList<String> results = new ArrayList<>();
        
    //     // Test each parameter one at a time over 100 runs
    //     // for(int i = 0; i < NUM_HIDDEN_NODES.length; i++){
    //         for(int j = 0; j < 100; j++){
    //             // Training set size
    //             final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
    //             // ================================================== Random seed
    //             final int seed = Math.abs((int) System.currentTimeMillis());
    //             Random rng = new Random(seed);
    //             // ==================================================
    //             // Preprocess the data
    //             Preprocessor preprocessor = new Preprocessor(data);
    //             preprocessor.encodeData();
    //             preprocessor.splitData(TRAINING_SIZE, rng);
    //             // Train the network
    //             System.out.println("==================================================");
    //             // System.out.println("Learning Rate: " + LEARNING_RATE[i]);
    //             // System.out.println("Max Epochs: " + MAX_EPOCHS[i]);
    //             // System.out.println("Error Tolerance: " + ERROR_TOLERANCE[i]);
    //             // System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS[i]);
    //             // System.out.println("Num Hidden Nodes: " + NUM_HIDDEN_NODES[i]);
    //             System.out.println("Seed: " + seed);
    //             // System.out.println("==================================================");
    //             // Write to file:
    //             String res = "";
    //             // res += "==================================================\n";
    //             // res += "Learning Rate: " + LEARNING_RATE[i] + "\n";
    //             // res += "Max Epochs: " + MAX_EPOCHS[i] + "\n";
    //             // res += "Error Tolerance: " + ERROR_TOLERANCE[i] + "\n";
    //             // res += "No Improvement Epochs: " + NO_IMP_EPOCHS[i] + "\n";
    //             // res += "Num Hidden Nodes: " + NUM_HIDDEN_NODES[i] + "\n";
    //             // res += "Seed: " + seed + "\n";
    //             // res += "---------------------------------------------------\n";
    //             res += seed + "\t";

    //             ANN ann = new ANN(rng, LEARNING_RATE[2], MAX_EPOCHS[2], ERROR_TOLERANCE[2], NO_IMP_EPOCHS[3], NUM_HIDDEN_NODES[2]);
    //             ann.trainNetwork(preprocessor.getTrainingSet());

    //             //Test the network
    //             res += ann.testNetwork(preprocessor.getTestingSet());

    //             results.add(res);
    //         }
    //     // }
    //     String path = "tuning/ann/tune_SEEDS.txt";
    //     try {
    //         // Create a new file
    //         File file = new File(path);
    //         // If the file doesn't exist, create it
    //         if (!file.exists()) {
    //             file.createNewFile();
    //         }
    //         // Create a new file writer
    //         FileWriter writer = new FileWriter(file);
    //         // Write the results to the file
    //         for(String res : results){
    //             writer.write(res);
    //         }
    //         // Close the file writer
    //         writer.close();

    //     } catch (IOException e) {
    //         System.out.println("[Main] Error writing to file " + path + ": ");
    //         e.printStackTrace();
    //     }   
    // }

    // public static void parameterTuneGP(ArrayList<String> data){
    //     // HyperParameters
    //     final double[] CROSSOVER_RATE = {0.01, 0.1, 0.2, 0.5, 0.7, 0.9, 1.0};
    //     int[] NO_IMP_GENS = {1, 2, 5, 10, 20, 35, 50};
    //     final int [] MAX_DEPTH = {3, 4, 5, 6, 7, 8, 9};
    //     final int [] TOURNAMENT = {2, 5, 10, 15, 20, 50, 70};
    //     double[] ERROR_TOLERANCE = {0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.7};


    //     ArrayList<String> results = new ArrayList<>();
        
    //     // Test each parameter one at a time over 100 runs
    //     // for(int i = 0; i < ERROR_TOLERANCE.length; i++){
    //         for(int j = 0; j < 100; j++){
    //             // Training set size
    //             final int TRAINING_SIZE = (int) Math.round(0.7 * 286); // 70% of the data
    //             // ================================================== Random seed
    //             final int seed = Math.abs((int) System.currentTimeMillis());
    //             Random rng = new Random(seed);
    //             // ==================================================
    //             // Preprocess the data
    //             Preprocessor preprocessor = new Preprocessor(data);
    //             preprocessor.encodeData();
    //             preprocessor.splitData(TRAINING_SIZE, rng);
    //             // Train the network
    //             System.out.println("==================================================");
    //             // System.out.println("Max Depth: " + MAX_DEPTH[i]);
    //             // System.out.println("Tournament Size: " + TOURNAMENT[i]);
    //             // System.out.println("Mutation Rate: " + MUTATION_RATE[i]);
    //             // System.out.println("Crossover Rate: " + CROSSOVER_RATE[i]);
    //             // System.out.println("Error Tolerance: " + ERROR_TOLERANCE[i]);
    //             // System.out.println("No Improvement Generations: " + NO_IMP_GENS[i]);
    //             System.out.println("Seed: " + seed);
    //             // System.out.println("==================================================");
    //             // Write to file:
    //             // Write to file
    //             String res = "";
    //             // res += "==================================================\n";
    //             // res += "Max Depth: " + MAX_DEPTH[i] + "\n";
    //             // res += "Tournament Size: " + TOURNAMENT[i] + "\n";
    //             // res += "Mutation Rate: " + MUTATION_RATE[i] + "\n";
    //             // res += "Crossover Rate: " + CROSSOVER_RATE[i] + "\n";
    //             // res += "Error Tolerance: " + ERROR_TOLERANCE[i] + "\n";
    //             // res += "No Improvement Epochs: " + NO_IMP_EPOCHS[i] + "\n";
    //             // res += "Seed: " + seed + "\n";
    //             // res += "---------------------------------------------------\n";
    //             res += seed + "\t";

    //             GP gp = new GP(rng, MAX_DEPTH[1], TOURNAMENT[5], CROSSOVER_RATE[6], ERROR_TOLERANCE[2], NO_IMP_GENS[4]);
    //             gp.evolve(preprocessor.getTrainingSetText());
    //             DecisionNode best = gp.getBest();

    //             //Test the algorithm
    //             res += gp.test(best, preprocessor.getTestingSetText(), false, false);        

    //             results.add(res);
    //         }
    //     // }
    //     String path = "tuning/gp/tune_SEEDS.txt";
    //     try {
    //         // Create a new file
    //         File file = new File(path);
    //         // If the file doesn't exist, create it
    //         if (!file.exists()) {
    //             file.createNewFile();
    //         }
    //         // Create a new file writer
    //         FileWriter writer = new FileWriter(file);
    //         // Write the results to the file
    //         for(String res : results){
    //             writer.write(res);
    //         }
    //         // Close the file writer
    //         writer.close();

    //     } catch (IOException e) {
    //         System.out.println("[Main] Error writing to file " + path + ": ");
    //         e.printStackTrace();
    //     }   
    // }
}
