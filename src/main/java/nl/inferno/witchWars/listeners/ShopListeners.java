package nl.inferno.witchWars.listeners;

import nl.inferno.witchWars.WitchWars;
import nl.inferno.witchWars.shop.ShopItem;
import nl.inferno.witchWars.shop.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class ShopListeners implements Listener {
    private final ShopManager shopManager;

    public ShopListeners(WitchWars plugin) {
        this.shopManager = plugin.getShopManager();
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().contains("WitchWars Shop")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null) {
            ShopItem item = shopManager.getShopItem(event.getCurrentItem());
            if (item != null) {
                shopManager.purchaseItem(player, item);
            }
        }
    }
}
