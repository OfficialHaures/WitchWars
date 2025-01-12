package nl.inferno.witchWars.game;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Arena {
    private final String name;
    private GameState gameState;
    private final Set<UUID> players;
    private final List<Team> teams;
    private Location lobbySpawn;
    private final WitchWars plugin;
    private BukkitTask gameTimer;
    private Map<UUID, Integer> playerKills = new HashMap<>();
    private int minPlayers;
    private int maxPlayers;

    public Arena(String name) {
        this.name = name;
        this.gameState = GameState.WAITING;
        this.players = new HashSet<>();
        this.teams = new ArrayList<>();
        this.plugin = WitchWars.getInstance();
        loadLobbySpawn();
        loadTeams();
    }

    private void loadLobbySpawn() {
        if (plugin.getConfig().contains("arenas." + name + ".lobby")) {
            String[] loc = plugin.getConfig().getString("arenas." + name + ".lobby").split(",");
            this.lobbySpawn = new Location(
                Bukkit.getWorld(loc[0]),
                Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]),
                Double.parseDouble(loc[3])
            );
        }
    }

    private void loadTeams() {
        if (plugin.getConfig().contains("arenas." + name + ".teams")) {
            for (String teamName : plugin.getConfig().getConfigurationSection("arenas." + name + ".teams").getKeys(false)) {
                String colorStr = plugin.getConfig().getString("arenas." + name + ".teams." + teamName + ".color");
                if (colorStr != null) {
                    try {
                        ChatColor color = ChatColor.valueOf(colorStr.toUpperCase());
                        teams.add(new Team(teamName, color));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid color for team " + teamName + " in arena " + name);
                    }
                }
            }
        }
    }

    private Location parseLocation(String locationStr) {
        String[] loc = locationStr.split(",");
        return new Location(
            Bukkit.getWorld(loc[0]),
            Double.parseDouble(loc[1]),
            Double.parseDouble(loc[2]),
            Double.parseDouble(loc[3])
        );
    }

    public void startGame() {
        gameState = GameState.PLAYING;
        distributePlayersToTeams();
        spawnWitches();
        startGenerators();
        teleportPlayers();
        startGameTimer();
    }

    private void distributePlayersToTeams() {
        List<Team> availableTeams = new ArrayList<>(teams);
        List<UUID> unassignedPlayers = new ArrayList<>(players);

        while (!unassignedPlayers.isEmpty() && !availableTeams.isEmpty()) {
            Team team = availableTeams.get(0);
            UUID playerId = unassignedPlayers.remove(0);
            Player player = Bukkit.getPlayer(playerId);

            if (player != null && team.getSpawnPoint() != null) {
                team.addPlayer(player);
            }

            if (team.isFull()) {
                availableTeams.remove(0);
            }
        }
    }

    private void spawnWitches() {
        teams.forEach(Team::spawnWitch);
    }

    private void startGenerators() {
        plugin.getGeneratorManager().startGenerators(name);
    }

    private void teleportPlayers() {
        for (Team team : teams) {
            for (UUID playerId : team.getPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.teleport(team.getSpawnPoint());
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
    }

    private void startGameTimer() {
        gameTimer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            checkEndGame();
        }, 20L, 20L);
    }

    public void teleportToLobby(Player player) {
        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= getMaxPlayers()) return false;
        players.add(player.getUniqueId());
        teleportToLobby(player);
        return true;
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        teams.forEach(team -> team.removePlayer(player.getUniqueId()));
    }

    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        resetGame();
    }

    private void resetGame() {
        gameState = GameState.WAITING;

        for(UUID playerId : players){
            Player player  = Bukkit.getPlayer(playerId);
            if(player != null){
                player.teleport(lobbySpawn);
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setLevel(0);
                player.setExp(0);

                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
            }
        }

        for(Team team : teams){
            team.reset();
        }

        plugin.getGeneratorManager().stopAllGenerators();
        players.clear();
        playerKills.clear();

        if(gameTimer != null){
            gameTimer.cancel();
            gameTimer = null;
        }
    }

    public boolean canStart() {
        return players.size() >= getMinPlayers() && gameState == GameState.WAITING;
    }

    public int getMinPlayers() {
        return plugin.getConfig().getInt("arenas." + name + ".minPlayers", 2);
    }

    private int getMaxPlayers() {
        return plugin.getConfig().getInt("arenas." + name + ".maxPlayers", 16);
    }

    public String getName() { return name; }
    public GameState getGameState() { return gameState; }
    public Set<UUID> getPlayers() { return Collections.unmodifiableSet(players); }
    public List<Team> getTeams() { return Collections.unmodifiableList(teams); }

    public Team getPlayerTeam(Player player) {
        for(Team team : teams){
            if(team.getPlayers().contains(player.getUniqueId())){
                return team;
            }
        }
        return null;
    }

    public String getPlayerKills(Player player) {
        return String.valueOf(playerKills.getOrDefault(player.getUniqueId(), 0));
    }
    public void addKill(Player player){
        playerKills.merge(player.getUniqueId(), 1, Integer::sum);
    }

    public void checkEndGame() {
        int teamsAlive = 0;
        Team winningTeam = null;

        for (Team team : teams) {
            if (team.isAlive()) {
                teamsAlive++;
                winningTeam = team;
            }
        }

        if (teamsAlive == 1 && winningTeam != null) {
            gameState = GameState.ENDING;

            broadcast(ChatColor.GOLD + "=========================");
            broadcast(ChatColor.WHITE + "Team " + winningTeam.getColor() + winningTeam.getName() +
                    ChatColor.WHITE + " has won the game!");
            broadcast(ChatColor.GOLD + "=========================");

            for (UUID playerId : winningTeam.getPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    plugin.getStatsManager().addWin(player);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }
            }

            Bukkit.getScheduler().runTaskLater(plugin, this::resetGame, 200L);
        }
        else if (teamsAlive == 0) {
            broadcast(ChatColor.RED + "Game ended in a draw!");
            resetGame();

        }

    }
    public void broadcast(String message) {
        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        }
        }

    public void setMinPlayers(int amount) {
        this.minPlayers = amount;
    }

    public void setMaxPlayers(int amount) {
        this.maxPlayers = amount;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public Team getTeam(String teamName) {
        for(Team team : teams){
            if(team.getName().equalsIgnoreCase(teamName)){
                return team;
            }
        }
        return null;
    }


    public void setLobbySpawn(Location loc) {
        this.lobbySpawn = loc.clone();
    }

    public Object getLobbySpawn() {
        return lobbySpawn != null ? lobbySpawn.clone() : null;
    }
}

