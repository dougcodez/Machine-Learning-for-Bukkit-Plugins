package io.github.dougcodez.spamorham;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SpamOrHamListener implements Listener {

    private final RNNModel rnnModel;

    public SpamOrHamListener(RNNModel rnnModel) {
        this.rnnModel = rnnModel;
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        //Remember make the sure model was previously trained so that way we can determine a prediction
        if (PredictionUtils.containsSpam(rnnModel, message)) {
            event.setCancelled(true);
            player.sendMessage("You can't say that!");

            //We use the decimal formatter to round the prediction to 3 decimal places
            DecimalFormat df = new DecimalFormat("#.###");
            df.setRoundingMode(RoundingMode.FLOOR);
            double prediction = Double.parseDouble(df.format(PredictionUtils.getPrediction()));

            //We then store the message and the prediction in the prediction map
            PredictionUtils.getPredictionMap().putIfAbsent(message, prediction);

            //We then alert the opped player that the message was spam and that's if the prediction was greater than 0.85 (threshold)
            if (player.isOp()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lSFilter: &7{&fCaptured Phrase &8| &fPrediction&7}&f: " + "&7{&f" + message + " &8| &f" +  prediction + "&7}"));
            }
        }
    }
}
