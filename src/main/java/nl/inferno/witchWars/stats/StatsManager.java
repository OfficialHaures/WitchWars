package nl.inferno.witchWars.stats;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private WitchWars plugin;
    private final Map<UUID, PlayerStats> stats;

    public StatsManager() {
        this.stats = new HashMap<>();
    }

    public void addKill(Player player) {
        getStats(player).addKill();
    }

    public void addWin(Player player) {
        getStats(player).addWin();
    }

    public void addWitchKill(Player player) {
        getStats(player).addWitchKill();
    }

    public PlayerStats getStats(Player player) {
        return stats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats());
    }

    public void saveAllData() {
        for (Map.Entry<UUID, PlayerStats> entry : stats.entrySet()) {
            String path = "stats." + entry.getKey();
            PlayerStats playerStats = entry.getValue();
            plugin.getConfig().set(path + ".kills", playerStats.getKills());
            plugin.getConfig().set(path + ".deaths", playerStats.getDeaths());
            plugin.getConfig().set(path + ".wins", playerStats.getWins());
            plugin.getConfig().set(path + ".gamesPlayed", playerStats.getGamesPlayed());
            plugin.getConfig().set(path + ".witchKills", playerStats.getWitchKills());
            plugin.getConfig().set(path + ".resourcesCollected" , playerStats.getResourcesCollected());
            plugin.getConfig().set(path + ".spellsCast", playerStats.getSpellsCast());

        }
        plugin.saveConfig();
    }

    public void addDeath(Player player) {
        getStats(player).addDeath();
    }
}
