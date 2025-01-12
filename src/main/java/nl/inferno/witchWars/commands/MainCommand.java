package nl.inferno.witchWars.commands;

import nl.inferno.witchWars.WitchWars;
import nl.inferno.witchWars.game.Arena;
import nl.inferno.witchWars.game.GeneratorTier;
import nl.inferno.witchWars.game.Team;
import nl.inferno.witchWars.stats.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class MainCommand implements CommandExecutor {
    private final WitchWars plugin;

    public MainCommand(WitchWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /witchwars join <arena>");
                    return true;
                }
                plugin.getGameManager().joinGame(player, args[1]);
                break;
            case "leave":
                plugin.getGameManager().leaveGame(player);
                break;
            case "stats":
                showStats(player);
                break;
            case "createarena":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /witchwars createarena <name>");
                        return true;
                    }
                    plugin.getGameManager().createArena(args[1]);
                    player.sendMessage("§aArena " + args[1] + " created!");
                }
                break;
            case "forcestart":
                if(hasPermission(player, "witchwars.admin")) {
                    if(args.length < 2) {
                        player.sendMessage("§cUsage: /witchwars forcestart <arena>");
                        return true;
                    }
                    Arena arena = plugin.getGameManager().getArena(args[1]);
                    if(arena != null) {
                        if(arena.getLobbySpawn() != null && arena.getTeams().stream().allMatch(team ->
                                team.getSpawnPoint() != null && team.getWitchSpawn() != null)) {
                            arena.startGame();
                            player.sendMessage("§aForce starting arena " + args[1]);
                        } else {
                            player.sendMessage("§cAll spawn locations must be set before starting the arena!");
                        }
                    } else {
                        player.sendMessage("§cArena not found!");
                    }
                }
                break;

            case "setspawn":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setspawn <arena> <team>");
                        return true;
                    }
                    setTeamSpawn(player, args[1], args[2]);
                }
                break;
            case "setwitch":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setwitch <arena> <team>");
                        return true;
                    }
                    setWitchSpawn(player, args[1], args[2]);
                }
                break;
            case "setgenerator":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setgenerator <arena> <type>");
                        return true;
                    }
                    setGenerator(player, args[1], args[2]);
                }
                break;
            case "setminplayers":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setminplayers <arena> <amount>");
                        return true;
                    }
                    setMinPlayers(args[1], Integer.parseInt(args[2]), player);
                }
                break;
            case "setmaxplayers":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setmaxplayers <arena> <amount>");
                        return true;
                    }
                    setMaxPlayers(args[1], Integer.parseInt(args[2]), player);
                }
                break;
            case "addteam":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 4) {
                        player.sendMessage("§cUsage: /witchwars addteam <arena> <team> <color>");
                        return true;
                    }
                    addTeam(args[1], args[2], args[3], player);
                }
                break;
            case "setlobby":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /witchwars setlobby <arena>");
                        return true;
                    }
                    setLobbySpawn(player, args[1]);
                }
                break;
            case "shop":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars shop <arena> <team>");
                        return true;
                    }
                    Arena arena = plugin.getGameManager().getArena(args[1]);
                    if (arena != null) {
                        Team team = arena.getTeam(args[2]);
                        if (team != null) {
                            plugin.getShopManager().spawnShopkeeper(player.getLocation(), team);
                            player.sendMessage("§aShop spawned for team " + team.getName());
                        } else {
                            player.sendMessage("§cTeam not found!");
                        }
                    } else {
                        player.sendMessage("§cArena not found!");
                    }
                }
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void addTeam(String arenaName, String teamName, String colorName, Player player) {
        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid color! Use valid ChatColor names.");
            return;
        }

        plugin.getConfig().set("arenas." + arenaName + ".teams." + teamName + ".color", colorName);
        plugin.saveConfig();

        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena != null) {
            arena.addTeam(new Team(teamName, color));
            player.sendMessage(ChatColor.GREEN + "Team " + color + teamName + ChatColor.GREEN + " added to arena " + arenaName);
        }
    }


    private void setMaxPlayers(String arenaName, int amount, Player player) {
        plugin.getConfig().set("arenas." + arenaName + ".maxPlayers", amount);
        plugin.saveConfig();
        plugin.getGameManager().getArena(arenaName).setMaxPlayers(amount);
        player.sendMessage(ChatColor.GREEN + "Maximum players for arena " + arenaName + " set to " + amount);
    }

    private void setMinPlayers(String arenaName, int amount, Player player) {
        plugin.getConfig().set("arenas." + arenaName + ".minPlayers", amount);
        plugin.saveConfig();
        plugin.getGameManager().getArena(arenaName).setMinPlayers(amount);
        player.sendMessage(ChatColor.GREEN + "Minimum players for arena " + arenaName + " set to " + amount);
    }


    private void showStats(Player player) {
        PlayerStats stats = plugin.getStatsManager().getStats(player);

        player.sendMessage(ChatColor.GOLD + "=== Your WitchWars Stats ===");
        player.sendMessage(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + stats.getKills());
        player.sendMessage(ChatColor.YELLOW + "Deaths: " + ChatColor.WHITE + stats.getDeaths());
        player.sendMessage(ChatColor.YELLOW + "K/D Ratio: " + ChatColor.WHITE + String.format("%.2f", stats.getKDRatio()));
        player.sendMessage(ChatColor.YELLOW + "Wins: " + ChatColor.WHITE + stats.getWins());
        player.sendMessage(ChatColor.YELLOW + "Games Played: " + ChatColor.WHITE + stats.getGamesPlayed());
        player.sendMessage(ChatColor.YELLOW + "Win Rate: " + ChatColor.WHITE + String.format("%.1f%%", stats.getWinRatio() * 100));
        player.sendMessage(ChatColor.YELLOW + "Witch Kills: " + ChatColor.WHITE + stats.getWitchKills());
        player.sendMessage(ChatColor.YELLOW + "Resources Collected: " + ChatColor.WHITE + stats.getResourcesCollected());
        player.sendMessage(ChatColor.YELLOW + "Spells Cast: " + ChatColor.WHITE + stats.getSpellsCast());
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6§lWitchWars Commands:");
        player.sendMessage("§7--- Player Commands ---");
        player.sendMessage("§f/witchwars join <arena> §7- Join an arena");
        player.sendMessage("§f/witchwars leave §7- Leave current arena");
        player.sendMessage("§f/witchwars stats §7- View your stats");

        if (player.hasPermission("witchwars.admin")) {
            player.sendMessage("§7--- Admin Commands ---");
            player.sendMessage("§f/witchwars forcestart <arena> §7- Force start an arena");
            player.sendMessage("§f/witchwars createarena <name> §7- Create a new arena");
            player.sendMessage("§f/witchwars setspawn <arena> <team> §7- Set team spawn");
            player.sendMessage("§f/witchwars setwitch <arena> <team> §7- Set witch spawn");
            player.sendMessage("§f/witchwars setgenerator <arena> <type> §7- Set generator");
            player.sendMessage("§f/witchwars setminplayers <arena> <amount> §7- Set min players");
            player.sendMessage("§f/witchwars setmaxplayers <arena> <amount> §7- Set max players");
            player.sendMessage("§f/witchwars addteam <arena> <team> <color> §7- Add team");
            player.sendMessage("§f/witchwars setlobby <arena> §7- Set lobby spawn");
        }
    }

    private boolean hasPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return false;
        }
        return true;
    }

    private void setTeamSpawn(Player player, String arena, String team) {
        Location loc = player.getLocation();
        plugin.getGameManager().setTeamSpawn(arena, team, loc);
        player.sendMessage("§aTeam spawn set for " + team + " in arena " + arena);
    }

    private void setWitchSpawn(Player player, String arena, String team) {
        Location loc = player.getLocation();
        plugin.getGameManager().setWitchSpawn(arena, team, loc);
        player.sendMessage("§aWitch spawn set for " + team + " in arena " + arena);
    }

    private void setGenerator(Player player, String arena, String type) {
        Location loc = player.getLocation();
        Material material;
        GeneratorTier tier;

        switch (type.toUpperCase()) {
            case "GOLD":
                material = Material.GOLD_INGOT;
                tier = GeneratorTier.TIER2;
                break;
            case "IRON":
                material = Material.IRON_INGOT;
                tier = GeneratorTier.TIER1;
                break;
            case "EMERALD":
                material = Material.EMERALD;
                tier = GeneratorTier.TIER3;
                break;
            case "DIAMOND":
                material = Material.DIAMOND;
                tier = GeneratorTier.TIER4;
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid generator type! Use: IRON, GOLD, EMERALD, DIAMOND");
                return;
        }
        plugin.getGeneratorManager().createGenerator(loc, material, tier);
        player.sendMessage("§aGenerator " + type + " set in arena " + arena);
    }


    private void setLobbySpawn(Player player, String arena) {
        Location loc = player.getLocation();
        plugin.getGameManager().setLobbySpawn(arena, loc);
        player.sendMessage("§aLobby spawn set for arena " + arena);
    }
}