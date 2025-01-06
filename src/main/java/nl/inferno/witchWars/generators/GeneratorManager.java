package nl.inferno.witchWars.generators;

import nl.inferno.witchWars.WitchWars;
import nl.inferno.witchWars.game.Generator;
import nl.inferno.witchWars.game.GeneratorTier;
import org.bukkit.*;
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
        addGeneratorType(Material.IRON_INGOT, 2, GeneratorTier.TIER1);
        addGeneratorType(Material.GOLD_INGOT, 5, GeneratorTier.TIER2);
        addGeneratorType(Material.EMERALD, 20, GeneratorTier.TIER3);
        addGeneratorType(Material.DIAMOND, 30, GeneratorTier.TIER4);
    }

    private void addGeneratorType(Material material, int spawnRate, GeneratorTier generatorTier) {
        Generator generator = new Generator(null, material, generatorTier);
        generators.put(generator.getLocation(), generator);
    }

    public void createGenerator(Location location, Material material, GeneratorTier tier) {
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

    public void startGenerators(String name) {
        for(Generator generator : generators.values()){
            if(generator.getLocation().getWorld().getName().equals(name)){
                generator.setEnabled(true);
                generator.start();
            }
        }
    }
}
