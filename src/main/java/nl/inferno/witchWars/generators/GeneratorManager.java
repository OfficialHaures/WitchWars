package nl.inferno.witchWars.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorManager {
    private final Map<Location, Generator> generators;
    private final List<BukkitTask> activeTasks;

    public GeneratorManager() {
        this.generators = new HashMap<>();
        this.activeTasks = new ArrayList<>();
        setupDefaultGenerators();
    }

    private void setupDefaultGenerators() {
        addGeneratorType(Material.IRON_INGOT, 2, Tier.TIER1);
        addGeneratorType(Material.GOLD_INGOT, 5, Tier.TIER2);
        addGeneratorType(Material.EMERALD, 20, Tier.TIER3);
        addGeneratorType(Material.DIAMOND, 30, Tier.TIER4);
    }

    public void createGenerator(Location location, Material material, Tier tier) {
        Generator generator = new Generator(location, material, tier);
        generators.put(location, generator);
    }

    public void startAllGenerators() {
        generators.values().forEach(this::startGenerator);
    }

    public void startGenerator(Generator generator) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(WitchWars.getInstance(), () -> {
            if (generator.isEnabled()) {
                spawnResource(generator);
                playGeneratorEffect(generator.getLocation());
            }
        }, 0L, generator.getTier().getSpawnRate() * 20L);

        activeTasks.add(task);
    }

    private void spawnResource(Generator generator) {
        World world = generator.getLocation().getWorld();
        Location spawnLoc = generator.getLocation().clone().add(0.5, 0.5, 0.5);
        ItemStack item = new ItemStack(generator.getMaterial());

        world.dropItem(spawnLoc, item);
    }

    private void playGeneratorEffect(Location location) {
        location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
            location.clone().add(0.5, 0.5, 0.5),
            10, 0.3, 0.3, 0.3, 0);
    }

    public void upgradeGenerator(Location location) {
        Generator generator = generators.get(location);
        if (generator != null) {
            generator.upgradeTier();
        }
    }

    public void stopAllGenerators() {
        activeTasks.forEach(BukkitTask::cancel);
        activeTasks.clear();
        generators.values().forEach(generator -> generator.setEnabled(false));
    }

    public Generator getGenerator(Location location) {
        return generators.get(location);
    }

    public Map<Location, Generator> getGenerators() {
        return generators;
    }
}