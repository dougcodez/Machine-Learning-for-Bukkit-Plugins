package io.github.dougcodez.itempredictor;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;


public class ItemDetection extends JavaPlugin implements CommandExecutor {

    private final PerceptronModel model = new PerceptronModel(2);


    public void onEnable() {
        getCommand("model").setExecutor(new ItemDetectionCommand(model));
    }
}
 
