package io.github.dougcodez.spamorham;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class SpamOrHam extends JavaPlugin {

    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        List<String> inputs = Arrays.asList(
                "Urgent: You have won a free cruise to the Bahamas!",
                "Your account has been suspended. Click here to restore access.",
                "Congratulations! You have been selected to receive a free gift.",
                "Dear John, I hope this email finds you well.",
                "Don't miss out on this limited time offer!",
                "Work from home and make $1000s per week!",
                "You have been pre-approved for a credit card with 0% interest.",
                "Get rich quick with this amazing opportunity!",
                "Lose weight fast with our all natural supplement.",
                "Click here to get the best deals on all your favorite products.",
                "Hi there, how was your weekend?",
                "Can you help me with a project I'm working on?",
                "Hey, did you see the game last night?",
                "The weather is really nice today, isn't it?",
                "Have you tried the new restaurant downtown?",
                "Just wanted to say thanks for your help the other day.",
                "This is a test message. Please ignore.",
                "Hope you have a great day!",
                "Looking forward to seeing you later.",
                "Take care and talk to you soon!"
        );

        List<Integer> targets = Arrays.asList(
                1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        ); // 1 for spam, 0 for ham


        // create model
        int inputSize = 100; // length of input message
        int hiddenSize = 32;
        int outputSize = 1; // single output for binary classification
        double learningRate = 0.05;
        int numEpochs = 1000;

        RNNModel rnn = new RNNModel(inputSize, hiddenSize, outputSize);

        // train model
        double[][] X = new double[inputs.size()][inputSize];
        double[][] Y = new double[inputs.size()][outputSize];
        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);
            int target = targets.get(i);
            for (int j = 0; j < inputSize; j++) {
                if (j < input.length()) {
                    X[i][j] = (double) input.charAt(j) / 256.0; // normalize input
                } else {
                    X[i][j] = 0.0;
                }
            }
            Y[i][0] = target;
        }
        rnn.train(X, Y, numEpochs, learningRate);



        Bukkit.getPluginManager().registerEvents(new SpamOrHamListener(rnn), this);
        getCommand("predictions").setExecutor(new PredictionCommand());
    }

    public void onDisable(){

    }
}
