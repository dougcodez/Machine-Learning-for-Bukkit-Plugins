package io.github.dougcodez.spamorham;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class RNNModel {

    //Layer sizes for our layers
    private final int inputSize;
    private final int hiddenSize;
    private final int outputSize;

    //Defining our weights for our input and output layers
    private final double[][] weightsIn;
    private final double[][] weightsOut;

    //Defining our hidden and output layers
    private final double[] hidden;
    private final double[] output;

    public RNNModel(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;

        //Initialize weights with random values
        weightsIn = new double[hiddenSize][inputSize];
        weightsOut = new double[outputSize][hiddenSize];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        IntStream.range(0, hiddenSize).forEach(i -> IntStream.range(0, inputSize).forEach(j -> weightsIn[i][j] = random.nextDouble() - 0.5));
        IntStream.range(0, outputSize).forEach(i -> IntStream.range(0, hiddenSize).forEach(j -> weightsOut[i][j] = random.nextDouble() - 0.5));

        // Initialize hidden and output layers with zeros
        hidden = new double[hiddenSize];
        output = new double[outputSize];
    }

    // Method to feed input and get output
    public double[] forward(double[] input) {
        // Compute hidden layer
        Arrays.setAll(hidden, i -> IntStream.range(0, inputSize)
                .mapToDouble(j -> weightsIn[i][j] * input[j])
                .sum());
        // Apply tanh activation function to hidden layer
        Arrays.setAll(hidden, i -> tanh(hidden[i]));
        // Compute output layer
        Arrays.setAll(output, i -> IntStream.range(0, hiddenSize)
                .mapToDouble(j -> weightsOut[i][j] * hidden[j])
                .sum());
        // Apply tanh activation function to output layer
        Arrays.setAll(output, i -> tanh(output[i]));
        return output;
    }

    //Method to train our model
    public void train(double[][] inputs, double[][] targets, int numEpochs, double learningRate) {
        int numInputs = inputs.length;
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            for (int i = 0; i < numInputs; i++) {
                double[] input = inputs[i];
                double[] target = targets[i];

                // Feed input forward to get output
                double[] output = forward(input);

                // Compute error
                double[] error = IntStream.range(0, outputSize)
                        .mapToDouble(j -> target[j] - output[j])
                        .toArray();

                // Backpropagate error to compute gradients
                double[] outputDeriv = DoubleStream.of(output)
                        .map(x -> 1 - x * x)
                        .toArray();
                double[] hiddenDeriv = DoubleStream.of(hidden)
                        .map(x -> 1 - x * x)
                        .toArray();
                double[] outputError = IntStream.range(0, outputSize)
                        .mapToDouble(j -> error[j] * outputDeriv[j])
                        .toArray();
                double[] hiddenError = IntStream.range(0, hiddenSize)
                        .mapToDouble(j -> IntStream.range(0, outputSize)
                                .mapToDouble(k -> weightsOut[k][j] * outputError[k])
                                .sum() * hiddenDeriv[j])
                        .toArray();


                //Update weights for input and output layers
                IntStream.range(0, outputSize).forEach(j -> IntStream.range(0, hiddenSize).forEach(k -> weightsOut[j][k] += learningRate * outputError[j] * hidden[k]));
                IntStream.range(0, hiddenSize).forEach(j -> IntStream.range(0, inputSize).forEach(k -> weightsIn[j][k] += learningRate * hiddenError[j] * input[k]));
            }
        }
    }

    //Method to predict output for a single input
    public double[] predict(double[] input) {
        return forward(input);
    }

    private double tanh(double x) {
        return (2.0 / (1.0 + Math.exp(-2.0 * x))) - 1.0;
    }
}
