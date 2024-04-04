package com.top1.portfel.gui;

import com.top1.portfel.config.ConfigManager;
import com.top1.portfel.config.YamalDataManager;
import com.top1.portfel.other.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemShopGui implements Listener {
    private final String guiTitle;
    private final int guiSlots;
    private final Material backgroundMaterial;
    private final Map<Integer, ShopItem> shopItems;
    private final YamalDataManager dataManager;
    private final Set<Player> playersInTransaction = new HashSet<>();

    public ItemShopGui(ConfigManager configManager, YamalDataManager dataManager, Plugin plugin) {
        this.dataManager = dataManager;
        guiTitle = ChatHelper.colored(configManager.getConfig().getString("ItemShopGui.options.title", "&cSklep"));
        guiSlots = configManager.getConfig().getInt("ItemShopGui.options.slots", 27);
        String backgroundMaterialName = configManager.getConfig().getString("ItemShopGui.options.background", "WHITE_STAINED_GLASS_PANE");
        backgroundMaterial = Material.getMaterial(backgroundMaterialName);
        shopItems = new HashMap<>();
        for (String slot : configManager.getConfig().getConfigurationSection("ItemShopGui.gui").getKeys(false)) {
            String path = "ItemShopGui.gui." + slot;
            Material material = Material.getMaterial(configManager.getConfig().getString(path + ".material", "DIAMOND"));
            int count = configManager.getConfig().getInt(path + ".count", 1);
            boolean enchanted = configManager.getConfig().getBoolean(path + ".enchanted", false);
            String name = ChatHelper.colored(configManager.getConfig().getString(path + ".name", "&cNazwa przedmiotu"));
            List<String> lore = ChatHelper.colored(configManager.getConfig().getStringList(path + ".lore"));
            boolean closeGui = configManager.getConfig().getBoolean(path + ".closegui", false);
            int cost = configManager.getConfig().getInt(path + ".cost", 0);
            List<String> commands = configManager.getConfig().getStringList(path + ".commands");

            ItemStack item = new ItemStack(material, count);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);
            if (enchanted) {
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            }
            item.setItemMeta(itemMeta);

            ShopItem shopItem = new ShopItem(item, closeGui, cost, commands);
            shopItems.put(Integer.parseInt(slot), shopItem);
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, guiSlots, guiTitle);

        ItemStack backgroundItem = new ItemStack(backgroundMaterial);
        ItemMeta backgroundMeta = backgroundItem.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        backgroundItem.setItemMeta(backgroundMeta);

        for (int i = 0; i < guiSlots; i++) {
            inventory.setItem(i, backgroundItem);
        }

        for (Map.Entry<Integer, ShopItem> entry : shopItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player) event.getInventory().getHolder();
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
                if (playersInTransaction.contains(player)) {
                    return;
                }
                playersInTransaction.add(player);
                int slotClicked = event.getSlot();
                handlePurchase(player, slotClicked);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            playersInTransaction.remove(player);
        }
    }

    public void handlePurchase(Player player, int slot) {
        if (shopItems.containsKey(slot)) {
            ShopItem shopItem = shopItems.get(slot);
            int cost = shopItem.getCost();
            String playerName = player.getName();

            if (dataManager.getPlayerBalance(playerName) >= cost) {
                dataManager.setPlayerBalance(playerName, dataManager.getPlayerBalance(playerName) - cost);

                for (String command : shopItem.getCommands()) {
                    String processedCommand = command.replace("{player}", playerName);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
                }

                player.sendTitle(ChatHelper.colored("&aPomyslnie zakupiono"), ChatHelper.colored("&aPomyslnie zakupiles przedmiot!"));
                player.sendMessage(ChatHelper.colored("&aPomyslnie zakupiles przedmiot"));
                if (shopItem.shouldCloseGui()) {
                    player.closeInventory();
                }
            } else {
                player.sendMessage(ChatHelper.colored("&cNie masz wystarczająco środków, aby to kupić!"));
            }
        }
    }
}

class ShopItem {
    private final ItemStack item;
    private final boolean closeGui;
    private final int cost;
    private final List<String> commands;

    public ShopItem(ItemStack item, boolean closeGui, int cost, List<String> commands) {
        this.item = item;
        this.closeGui = closeGui;
        this.cost = cost;
        this.commands = commands;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean shouldCloseGui() {
        return closeGui;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getCommands() {
        return commands;
    }
}
