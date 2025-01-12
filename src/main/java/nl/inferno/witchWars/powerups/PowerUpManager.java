package nl.inferno.witchWars.powerups;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PowerUpManager {
    private final Map<String, PowerUp> powerUps;
    private final Map<UUID, Set<PowerUp>> activePowerUps;
    private final List<Location> spawnLocations;

    public PowerUpManager() {
        this.powerUps = new HashMap<>();
        this.activePowerUps = new HashMap<>();
        this.spawnLocations = new ArrayList<>();
        registerDefaultPowerUps();
    }

    private void registerDefaultPowerUps() {
        registerPowerUp(new PowerUp("Speed Boost", 30) {
            @Override
            public void apply(Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
            }
        });

        registerPowerUp(new PowerUp("Jump Boost", 30) {
            @Override
            public void apply(Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 600, 1));
            }
        });

        registerPowerUp(new PowerUp("Strength", 45) {
            @Override
            public void apply(Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 0));
            }
        });

        registerPowerUp(new PowerUp("Witch Shield", 60) {
            @Override
            public void apply(Player player) {
                WitchWars.getInstance().getTeamManager()
                    .getPlayerTeam(player)
                    .getWitch()
                    .addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 2));
            }
        });
    }

    private void registerPowerUp(PowerUp powerUp) {
        powerUps.put(powerUp.getName(), powerUp);
    }

    public void spawnRandomPowerUp() {
        if (spawnLocations.isEmpty()) return;

        Random random = new Random();
        Location location = spawnLocations.get(random.nextInt(spawnLocations.size()));
        PowerUp powerUp = getRandomPowerUp();

        powerUp.spawn(location);
    }

    public void applyPowerUp(Player player, String powerUpName) {
        PowerUp powerUp = powerUps.get(powerUpName);
        if (powerUp != null) {
            powerUp.apply(player);
            trackPowerUp(player, powerUp);
        }
    }

    private void trackPowerUp(Player player, PowerUp powerUp) {
        activePowerUps.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(powerUp);

        Bukkit.getScheduler().runTaskLater(WitchWars.getInstance(), () -> {
            removePowerUp(player, powerUp);
        }, powerUp.getDuration() * 20L);
    }

    private void removePowerUp(Player player, PowerUp powerUp) {
        Set<PowerUp> playerPowerUps = activePowerUps.get(player.getUniqueId());
        if (playerPowerUps != null) {
            playerPowerUps.remove(powerUp);
        }
    }

    public void addSpawnLocation(Location location) {
        spawnLocations.add(location);
    }

    public Set<PowerUp> getActivePowerUps(Player player) {
        return activePowerUps.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    private PowerUp getRandomPowerUp() {
        List<PowerUp> availablePowerUps = new ArrayList<>(powerUps.values());
        return availablePowerUps.get(new Random().nextInt(availablePowerUps.size()));
    }

    public boolean isPowerUp(@NotNull Item item) {
        ItemStack itemStack = item.getItemStack();
        return powerUps.values().stream()
            .anyMatch(powerUp -> powerUp.getDisplayItem().isSimilar(itemStack));
    }

    public void handlePowerUpPickup(@NotNull Item item, @NotNull Player player) {
        ItemStack itemStack = item.getItemStack();

        for (PowerUp powerUp : powerUps.values()) {
            if (powerUp.getDisplayItem().isSimilar(itemStack)) {
                powerUp.apply(player);
                item.remove();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "You collected a " + powerUp.getName() + "!");
                break;
            }
        }
    }
}
