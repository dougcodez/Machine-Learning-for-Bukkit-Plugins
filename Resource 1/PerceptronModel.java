package io.github.dougcodez.itempredictor;

import org.bukkit.Bukkit;

import java.util.List;

public class PerceptronModel {

    private double[] weights;
    private double bias;

    public PerceptronModel(int numFeatures) {
        // Initialize the weights and bias to 0
        weights = new double[numFeatures];
        bias = 0;
    }

    public void train(List<double[]> X, int[] y, double learningRate, int numIterations) {
        // Train the perceptron model using the input data X and labels y
        for (int iteration = 0; iteration < numIterations; iteration++) {
            for (int i = 0; i < X.size(); i++) {
                double[] x = X.get(i);
                int prediction = predict(x);
                int error = y[i] - prediction;
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * x[j];
                }
                bias += learningRate * error;
            }
        }
    }

    public int predict(double[] x) {
        // Compute the weighted sum of the input features and bias
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * x[i];
        }
        // Apply the function to the weighted sum to get the probability
        return sum > 0.5 ? 1 : 0;
    }

}
