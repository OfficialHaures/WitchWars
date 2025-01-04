package nl.inferno.witchWars.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {
    public void updateScoreboard(Player player, Arena arena) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("witchwars", "dummy", "§6§lWitchWars");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = 15;
        obj.getScore("§7§m------------------").setScore(score--);
        obj.getScore("§fTeam: " + arena.getPlayerTeam(player).getColor() + arena.getPlayerTeam(player).getName()).setScore(score--);
        obj.getScore("§fWitch Health: §c" + arena.getPlayerTeam(player).getWitch().getHealth()).setScore(score--);
        obj.getScore("§fKills: §a" + arena.getPlayerKills(player)).setScore(score--);

        player.setScoreboard(board);
    }
}
