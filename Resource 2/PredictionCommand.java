package io.github.dougcodez.spamorham;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PredictionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("predictions")) {
            Map<String, Double> predictionMap = PredictionUtils.getPredictionMap();
            if (predictionMap.size() >= 1) {
                for (Map.Entry<String, Double> entry : predictionMap.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lSpamOrHam: &7{&fCaptured Phrase &8| &fPrediction&7}&f: " + "&7{&f" + key + " &8| &f" + value + "&7}"));
                }
            }else{
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lSpamOrHam: &7{&fCaptured Phrase &8| &fPrediction&7}&f: " + "&7{&fNo predictions found &8| &f0.0&7}"));
            }
        }
        return true;
    }
}
