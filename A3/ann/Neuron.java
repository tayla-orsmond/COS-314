package ann;
// Tayla Orsmond u21467456
// An abstract class that represents a neuron in a neural network
// The neural network is trained using the backpropagation algorithm

import java.util.Random;

public abstract class Neuron {
    // inputs get passed in when necessary
    private double[] weights; // weights for each input
    private double output; // output of the neuron
    private double error; // error of the neuron
    private double bias; // bias
    private double biasWeight; // bias weight
    private double learningRate; // learning rate

    /**
     * Constructor for the neuron
     * @param numInputs the number of inputs
     * @param learningRate the learning rate
     * @param rng the random number generator
     */
    protected Neuron(int numInputs, double learningRate, Random rng) {
        weights = new double[numInputs];
        this.learningRate = learningRate;
        bias = 1;
        biasWeight = rng.nextDouble();
        for (int i = 0; i < numInputs; i++) {
            weights[i] = rng.nextDouble();
        }
    }

    // Getters
    public double[] getWeights() {
        return weights;
    }

    public double getOutput() {
        return output;
    }

    public double getError() {
        return error;
    }

    public double getBiasWeight() {
        return biasWeight;
    }

    // Setters
    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public void setBiasWeight(double biasWeight) {
        this.biasWeight = biasWeight;
    }

    /**
     * Method to calculate the output of the neuron
     * @param inputs the inputs to the neuron
     * @return the output to the neuron
     */
    public double calculateOutput(double[] inputs) {
        double sum = 0;
        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }
        sum += bias * biasWeight;
        output = activationFunction(sum);
        return output;
    }

    /**
     * Method to calculate the error of the neuron
     * @param target the target value for the neuron
     * target can be target - output for output layer, or sum of weights * errors for hidden layers
     * @return the error of the neuron
     */
    public double calculateError(double value) { 
        error = value * activationFunctionDerivative(output);
        return error;
    }

    /**
     * Method to update the weights of the neuron
     * @param inputs the inputs to the neuron
     * inputs can be the inputs to the neural network for the first layer, or the outputs of the previous layer for the other layers
     */
    public void updateWeights(double[] inputs) {
        for (int i = 0; i < inputs.length; i++) {
            weights[i] += learningRate * error * inputs[i];
        }
        biasWeight = learningRate * error * bias;
    }
    
    public abstract double activationFunction(double input);

    public abstract double activationFunctionDerivative(double input);
    
}
