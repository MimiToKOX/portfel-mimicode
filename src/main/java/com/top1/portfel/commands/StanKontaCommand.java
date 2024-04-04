// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel.commands;

import com.top1.portfel.config.YamalDataManager;
import com.top1.portfel.other.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StanKontaCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final YamalDataManager dataManager;

    public StanKontaCommand(JavaPlugin plugin, FileConfiguration config, YamalDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        plugin.getCommand("stankonta").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda wymaga bycia graczem!");
            return false;
        }

        Player player = (Player) sender;

        int balance = dataManager.getPlayerBalance(player.getName());
        String message = ChatColor.GREEN + "Masz teraz {balance} PLN";
        String messageWithBalance = message.replace("{balance}", String.valueOf(balance));
        player.sendMessage(ChatHelper.colored(messageWithBalance));

        return true;
    }
}
