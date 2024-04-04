// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel.commands;

import com.top1.portfel.config.ConfigManager;
import com.top1.portfel.config.YamalDataManager;
import com.top1.portfel.gui.ItemShopGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class PortfelCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final YamalDataManager dataManager;

    public PortfelCommand(JavaPlugin plugin, FileConfiguration config, YamalDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        plugin.getCommand("portfel").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda wymaga bycia graczem!");
            return false;
        }

        Player player = (Player) sender;

        ItemShopGui itemShopGui = new ItemShopGui(new ConfigManager(plugin), dataManager, plugin);
        itemShopGui.openInventory(player);

        return true;
    }
}
