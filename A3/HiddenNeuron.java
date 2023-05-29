// Tayla Orsmond u21467456
// A class that represents a neuron in the hidden layer of a neural network
// This neuron makes use of the ReLU activaton function
// The neural network is trained using the backpropagation algorithm

import java.util.Random;

public class HiddenNeuron extends Neuron {
    public HiddenNeuron(int numInputs, double learningRate, Random rng) {
        super(numInputs, learningRate, rng);
    }

    // ReLU activation function
    public double activationFunction(double input) {
        if (input > 0) {
            return input;
        } else {
            return 0;
        }
    }

    // Derivative of ReLU activation function
    public double activationFunctionDerivative(double input) {
        if (input > 0) {
            return 1;
        } else {
            return 0;
        }
    }
    
}
