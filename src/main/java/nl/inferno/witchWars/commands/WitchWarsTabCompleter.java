package nl.inferno.witchWars.commands;

import nl.inferno.witchWars.WitchWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;

public class WitchWarsTabCompleter implements TabCompleter {
    private final WitchWars plugin;

    public WitchWarsTabCompleter(WitchWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("join");
            completions.add("leave");
            completions.add("stats");

            if (sender.hasPermission("witchwars.admin")) {
                completions.add("createarena");
                completions.add("setspawn");
                completions.add("setwitch");
                completions.add("setgenerator");
                completions.add("setminplayers");
                completions.add("setmaxplayers");
                completions.add("addteam");
                completions.add("setlobby");
            }
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "join":
                case "createarena":
                case "setspawn":
                case "setwitch":
                case "setgenerator":
                case "setminplayers":
                case "setmaxplayers":
                case "setlobby":
                    plugin.getGameManager().getArenas().forEach(arena ->
                        completions.add(arena.getName()));
                    break;
            }
        } else if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "addteam":
                    for (ChatColor color : ChatColor.values()) {
                        if (color.isColor()) {
                            completions.add(color.name().toLowerCase());
                        }
                    }
                    break;
                case "setgenerator":
                    completions.add("IRON");
                    completions.add("GOLD");
                    completions.add("EMERALD");
                    completions.add("DIAMOND");
                    break;
            }
        }

        return completions;
    }
}
