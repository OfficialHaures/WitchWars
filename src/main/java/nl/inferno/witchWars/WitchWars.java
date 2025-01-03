package nl.inferno.witchWars;

import nl.inferno.witchWars.commands.*;
import nl.inferno.witchWars.generators.GeneratorManager;
import nl.inferno.witchWars.managers.*;
import nl.inferno.witchWars.listeners.*;
import nl.inferno.witchWars.powerups.PowerUpManager;
import nl.inferno.witchWars.shop.ShopManager;
import nl.inferno.witchWars.stats.StatsManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WitchWars extends JavaPlugin {
    private static WitchWars instance;
    private GameManager gameManager;
    private TeamManager teamManager;
    private ShopManager shopManager;
    private GeneratorManager generatorManager;
    private StatsManager statsManager;
    private PowerUpManager powerUpManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load configuration
        saveDefaultConfig();

        // Initialize managers
        this.gameManager = new GameManager(this);
        this.teamManager = new TeamManager();
        this.shopManager = new ShopManager();
        this.generatorManager = new GeneratorManager();
        this.statsManager = new StatsManager();
        this.powerUpManager = new PowerUpManager();

        // Register commands
        getCommand("witchwars").setExecutor(new MainCommand(instance));

        // Register events
        getServer().getPluginManager().registerEvents(new GameListeners(this), this);
        getServer().getPluginManager().registerEvents(new ShopListeners(instance), this);
        getServer().getPluginManager().registerEvents(new TeamListeners(this), this);
        getServer().getPluginManager().registerEvents(new PowerUpListeners(this), this);

        getLogger().info("WitchWars enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save all data
        statsManager.saveAllData();
        gameManager.stopAllGames();

        getLogger().info("WitchWars disabled successfully!");
    }

    public static WitchWars getInstance() {
        return instance;
    }

    // Getters for all managers
    public GameManager getGameManager() { return gameManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public ShopManager getShopManager() { return shopManager; }
    public GeneratorManager getGeneratorManager() { return generatorManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public PowerUpManager getPowerUpManager() { return powerUpManager; }
}