package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListeners implements Listener {
    private WitchWars plugin;

    public GameListeners(WitchWars plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerDeathEvent event){
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if(killer != null){
            plugin.getGameManager().handlePlayersKill(killer, player);
        }
        plugin.getGameManager().handlePlayerDeath(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        plugin.getGameManager().leaveGame(event.getPlayer());
    }
}
