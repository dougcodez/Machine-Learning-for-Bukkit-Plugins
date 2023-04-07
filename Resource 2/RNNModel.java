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

        //Initialize hidden and output layers with zeros
        hidden = new double[hiddenSize];
        output = new double[outputSize];
    }

    //Method to feed input and get output
    public double[] forward(double[] input) {
        /*
         * Here we determine the hidden layer values by looping over each corresponding element assigned within the hidden array.
         * We then multiply the weightsIn array by the input array and gain the sum of the input values multiplied by the
         * corresponding weights.
         */
        Arrays.setAll(hidden, i -> IntStream.range(0, inputSize)
                .mapToDouble(j -> weightsIn[i][j] * input[j])
                .sum());
        //We then apply the hyperbolic tangent function to the hidden array
        Arrays.setAll(hidden, i -> tanh(hidden[i]));

        /*
         * Here we determine the output layer values by looping over each corresponding element assigned within the output array.
         * We then multiply the weightsOut array by the hidden array and gain the sum of the hidden value multiplied by the
         * corresponding weights .
         */
        Arrays.setAll(output, i -> IntStream.range(0, hiddenSize)
                .mapToDouble(j -> weightsOut[i][j] * hidden[j])
                .sum());

        //We then apply the hyperbolic tangent function to the output array
        Arrays.setAll(output, i -> tanh(output[i]));
        return output;
    }

    //Method to train our model
    public void train(double[][] inputs, double[][] targets, int numEpochs, double learningRate) {

        //This represents how many training samples we are going for when we go to training our model
        int numInputs = inputs.length;

        //This represents how many times we are going train our model
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            //This line starts the loop that will loop through all of our training samples
            for (int i = 0; i < numInputs; i++) {

                //This represents our input data for the current training sample
                double[] input = inputs[i];

                //This represents our target data for the current training sample
                double[] target = targets[i];

                //We are going to use the forward method to get our output.
                double[] output = forward(input);

                /*
                 * Here we are calculating the error for both predicted and expected output.
                 * We are ensuring the range is from 0 to the size of the output.
                 * It is doing this by subtracting the target elements from each output element.
                 * mapToDouble is used to convert the integers gathered to doubles.
                 * Once mapped we convert error to an array.

                 */
                double[] error = IntStream.range(0, outputSize)
                        .mapToDouble(j -> target[j] - output[j])
                        .toArray();

                /*
                 * Pass in the back propagation algorithm to determine gradients for both hidden and output layers
                 */

                /*
                 *This line determines the derivative of our output layers by streaming through the output layer and
                 * applying the derivative of the tanh function to each element
                 */
                double[] outputDeriv = DoubleStream.of(output)
                        .map(x -> 1 - x * x)
                        .toArray();

                /*
                *This line determines the derivative of our hidden layers by streaming through the hidden layer and
                * applying the derivative of the tanh function to each element
                 */
                double[] hiddenDeriv = DoubleStream.of(hidden)
                        .map(x -> 1 - x * x)
                        .toArray();

                /*
                 *This line determine the error for the output layers by gathering a stream of integers ranging from 0 to the size of the output layer - 1
                 * For each corresponding element, we multiply the error by the derivative of the output layer
                 * The stream is then turned back to an array and stored in the outputError array
                 */

                double[] outputError = IntStream.range(0, outputSize)
                        .mapToDouble(j -> error[j] * outputDeriv[j])
                        .toArray();

                /*
                *This line determine the error for the hidden layers by gathering a stream of integers ranging from 0 to the size of the hidden layer - 1
                * For each corresponding element, we determine the sum of the product of the error of the output layer and the weights of the output layer
                * The stream is then turned back to an array and stored in the hiddenError array
                 */
                double[] hiddenError = IntStream.range(0, hiddenSize)
                        .mapToDouble(j -> IntStream.range(0, outputSize)
                                .mapToDouble(k -> weightsOut[k][j] * outputError[k])
                                .sum() * hiddenDeriv[j])
                        .toArray();


                /*
                 * Update weights for both hidden and output layers
                 */

                /*
                 * The update for this line is based on the errors within the input layer and the hidden layer that were calculated above
                 * along with the set learning rate.
                 */
                IntStream.range(0, outputSize).forEach(j -> IntStream.range(0, hiddenSize).forEach(k -> weightsOut[j][k] += learningRate * outputError[j] * hidden[k]));

                /*
                 * The update for this line is based on the errors within the hidden layer and the corresponding input layer.
                 * The process of this update makes sure that all weights are fine-tuned a certain way that way the overall error
                 * is as minimal as possible.
                 */
                IntStream.range(0, hiddenSize).forEach(j -> IntStream.range(0, inputSize).forEach(k -> weightsIn[j][k] += learningRate * hiddenError[j] * input[k]));
            }
        }
    }

    /**
     * Method to apply forward method
     * @param input input to be fed forward
     * @return given prediction of the input passed in
     */
    public double[] predict(double[] input) {
        return forward(input);
    }

    /**
     * Our activation function tanh
     * @param x represents the input
     * @return the output of the tanh function
     */
    private double tanh(double x) {
        return (2.0 / (1.0 + Math.exp(-2.0 * x))) - 1.0;
    }
}
