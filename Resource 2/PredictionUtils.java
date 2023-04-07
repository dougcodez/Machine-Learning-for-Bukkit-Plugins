package io.github.dougcodez.spamorham;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PredictionUtils {

    //Prediction Grabber stores the given prediction in its own reference
    private static final AtomicReference<Double> predictionGrabber = new AtomicReference<>();

    //Prediction map stores both the spam message and the prediction if it bypasses the specified threshold/bias
    private static final Map<String, Double> predictionMap = new LinkedHashMap<>();


    /**
     * Checks if the given message contains spam
     * @param rnnModel The RNNModel to use
     * @param message The message to check
     * @return True if the message contains spam
     */
    public static boolean containsSpam(RNNModel rnnModel, String message) {
        //The length of the message
        int inputSize = 100;

        //The declared input with its length passed with in
        double[] input = new double[inputSize];

        //Here we loop through the message to ranging from 0 and the length of the message
        for (int i = 0; i < inputSize; i++) {
            /**
             * If the message length is less than the input size, we normalize it by dividing it by 256 to get a value between 0 and 1
             */
            if (i < message.length()) {
                input[i] = (double) message.charAt(i) / 256.0; // normalize input
            } else {
                input[i] = 0.0;
            }
        }
        //We then pass the input to the forward method to get the prediction. Obviously make sure the model is trained first
        double[] output = rnnModel.predict(input);

        //We then grab the prediction from the output array and store it in the predictionGrabber reference
        predictionGrabber.set(output[0]);

        //If the prediction is greater than 0.85 then it will be considered spam
        return output[0] >= 0.85;
    }

    public static double getPrediction() {
        return predictionGrabber.get();
    }

    public static Map<String, Double> getPredictionMap() {
        return predictionMap;
    }
}
