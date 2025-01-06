package nl.inferno.witchWars.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {
    private final String name;
    private final ChatColor color;
    private final Set<UUID> players;
    private Witch teamWitch;
    private Location spawnPoint;
    private Location witchSpawnPoint;
    private boolean alive;
    private int level;
    private double witchHealth;

    public Team(String name, ChatColor color, Location spawnPoint, Location witchSpawnPoint) {
        this.name = name;
        this.color = color;
        this.players = new HashSet<>();
        this.spawnPoint = spawnPoint;
        this.witchSpawnPoint = witchSpawnPoint;
        this.alive = true;
        this.level = 1;
        this.witchHealth = 100.0;
    }

    public void addPlayer(UUID player) {
        players.add(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.teleport(spawnPoint);
            p.sendMessage(color + "You joined team " + name);
        }
    }

    public void spawnWitch() {
        teamWitch = witchSpawnPoint.getWorld().spawn(witchSpawnPoint, Witch.class);
        teamWitch.setCustomName(color + name + "'s Witch");
        teamWitch.setCustomNameVisible(true);
        teamWitch.setMaxHealth(witchHealth);
        teamWitch.setHealth(witchHealth);

        applyWitchEffects();
    }

    private void applyWitchEffects() {
        teamWitch.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, level - 1));
        teamWitch.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
    }

    public void upgradeTeam() {
        level++;
        witchHealth += 25.0;
        if (teamWitch != null && teamWitch.isValid()) {
            teamWitch.setMaxHealth(witchHealth);
            teamWitch.setHealth(witchHealth);
            applyWitchEffects();
        }
    }

    public void eliminateTeam() {
        alive = false;
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }



    public void removePlayer(@NotNull UUID uniqueId) {
        players.remove(uniqueId);

        if(players.isEmpty()){
            alive = false;
            if(teamWitch != null){
                teamWitch.remove();
            }
        }
    }

    public void reset() {
        players.clear();
        alive = true;

        if(teamWitch != null){
            teamWitch.remove();
            teamWitch = null;
        }

        level = 1;
        witchHealth = 100.0;
    }

    public @NotNull Location getSpawnPoint() {
        return spawnPoint.clone();
    }


    // Getters and setters
    public String getName() { return name; }
    public ChatColor getColor() { return color; }
    public Set<UUID> getPlayers() { return players; }
    public boolean isAlive() { return alive; }
    public int getLevel() { return level; }
    public Witch getWitch() { return teamWitch; }
}
