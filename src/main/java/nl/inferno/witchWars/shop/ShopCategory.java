package nl.inferno.witchWars.shop;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ShopCategory {
    private final String name;
    private final List<ShopItem> items;
    private final int slot;
    private final ItemStack displayItem;

    public ShopCategory(String name, int slot, ItemStack displayItem) {
        this.name = name;
        this.items = new ArrayList<>();
        this.slot = slot;
        this.displayItem = displayItem;
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

    public List<ShopItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }
}
