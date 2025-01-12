package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TeamListeners implements Listener {
    private WitchWars plugin;

    public TeamListeners(WitchWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player damager = (Player)  event.getDamager();
        Player victim = (Player)  event.getEntity();
        if(plugin.getTeamManager().areOnSameTeam(damager, victim)){
            event.setCancelled(true);
        }
    }
}
