package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.net.http.WebSocket;

public class PowerUpListeners implements Listener {
    private WitchWars plugin;

    public PowerUpListeners(WitchWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPowerPickup(PlayerPickupItemEvent event){
        if (plugin.getPowerUpManager().isPowerUp(event.getItem())) {
        event.setCancelled(true);
        plugin.getPowerUpManager().handlePowerUpPickup(event.getItem(), event.getPlayer());
        }
    }
}
