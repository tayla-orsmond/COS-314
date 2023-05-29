// Tayla Orsmond u21467456
// Main

import java.util.ArrayList;
import java.util.Random;

public class Main {    
    public static void main(String[] args) {
        System.out.println("========== COS 314 - Assignment 3 - ANNs & Decision Trees ==========");
        System.out.println("==========                Tayla Orsmond u21467456         ==========");
        // Hyperparameters
        final double LEARNING_RATE = 0.7;
        final int TRAINING_SIZE = (int) Math.floor(0.7 * 286); // 70% of the data
        final int MAX_EPOCHS = 50;
        final double ERROR_TOLERANCE = 0.01;
        // Random seed
        final int seed = 8008135;
        Random rng = new Random(seed);
        // Load the data
        System.out.println("[M] Loading data...");
        // Read the data
        Loader loader = new Loader();
        ArrayList<String> data = new ArrayList<String>();
        try{
            data = loader.readFile("breast-cancer.data");
        } catch (Exception e) {
            System.out.println("[Main]: Error reading file");
            return;
        }
        // Preprocess the data
        System.out.println("[M] Preprocessing data...");
        Preprocessor preprocessor = new Preprocessor(data);
        preprocessor.encodeData();
        System.out.println("Training set size: " + TRAINING_SIZE);
        preprocessor.splitData(TRAINING_SIZE, rng);
        // Train the network
        System.out.println("==================================================");
        System.out.println("Learning Rate: " + LEARNING_RATE);
        System.out.println("Max Epochs: " + MAX_EPOCHS);
        System.out.println("Error Tolerance: " + ERROR_TOLERANCE);
        System.out.println("Seed: " + seed);
        System.out.println("==================================================");
        System.out.println("\n === [M] Training network... ===\n");

        ANN ann = new ANN(rng, LEARNING_RATE, MAX_EPOCHS, ERROR_TOLERANCE);
        ann.trainNetwork(preprocessor.getTrainingSet());
        //Test the network
        System.out.println("\n=== [M] Testing network... ===\n");
        ann.testNetwork(preprocessor.getTestingSet());
    }
}
