package ann;
// Tayla Orsmond u21467456
// A class that represents a neural network
// The neural network is trained using the backpropagation algorithm
// It has one hidden layer, 51 input neurons and 1 output neuron
// The hidden layer makes use of the ReLU activation function
// The output layer makes use of the binary sigmoid activation function
// The network is seeded from main

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ANN {
    private int numInputs = 51;
    private int numHiddenNeurons;
    private HiddenNeuron[] hiddenLayer; // hidden layer
    private double[] hiddenLayerOutputs; // outputs of the hidden layer
    private OutputNeuron outputNeuron; // output neuron

    private double maxEpochs; // maximum number of iterations for training
    private double errorTolerance; // error tolerance for training
    private int noImpEpochs; // number of epochs of no improvement to wait before stopping training

    /**
     * Constructor for the neural network
     * @param rng the random number generator (seeded from main)
     * @param learningRate the learning rate
     * @param maxEpochs the maximum number of epochs
     * @param errorTolerance the error tolerance
     * @param noImpEpochs the number of epochs of no improvement
     */
    public ANN(Random rng, double learningRate, double maxEpochs, double errorTolerance, int noImpEpochs, int numHiddenNeurons) {
        this.maxEpochs = maxEpochs;
        this.errorTolerance = errorTolerance;
        this.noImpEpochs = noImpEpochs;
        this.numHiddenNeurons = numHiddenNeurons;
        // Initialise the hidden layer
        hiddenLayer = new HiddenNeuron[numHiddenNeurons];
        for (int i = 0; i < numHiddenNeurons; i++) {
            hiddenLayer[i] = new HiddenNeuron(numInputs, learningRate, rng);
        }
        // Initialise the output neuron
        outputNeuron = new OutputNeuron(numHiddenNeurons, learningRate, rng);       
    }

    /**
     * Method to train the network
     * Stops training when the error difference is less than 0.1
     * Or after 50 iterations
     * @param trainingSet the training set
     */
    public String trainNetwork(ArrayList<double[]> trainingSet) {
        // Initialise the number of correct classifications
        int correct = 0;
        int falsePos = 0;
        int falseNeg = 0;
        int truePos = 0;
        int trueNeg = 0;
        // Initialise the error difference
        double errordifference = 1;
        // Initialise the previous error
        double previouserror = 0;
        // Initialise the number of epochs
        int noImprovement = 0;
        // Loop through the training set
        for(int i = 0; i < maxEpochs && noImprovement < noImpEpochs; i++) {
            // Reset the number of correct classifications
            correct = 0;
            falsePos = 0;
            falseNeg = 0;
            truePos = 0;
            trueNeg = 0;
            // Loop through the training set
            for (int j = 0; j < trainingSet.size(); j++) {
                // Train the network
                train(Arrays.copyOfRange(trainingSet.get(j), 1, trainingSet.get(j).length), trainingSet.get(j)[0]);
                // Calculate the output class of the network
                String outputClass = determineClass();
                String targetClass = (trainingSet.get(j)[0] == 0 ? "no-recurrence-events" : "recurrence-events");
                //Print
                // Check if the output class is correct
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
            // Calculate the error difference
            errordifference = Math.abs(outputNeuron.getError() - previouserror);
            // Set the previous error to the current error
            previouserror = outputNeuron.getError();

            if(errordifference < errorTolerance) {
                noImprovement++;
            } else {
                noImprovement = 0;
            }

            // Print
            System.out.println("Epoch: " + (i + 1) + " \n\tError: " + outputNeuron.getError() + " \tError Difference: " + errordifference);
        }
        // Calculate the precision
        double precision = (double) truePos / (truePos + falsePos);
        // Calculate the recall
        double recall = (double) truePos / (truePos + falseNeg);
        // Calculate the F-measure
        double fMeasure = 2 * ((precision * recall) / (precision + recall));
        // Calculate the accuracy
        double accuracy = (double) correct / trainingSet.size() * 100;

        String res = "";
        res += trainingSet.size() + " \t";
        res += correct + " \t";
        res += accuracy + "% \t";
        // res += truePos + " \t" + trueNeg + " \t" + falsePos + " \t" + falseNeg + " \t";
        res += precision + " \t" + recall + " \t" + fMeasure + "\n";

        // Print the accuracy & F-measure of the network
        System.out.println("[TRAIN SET]=======================================");
        System.out.println("Accuracy: " + accuracy + "%");
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F-Measure: " + fMeasure);
        System.out.println("[Correct: " + correct + "]");
        System.out.println("[TruePos: " + truePos + " \tTrueNeg: "+ trueNeg + "]");
        System.out.println("[FalsePos: " + falsePos + " \tFalseNeg: "+ falseNeg + "]");
        System.out.println("==================================================");
        return res;
    }

    /**
     * Method to test the network
     * @param testingSet the testing set
     */
    public String testNetwork(ArrayList<double[]> testingSet) {
        // Initialise the number of correct classifications
        int correct = 0;
        int falsePos = 0;
        int falseNeg = 0;
        int truePos = 0;
        int trueNeg = 0;
        // Loop through the testing set
        for (int i = 0; i < testingSet.size(); i++) {
            // Calculate the output of the network
            feedforward(Arrays.copyOfRange(testingSet.get(i), 1, testingSet.get(i).length));
            // Calculate the output class of the network
            String outputClass = determineClass();
            String targetClass = (testingSet.get(i)[0] == 0 ? "no-recurrence-events" : "recurrence-events");
            //Print
            // Check if the output class is correct
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

        // Print the accuracy & F-measure of the network
        System.out.println("[TEST SET] =======================================");
        System.out.println("Accuracy: " + accuracy + "%");
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F-Measure: " + fMeasure);
        System.out.println("[Correct: " + correct + "]");
        System.out.println("[TruePos: " + truePos + " \tTrueNeg: "+ trueNeg + "]");
        System.out.println("[FalsePos: " + falsePos + " \tFalseNeg: "+ falseNeg + "]");
        System.out.println("==================================================");
        return res;
    }

    /**
     * Method to train the network
     * Uses the backpropagation algorithm
     * @param inputs
     * @param target
     */
    public void train(double[] inputs, double target) {
        // Calculate the output of the network (feedforward)
        feedforward(inputs);
        // Calculate the error information term of the network
        outputNeuron.calculateError((target - outputNeuron.getOutput()));
        // Backpropagate the error information term through the network
        backpropagate(inputs);
    }

    /**
     * Method to calculate the output of the network (feedforward)
     * @param inputs the inputs to the layer
     * @return the output of the network
     */
    public double feedforward(double[] inputs) {
        // Calculate the output of the hidden layer
        hiddenLayerOutputs = new double[numHiddenNeurons];
        for (int i = 0; i < numHiddenNeurons; i++) {
            hiddenLayerOutputs[i] = hiddenLayer[i].calculateOutput(inputs);
        }
        // Calculate the output of the output layer
        return outputNeuron.calculateOutput(hiddenLayerOutputs);
    } 

    /**
     * Method to backpropagate the error information term through the network
     * @param inputs the inputs to the network
     */
    public void backpropagate(double[] inputs) {
        // Loop through the hidden layer
        for (int i = 0; i < numHiddenNeurons; i++) {
            double deltaSumInput = outputNeuron.getError() * outputNeuron.getWeights()[i];
            // Calculate the error of the hidden neuron
            hiddenLayer[i].calculateError(deltaSumInput);
            // Update the weights of the hidden neuron
            hiddenLayer[i].updateWeights(inputs);
        }
        // Update the weights of the output neuron
        outputNeuron.updateWeights(hiddenLayerOutputs);
    }
    
    /**
     * Method to determine the class of the output
     * @return the class of the output
     */
    public String determineClass() {
        return outputNeuron.getOutput() > 0.5 ? "recurrence-events" : "no-recurrence-events";
    }
}
