// Tayla Orsmond u21467456
// Main program

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
        // ========== Random seed
        final int seed = 2069687265;
        Random rng = new Random(seed);
        AtomicBoolean yes = new AtomicBoolean(false);
        // ==========
        // Preprocess the data
        System.out.println("[Main] Preprocessing data...");
        System.out.println("Training set size: " + TRAINING_SIZE + "/286 (" + Math.round(TRAINING_SIZE / 286.0 * 100)  + "%)");
        System.out.println("\u001B[33mSeed: " + seed + "\u001B[0m");
        Preprocessor preprocessor = new Preprocessor(data);
        preprocessor.encodeData();
        preprocessor.splitData(TRAINING_SIZE, rng);
        // Run the GP
        if(!yes.getAndSet(true)){
            runGP(preprocessor, rng);
        }
        // Run the ANN
        if(yes.getAndSet(false)){
            runANN(preprocessor, rng);
        }

        System.out.println("\u001B[0m==================================================================\n[Main] Done. Press Enter to exit.");
        // Press enter to exit
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
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
        System.out.println("\u001B[34mANN ==============================================");
        System.out.println("Learning Rate: " + LEARNING_RATE);
        System.out.println("Max Epochs: " + MAX_EPOCHS);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("No Improvement Epochs: " + NO_IMP_EPOCHS);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training network... ===\n");

        ANN ann = new ANN(rng, LEARNING_RATE, MAX_EPOCHS, ERROR_TOLERANCE, NO_IMP_EPOCHS, NUM_HIDDEN_NODES);
        result += ann.trainNetwork(preprocessor.getTrainingSet());
        //Test the network
        System.out.println("\n=== [M] Testing network... ===\n");
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
        System.out.println("\u001B[32mGP ===============================================");
        System.out.println("Max Depth: " + MAX_DEPTH);
        System.out.println("Tournament Size: " + TOURNAMENT);
        System.out.println("Crossover Rate: " + CROSSOVER_RATE);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("No Improvement Generations: " + NO_IMP_GENS);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training GP (evolving trees)... ===\n");

        GP gp = new GP(rng, MAX_DEPTH, TOURNAMENT, CROSSOVER_RATE, ERROR_TOLERANCE, NO_IMP_GENS);
        result += gp.evolve(preprocessor.getTrainingSetText());
        DecisionNode bestTree = gp.getBest();
        // Test the GP
        System.out.println("\n=== [M] Testing GP (with best tree)... ===\n");
        result += gp.test(bestTree, preprocessor.getTestingSetText(), true, true);
        return result;
    }
}
