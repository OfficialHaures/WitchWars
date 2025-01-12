package nl.inferno.witchWars.game;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Generator {
    private final Location location;
    private final Material material;
    private final int spawnRate;
    private boolean isActive;
    private BukkitTask task;
    private GeneratorTier tier;

    public Generator(Location location, Material material, GeneratorTier tier) {
        this.location = location;
        this.material = material;
        this.tier = tier;
        this.spawnRate = tier.getSpawnRate();
        this.isActive = false;
    }

    public void start() {
        if (isActive) return;
        isActive = true;

        task = Bukkit.getScheduler().runTaskTimer(WitchWars.getInstance(), () -> {
            if (!isActive) return;
            spawnResource();
            playEffect();
        }, spawnRate * 20L, spawnRate * 20L);
    }

    public void stop() {
        isActive = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void spawnResource() {
        World world = location.getWorld();
        if (world != null) {
            Location spawnLoc = location.clone().add(0.5, 0.5, 0.5);
            ItemStack item = new ItemStack(material, tier.getAmount());
            world.dropItem(spawnLoc, item);
        }
    }

    private void playEffect() {
        location.getWorld().spawnParticle(
            Particle.VILLAGER_HAPPY,
            location.clone().add(0.5, 1.0, 0.5),
            10, 0.2, 0.2, 0.2, 0
        );
    }

    public void upgradeTier() {
        switch (tier) {
            case TIER1:
                tier = GeneratorTier.TIER2;
                break;
            case TIER2:
                tier = GeneratorTier.TIER3;
                break;
            case TIER3:
                tier = GeneratorTier.TIER4;
                break;
        }

        if (isActive) {
            stop();
            start();
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    public Location getLocation() { return location; }
    public Material getMaterial() { return material; }
    public boolean isActive() { return isActive; }
    public GeneratorTier getTier() { return tier; }

    public boolean isEnabled() {
        return isActive;
    }
}

