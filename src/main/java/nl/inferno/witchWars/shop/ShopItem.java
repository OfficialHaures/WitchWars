package nl.inferno.witchWars.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private final String name;
    private final Material material;
    private final int cost;
    private final Material costType;
    private final int amount;
    private final List<String> description;
    private final int slot;

    public ShopItem(String name, Material material, int cost, Material costType, int amount, int slot) {
        this.name = name;
        this.material = material;
        this.cost = cost;
        this.costType = costType;
        this.amount = amount;
        this.slot = slot;
        this.description = new ArrayList<>();
    }

    public ItemStack createDisplayItem() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6" + name);

            List<String> lore = new ArrayList<>();
            lore.add("§7Cost: §f" + cost + " " + costType.name());
            lore.addAll(description);

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void addDescription(String line) {
        description.add("§7" + line);
    }

    // Getters
    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public int getCost() { return cost; }
    public Material getCostType() { return costType; }
    public int getAmount() { return amount; }
    public List<String> getDescription() { return description; }
    public int getSlot() { return slot; }
}
