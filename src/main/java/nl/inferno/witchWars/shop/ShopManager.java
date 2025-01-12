package nl.inferno.witchWars.shop;

import nl.inferno.witchWars.game.Team;
import nl.inferno.witchWars.shop.ShopItem;
import org.bukkit.*;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopManager {
    private final Map<UUID, Shop> shopKeepers = new HashMap<>();
    private final Map<String, ShopItem> items = new HashMap<>();

    public ShopManager() {
        registerShopItems();
    }

    private void registerShopItems() {
        // Weapons
        addShopItem(new ShopItem("Iron Sword", Material.IRON_SWORD, 8, Material.IRON_INGOT));
        addShopItem(new ShopItem("Diamond Sword", Material.DIAMOND_SWORD, 4, Material.DIAMOND));

        // Armor
        addShopItem(new ShopItem("Iron Armor", Material.IRON_CHESTPLATE, 12, Material.IRON_INGOT));
        addShopItem(new ShopItem("Diamond Armor", Material.DIAMOND_CHESTPLATE, 6, Material.DIAMOND));

        // Tools
        addShopItem(new ShopItem("Pickaxe", Material.IRON_PICKAXE, 4, Material.IRON_INGOT));

        // Blocks
        addShopItem(new ShopItem("Wool", Material.WHITE_WOOL, 4, Material.IRON_INGOT, 16));
    }

    private void addShopItem(ShopItem item) {
        items.put(item.getName(), item);
    }


    public void spawnShopkeeper(Location location, Team team) {
        Piglin piglin = location.getWorld().spawn(location, Piglin.class);
        piglin.setCustomName(team.getColor() + "Shop");
        piglin.setCustomNameVisible(true);
        piglin.setImmuneToZombification(true);
        piglin.setAI(false);
        piglin.setInvulnerable(true);

        shopKeepers.put(piglin.getUniqueId(), new Shop(team));
    }

    public void openShop(Player player, Piglin shopkeeper) {
        Shop shop = shopKeepers.get(shopkeeper.getUniqueId());
        if (shop != null) {
            Inventory inv = createShopInventory(player);
            player.openInventory(inv);
        }
    }

    private Inventory createShopInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Shop");

        for (ShopItem item : items.values()) {
            inv.addItem(item.createDisplayItem());
        }

        return inv;
    }

    public void handlePurchase(Player player, ItemStack clicked) {
        ShopItem shopItem = getShopItemFromDisplay(clicked);
        if (shopItem != null) {
            ItemStack currency = new ItemStack(shopItem.getCurrency());
            if (hasEnoughCurrency(player, shopItem.getCost(), shopItem.getCurrency())) {
                removeCurrency(player, shopItem.getCost(), shopItem.getCurrency());
                ItemStack reward = new ItemStack(shopItem.getItem(), shopItem.getAmount());
                player.getInventory().addItem(reward);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "Successfully purchased " + shopItem.getName());
            } else {
                player.sendMessage(ChatColor.RED + "You don't have enough resources!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }

    private ShopItem getShopItemFromDisplay(ItemStack clicked) {
        if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            for (ShopItem item : items.values()) {
                if (name.equals(item.getName())) {
                    return item;
                }
            }
        }
        return null;
    }


    private boolean hasEnoughCurrency(Player player, int cost, Material currency) {
        return player.getInventory().contains(currency, cost);
    }

    private void removeCurrency(Player player, int cost, Material currency) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = cost;

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == currency) {
                if (item.getAmount() <= remaining) {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
            }
        }
    }
}
