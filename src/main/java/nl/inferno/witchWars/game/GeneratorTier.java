package nl.inferno.witchWars.game;

public enum GeneratorTier {
    TIER1(2, 1),
    TIER2(5, 1),
    TIER3(10, 1),
    TIER4(15, 2);

    private final int spawnRate;
    private final int amount;

    GeneratorTier(int spawnRate, int amount) {
        this.spawnRate = spawnRate;
        this.amount = amount;
    }

    public int getSpawnRate() { return spawnRate; }
    public int getAmount() { return amount; }
}
