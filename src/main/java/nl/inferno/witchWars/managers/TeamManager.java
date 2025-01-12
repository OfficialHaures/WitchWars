package nl.inferno.witchWars.managers;

import nl.inferno.witchWars.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TeamManager {
    private final HashMap<String, Team> teams = new HashMap<>();
    private final HashMap<Player, Team> playerTeams = new HashMap<Player, Team>();

    public void createTeam(String name, ChatColor color) {
        teams.put(name, new Team(name, color));
    }

    public void addPlayerToTeam(Player player, String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            playerTeams.put(player, team);
            team.addPlayer(player);
        }
    }

    public Team getPlayerTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.getPlayers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public boolean areOnSameTeam(Player damager, Player victim) {
        Team damagerTeam = getPlayerTeam(damager);
        Team victimTeam = getPlayerTeam(victim);

        return damagerTeam != null && damagerTeam.equals(victimTeam);
    }
}
