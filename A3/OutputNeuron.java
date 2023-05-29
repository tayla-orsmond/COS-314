// Tayla Orsmond u21467456
// A class that represents a neuron in the output layer of a neural network
// This neuron makes use of the binary sigmoid function as its activation function
// The neural network is trained using the backpropagation algorithm

import java.util.Random;

public class OutputNeuron extends Neuron {
    public OutputNeuron(int numInputs, double learningRate, Random rng) {
        super(numInputs, learningRate, rng);
    }

    // Binary sigmoid activation function
    public double activationFunction(double input) {
        return 1.0 / (1.0 + Math.exp(-input));
    }

    // Derivative of binary sigmoid activation function
    public double activationFunctionDerivative(double input) {
        return activationFunction(input) * (1.0 - activationFunction(input));
    }
    
}
