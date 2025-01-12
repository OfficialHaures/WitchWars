package nl.inferno.witchWars.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private final String name;
    private final Material item;
    private final int cost;
    private final Material currency;
    private final int amount;

    public ShopItem(String name, Material item, int cost, Material currency, int amount) {
        this.name = name;
        this.item = item;
        this.cost = cost;
        this.currency = currency;
        this.amount = amount;
    }

    public ShopItem(String name, Material item, int cost, Material currency) {
        this(name, item, cost, currency, 1);
    }

    public ItemStack createDisplayItem() {
        ItemStack displayItem = new ItemStack(item, amount);
        ItemMeta meta = displayItem.getItemMeta();

        meta.setDisplayName("§6" + name);
        List<String> lore = new ArrayList<>();
        lore.add("§7Cost: §f" + cost + " " + currency.name());
        lore.add("");
        lore.add("§eClick to purchase!");

        meta.setLore(lore);
        displayItem.setItemMeta(meta);

        return displayItem;
    }

    // Getters
    public String getName() { return name; }
    public Material getMaterial() { return item; }
    public int getCost() { return cost; }
    public Material getCostType() { return currency; }
    public int getAmount() { return amount; }

    public Material getCurrency() {
        return currency;
    }

    public @NotNull Material getItem() {
        return item;
    }
}