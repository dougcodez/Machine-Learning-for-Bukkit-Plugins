package io.github.dougcodez.itempredictor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemDetectionCommand implements CommandExecutor {

    private PerceptronModel model;

    public ItemDetectionCommand(PerceptronModel model) {
        this.model = model;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args[0].equalsIgnoreCase("train")) {
                // Train the model with some example data
                List<double[]> X = new ArrayList<>();
                List<Integer> yLabels = new ArrayList<>();

                int numDiamonds = 0;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.DIAMOND) {
                        numDiamonds++;
                    }
                }
                for (ItemStack item : player.getInventory().getContents()) {
                    double[] x = new double[player.getInventory().getSize()];
                    int y;
                    if (item != null && item.getType() == Material.DIAMOND) {
                        int index = player.getInventory().first(item);
                        x[index] = 1;
                        y = 1; // Diamond present
                        numDiamonds--;
                    } else {
                        y = 0; // No diamond
                    }
                    X.add(x);
                    yLabels.add(y);
                }


                // Train the perceptron model
                double learningRate = 0.1;
                int numIterations = 50;
                model.train(X, yLabels.stream().mapToInt(i -> i).toArray(), learningRate, numIterations);

                // Evaluate model performance
                player.sendMessage("Predictions");
                player.sendMessage("--------------------");
                for (int i = 0; i < X.size(); i++) {
                    double[] x = X.get(i);
                    double prediction = model.predict(x);
                    int y_pred = prediction >= 0.5 ? 1 : 0;
                    player.sendMessage("Prediction #" + i + ": " + y_pred);
                }
                X.clear();
                yLabels.clear();
            }
        }

        return true;
    }
}
