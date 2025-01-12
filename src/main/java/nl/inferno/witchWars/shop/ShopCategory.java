package nl.inferno.witchWars.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ShopCategory {
    private final String name;
    private final List<ShopItem> items;
    private final Material displayMaterial;
    private final int slot;

    public ShopCategory(String name, Material displayMaterial) {
        this.name = name;
        this.displayMaterial = displayMaterial;
        this.items = new ArrayList<>();
        this.slot = items.size();
    }

    public void addItem(ShopItem item) {
        items.add(item);
    }

    public void removeItem(ShopItem item) {
        items.remove(item);
    }

    public ShopItem getItem(int slot) {
        return items.get(slot);
    }

    public ItemStack getDisplayItem() {
        ItemStack display = new ItemStack(displayMaterial);
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + name);
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to view " + name + " items");
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        return display;
    }

    public String getName() {
        return name;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public int getSlot() {
        return slot;
    }
}