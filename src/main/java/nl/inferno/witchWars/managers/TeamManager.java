package nl.inferno.witchWars.managers;

import nl.inferno.witchWars.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeamManager {
    private final HashMap<String, Team> teams = new HashMap<>();
    private final HashMap<UUID, Team> playerTeams = new HashMap<>();

    public void createTeam(String name, ChatColor color) {
        teams.put(name, new Team(name, color));
    }

    public void addPlayerToTeam(UUID player, String teamName) {
        Team team = teams.get(teamName);
        if (team != null) {
            playerTeams.put(player, team);
            team.addPlayer(player);
        }
    }

    public Team getPlayerTeam(Player player) {
        for(Team team: teams){
            if(team.getPlayers().contains(player.getUniqueId())){
                return team;
            }
        }
        return null;
    }
}
