package nl.inferno.witchWars.powerups;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class PowerUp {
    private final String name;
    private final int duration;
    private ArmorStand display;
    private Location location;
    private boolean active;
    private final Material displayMaterial;
    private float rotationAngle;

    public PowerUp(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.active = false;
        this.displayMaterial = getDisplayMaterial();
        this.rotationAngle = 0f;
    }

    public void spawn(Location location) {
        this.location = location;
        this.active = true;

        // Create floating display
        display = location.getWorld().spawn(location, ArmorStand.class);
        display.setVisible(false);
        display.setGravity(false);
        display.setSmall(true);
        display.setHelmet(new ItemStack(displayMaterial));

        // Start animation
        startFloatingAnimation();
    }

    private void startFloatingAnimation() {
        Bukkit.getScheduler().runTaskTimer(WitchWars.getInstance(), () -> {
            if (!active || display == null) return;

            rotationAngle += 10f;
            if (rotationAngle >= 360f) rotationAngle = 0f;

            double y = Math.sin(Math.toRadians(rotationAngle)) * 0.2;
            Location newLoc = location.clone().add(0, y, 0);
            display.teleport(newLoc);

            // Spawn particles
            location.getWorld().spawnParticle(
                Particle.SPELL_WITCH,
                location.clone().add(0, 1, 0),
                3, 0.2, 0.2, 0.2, 0
            );
        }, 0L, 2L);
    }

    public void collect(Player player) {
        if (!active) return;

        active = false;
        if (display != null) {
            display.remove();
        }

        apply(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.sendMessage("§aYou collected " + name + " power-up!");
    }

    private Material getDisplayMaterial() {
        switch (name.toLowerCase()) {
            case "speed boost":
                return Material.SUGAR;
            case "jump boost":
                return Material.RABBIT_FOOT;
            case "strength":
                return Material.BLAZE_POWDER;
            case "witch shield":
                return Material.SHIELD;
            default:
                return Material.NETHER_STAR;
        }
    }

    // Abstract method to be implemented by specific power-ups
    public abstract void apply(Player player);

    // Getters
    public String getName() { return name; }
    public int getDuration() { return duration; }
    public boolean isActive() { return active; }
    public Location getLocation() { return location; }
}