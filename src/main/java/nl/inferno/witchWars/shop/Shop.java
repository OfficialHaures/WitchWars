package nl.inferno.witchWars.shop;

import nl.inferno.witchWars.game.Team;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    private final Team team;
    private final Map<String, ShopCategory> categories;

    public Shop(Team team) {
        this.team = team;
        this.categories = new HashMap<>();
        setupCategories();
    }

    private void setupCategories() {
        // Combat
        ShopCategory combat = new ShopCategory("Combat", Material.IRON_SWORD);
        combat.addItem(new ShopItem("Iron Sword", Material.IRON_SWORD, 8, Material.IRON_INGOT));
        combat.addItem(new ShopItem("Diamond Sword", Material.DIAMOND_SWORD, 4, Material.DIAMOND));
        categories.put("combat", combat);

        // Armor
        ShopCategory armor = new ShopCategory("Armor", Material.IRON_CHESTPLATE);
        armor.addItem(new ShopItem("Iron Armor", Material.IRON_CHESTPLATE, 12, Material.IRON_INGOT));
        armor.addItem(new ShopItem("Diamond Armor", Material.DIAMOND_CHESTPLATE, 6, Material.DIAMOND));
        categories.put("armor", armor);

        // Blocks
        ShopCategory blocks = new ShopCategory("Blocks", Material.WHITE_WOOL);
        blocks.addItem(new ShopItem("Wool", Material.WHITE_WOOL, 4, Material.IRON_INGOT, 16));
        blocks.addItem(new ShopItem("Wood", Material.OAK_PLANKS, 4, Material.IRON_INGOT, 16));
        categories.put("blocks", blocks);

        // Tools
        ShopCategory tools = new ShopCategory("Tools", Material.IRON_PICKAXE);
        tools.addItem(new ShopItem("Iron Pickaxe", Material.IRON_PICKAXE, 4, Material.IRON_INGOT));
        tools.addItem(new ShopItem("Shears", Material.SHEARS, 2, Material.IRON_INGOT));
        categories.put("tools", tools);
    }

    public Team getTeam() {
        return team;
    }

    public Map<String, ShopCategory> getCategories() {
        return categories;
    }

    public ShopCategory getCategory(String name) {
        return categories.get(name.toLowerCase());
    }
}
