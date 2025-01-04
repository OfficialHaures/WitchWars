package nl.inferno.witchWars.stats;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
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

    private PlayerStats getStats(Player player) {
        return stats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats());
    }

    public void saveAllData() {
        // Save stats to database/file
    }
}
