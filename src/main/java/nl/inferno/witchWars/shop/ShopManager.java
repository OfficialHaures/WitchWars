package nl.inferno.witchWars.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ShopManager {
    private final Map<String, ShopCategory> categories;
    private final Map<Material, Integer> prices;

    public ShopManager() {
        this.categories = new HashMap<>();
        this.prices = new HashMap<>();
        setupDefaultShop();
    }

    private void setupDefaultShop() {
        // Combat Category
        ShopCategory combat = new ShopCategory("Combat", 0, new ItemStack(Material.IRON_SWORD));
        combat.addItem(new ShopItem("Iron Sword", Material.IRON_SWORD, 10, Material.IRON_INGOT, 1, 0));
        combat.addItem(new ShopItem("Diamond Sword", Material.DIAMOND_SWORD, 5, Material.EMERALD, 1, 1));
        categories.put("combat", combat);

        // Blocks Category
        ShopCategory blocks = new ShopCategory("Blocks", 1, new ItemStack(Material.WHITE_WOOL));
        blocks.addItem(new ShopItem("Wool", Material.WHITE_WOOL, 4, Material.IRON_INGOT, 16, 0));
        blocks.addItem(new ShopItem("Wood", Material.OAK_PLANKS, 4, Material.GOLD_INGOT, 16, 0));
        categories.put("blocks", blocks);

        // Tools Category
        ShopCategory tools = new ShopCategory("Tools", 2, new ItemStack(Material.IRON_PICKAXE));
        tools.addItem(new ShopItem("Pickaxe", Material.IRON_PICKAXE, 10, Material.IRON_INGOT, 1, 0));
        tools.addItem(new ShopItem("Shears", Material.SHEARS, 20, Material.IRON_INGOT, 1, 0));
        categories.put("tools", tools);

        // Potions Category
        ShopCategory potions = new ShopCategory("Potions", 3, new ItemStack(Material.POTION));
        potions.addItem(new ShopItem("Speed Potion", Material.POTION, 1, Material.EMERALD, 1, 0));
        potions.addItem(new ShopItem("Jump Potion", Material.POTION, 1, Material.EMERALD, 1, 0));
        categories.put("potions", potions);
    }

    public boolean purchaseItem(Player player, ShopItem item) {
        if (hasEnoughResources(player, item)) {
            removeResources(player, item);
            giveItem(player, item);
            player.sendMessage("§aSuccessfully purchased " + item.getName());
            return true;
        }
        player.sendMessage("§cNot enough resources!");
        return false;
    }

    private boolean hasEnoughResources(Player player, ShopItem item) {
        return player.getInventory().contains(item.getCostType(), item.getCost());
    }

    private void removeResources(Player player, ShopItem item) {
        ItemStack cost = new ItemStack(item.getCostType(), item.getCost());
        player.getInventory().removeItem(cost);
    }

    private void giveItem(Player player, ShopItem item) {
        ItemStack itemStack = new ItemStack(item.getMaterial(), item.getAmount());
        player.getInventory().addItem(itemStack);
    }

    public Map<String, ShopCategory> getCategories() {
        return categories;
    }

    public ShopItem getShopItem(@Nullable ItemStack currentItem) {

        if(currentItem == null) return null;

        for(ShopCategory category : categories.values()){
            for(ShopItem item : category.getItems()){
                if(item.getMaterial() == currentItem.getType()){
                    return item;
                }
            }
        }


        return null;
    }
}
