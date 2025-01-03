package nl.inferno.witchWars.game;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

public class Arena {
    private final String name;
    private GameState state;
    private final Set<UUID> players;
    private final List<Team> teams;
    private final Location lobbySpawn;
    private BukkitTask gameTask;
    private int countdown;
    private final int minPlayers;
    private final int maxPlayers;

    public Arena(String name) {
        this.name = name;
        this.state = GameState.WAITING;
        this.players = new HashSet<>();
        this.teams = new ArrayList<>();
        this.lobbySpawn = loadLobbySpawn();
        this.minPlayers = WitchWars.getInstance().getConfig().getInt("settings.min-players", 2);
        this.maxPlayers = WitchWars.getInstance().getConfig().getInt("settings.max-players", 16);
        loadTeams();
    }

    public void startGame() {
        state = GameState.PLAYING;
        distributePlayersToTeams();
        spawnWitches();
        startGenerators();
        teleportPlayers();
        startGameTimer();
    }

    public void stopGame() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        resetArena();
        state = GameState.WAITING;
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= maxPlayers) return false;
        players.add(player.getUniqueId());
        teleportToLobby(player);
        checkStart();
        return true;
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        checkEnd();
    }

    private void checkStart() {
        if (players.size() >= minPlayers && state == GameState.WAITING) {
            startCountdown();
        }
    }

    private void startCountdown() {
        state = GameState.STARTING;
        countdown = 30;

        Bukkit.getScheduler().runTaskTimer(WitchWars.getInstance(), task -> {
            if (countdown <= 0) {
                task.cancel();
                startGame();
            } else if (countdown % 10 == 0 || countdown <= 5) {
                broadcast("Game starting in " + countdown + " seconds!");
            }
            countdown--;
        }, 0L, 20L);
    }

    private void broadcast(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public GameState getGameState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public boolean canStart() {
        return players.size() >= minPlayers && state == GameState.WAITING;
    }

    public void checkEndGame() {
        int teamAlive = 0;
        Team winningTeam = null;

        for(Team team : teams){
            if(team.isAlive()){
                teamAlive++;
                winningTeam = team;
            }
        }

        if(teamAlive == 1 && winningTeam != null){
            GameState gameState = GameState.ENDING;

            broadcast(ChatColor.GOLD + "=========================");
            broadcast(ChatColor.WHITE + "Team " + winningTeam.getColor() + winningTeam.getName() + ChatColor.WHITE + " has won the game!");
            broadcast(ChatColor.GOLD + "=========================");

            for(UUID playerId : winningTeam.getPlayers()){
                Player player = Bukkit.getPlayer(playerId);
                if(player != null) {
                    WitchWars.getInstance().getStatsManager().addWin(player);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }
            }

            Bukkit.getScheduler().runTaskLater(WitchWars.getInstance(), this::resetGame, 200L);
        }
         else if (teamAlive == 0){
             broadcast(ChatColor.RED + "Game ended in a draw!");
             resetGame();
        }
    }

    private void resetGame(){

        GameState gameState = GameState.WAITING;

        for(UUID playerId : players){
            Player player = Bukkit.getPlayer(playerId);

            if(player != null){
                player.teleport(lobbySpawn);
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.setHealth(20.0);
                player.setFoodLevel(20);

                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
            }
        }

        for(Team team : teams){
            team.reset();
        }

        WitchWars.getInstance().getGeneratorManager().resetGenerators();

        players.clear();

        WitchWars.getInstance().getScoreboardManager().resetScoreboard(this);
    }
}
