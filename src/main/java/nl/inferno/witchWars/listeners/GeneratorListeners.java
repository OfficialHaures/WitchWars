package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.net.http.WebSocket;

public class GeneratorListeners implements Listener {
    private WitchWars plugin;

    public GeneratorListeners(WitchWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGeneratorBreak(BlockBreakEvent event){
        if(plugin.getGeneratorManager().isGenerator(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }
}
