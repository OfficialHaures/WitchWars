package nl.inferno.witchWars.commands;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

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
            // Player commands
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
                    setMinPlayers(args[1], Integer.parseInt(args[2]));
                }
                break;
            case "setmaxplayers":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 3) {
                        player.sendMessage("§cUsage: /witchwars setmaxplayers <arena> <amount>");
                        return true;
                    }
                    setMaxPlayers(args[1], Integer.parseInt(args[2]));
                }
                break;
            case "addteam":
                if (hasPermission(player, "witchwars.admin")) {
                    if (args.length < 4) {
                        player.sendMessage("§cUsage: /witchwars addteam <arena> <team> <color>");
                        return true;
                    }
                    addTeam(args[1], args[2], args[3]);
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
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6§lWitchWars Commands:");
        player.sendMessage("§7--- Player Commands ---");
        player.sendMessage("§f/witchwars join <arena> §7- Join an arena");
        player.sendMessage("§f/witchwars leave §7- Leave current arena");
        player.sendMessage("§f/witchwars stats §7- View your stats");

        if (player.hasPermission("witchwars.admin")) {
            player.sendMessage("§7--- Admin Commands ---");
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
        plugin.getGeneratorManager().createGenerator(arena, type, loc);
        player.sendMessage("§aGenerator " + type + " set in arena " + arena);
    }

    private void setLobbySpawn(Player player, String arena) {
        Location loc = player.getLocation();
        plugin.getGameManager().setLobbySpawn(arena, loc);
        player.sendMessage("§aLobby spawn set for arena " + arena);
    }
}