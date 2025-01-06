package nl.inferno.witchWars.stats;

public class PlayerStats {
    private int kills;
    private int deaths;
    private int wins;
    private int gamesPlayed;
    private int witchKills;
    private int resourcesCollected;
    private int spellsCast;

    public PlayerStats() {
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.gamesPlayed = 0;
        this.witchKills = 0;
        this.resourcesCollected = 0;
        this.spellsCast = 0;
    }

    public void addKill() { kills++; }
    public void addDeath() { deaths++; }
    public void addWin() { wins++; }
    public void addGame() { gamesPlayed++; }
    public void addWitchKill() { witchKills++; }
    public void addResources(int amount) { resourcesCollected += amount; }
    public void addSpellCast() { spellsCast++; }

    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public double getWinRatio() {
        return gamesPlayed == 0 ? 0 : (double) wins / gamesPlayed;
    }

    // Getters
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getWins() { return wins; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getWitchKills() { return witchKills; }
    public int getResourcesCollected() { return resourcesCollected; }
    public int getSpellsCast() { return spellsCast; }
}