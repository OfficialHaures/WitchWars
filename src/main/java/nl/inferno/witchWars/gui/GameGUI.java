package nl.inferno.witchWars.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameGUI {
    private final Inventory inventory;

    public GameGUI(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public void addItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
