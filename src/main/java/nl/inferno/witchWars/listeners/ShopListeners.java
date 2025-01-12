package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import nl.inferno.witchWars.shop.ShopItem;
import nl.inferno.witchWars.shop.ShopManager;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListeners implements Listener {
    private WitchWars plugin;
    private final ShopManager shopManager;
    public ShopListeners(WitchWars plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @EventHandler
    public void onShopInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Piglin) {
            Piglin piglin = (Piglin) event.getRightClicked();
            if (piglin.getCustomName() != null) {
                event.setCancelled(true);
                plugin.getShopManager().openShop(event.getPlayer(), piglin);
            }
        }
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals("§6Shop")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked != null && clicked.hasItemMeta()) {
            plugin.getShopManager().handlePurchase(player, clicked);
        }
    }}
