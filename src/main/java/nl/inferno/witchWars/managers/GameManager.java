package nl.inferno.witchWars.managers;

import nl.inferno.witchWars.WitchWars;
import nl.inferno.witchWars.game.GameState;
import nl.inferno.witchWars.game.Arena;
import nl.inferno.witchWars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.UUID;

public class GameManager {
    private final WitchWars plugin;
    private final HashMap<String, Arena> arenas;
    private final HashMap<UUID, String> playerArena;

    public GameManager(WitchWars plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.playerArena = new HashMap<>();
        loadArenas();
    }

    private void loadArenas() {
        if (plugin.getConfig().contains("arenas")) {
            for (String arenaName : plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
                createArena(arenaName);
            }
        }
    }

    public void createArena(String name) {
        arenas.put(name, new Arena(name));
    }

    public void startGame(String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena != null && arena.canStart()) {
            arena.startGame();
            broadcastToArena(arenaName, ChatColor.GREEN + "Game is starting!");
        }
    }

    public void stopGame(String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena != null) {
            arena.stopGame();
        }
    }

    public void stopAllGames() {
        arenas.values().forEach(Arena::stopGame);
    }

    public void joinGame(Player player, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena != null && arena.getGameState() == GameState.WAITING) {
            if (arena.addPlayer(player)) {
                playerArena.put(player.getUniqueId(), arenaName);
                player.sendMessage(ChatColor.GREEN + "You joined arena " + arenaName);
            }
        }
    }

    public void leaveGame(Player player) {
        String arenaName = playerArena.get(player.getUniqueId());
        if (arenaName != null) {
            Arena arena = arenas.get(arenaName);
            if (arena != null) {
                arena.removePlayer(player);
                playerArena.remove(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "You left the game!");
            }
        }
    }

    public void broadcastToArena(String arenaName, String message) {
        Arena arena = arenas.get(arenaName);

        if(arena != null){
            for(UUID playerId : arena.getPlayers()){
                Player player = Bukkit.getPlayer(playerId);

                if(player != null){
                    player.sendMessage(message);
                }
            }
        }
    }

    public void checkGameEnd(String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena != null) {
            arena.checkEndGame();
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public Arena getPlayerArena(Player player) {
        String arenaName = playerArena.get(player.getUniqueId());
        return arenaName != null ? arenas.get(arenaName) : null;
    }

    public void handlePlayersKill(Player killer, Player player) {
        Arena arena = getPlayerArena(killer);

        if(arena != null){
            arena.addKill(killer);
            plugin.getStatsManager().addKill(killer);
            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    public void handlePlayerDeath(Player player) {
        Arena arena = getPlayerArena(player);

        if(arena != null){
            plugin.getStatsManager().addDeath(player);
            arena.checkEndGame();
        }
    }

    public void setTeamSpawn(String arenaName, String teamName, Location loc) {
        String path = "arenas." + arenaName + ".teams." + teamName + ".spawn";
        saveLocation(path, loc);

        Arena arena = getArena(arenaName);
        if (arena != null) {
            Team team = arena.getTeam(teamName);
            if (team != null) {
                team.setSpawnPoint(loc);
            }
        }
    }

    public void setWitchSpawn(String arenaName, String teamName, Location loc) {
        String path = "arenas." + arenaName + ".teams." + teamName + ".witch";
        saveLocation(path, loc);

        Arena arena = getArena(arenaName);
        if (arena != null) {
            Team team = arena.getTeam(teamName);
            if (team != null) {
                team.setWitchSpawn(loc);
            }
        }
    }

    public void setLobbySpawn(String arenaName, Location loc) {
        String path = "arenas." + arenaName + ".lobby";
        saveLocation(path, loc);

        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.setLobbySpawn(loc);
        }
    }

    private void saveLocation(String path, Location loc) {
        String locString = loc.getWorld().getName() + "," +
                          loc.getX() + "," +
                          loc.getY() + "," +
                          loc.getZ() + "," +
                          loc.getYaw() + "," +
                          loc.getPitch();
        plugin.getConfig().set(path, locString);
        plugin.saveConfig();
    }
}
