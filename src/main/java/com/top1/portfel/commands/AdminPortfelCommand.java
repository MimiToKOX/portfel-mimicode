// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel.commands;

import com.top1.portfel.config.YamalDataManager;
import com.top1.portfel.other.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.List;

public class AdminPortfelCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final short MAX_BALANCE = 32767;

    public AdminPortfelCommand(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        plugin.getCommand("adminportfel").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda wymaga bycia graczem!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mimicode.portfel.adminportfel") && !player.isOp()) {
            player.sendMessage(ChatHelper.colored("&4:c &c> Nie posiadasz takich permisji! (mimicode.portfel.adminportfel)"));
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ChatHelper.colored("&cUżycie: /adminPortfel <add/remove/set> <nick gracza> <wartość>"));
            return false;
        }

        if (args.length == 1) {
            String option = args[0].toLowerCase();
            List<String> options = new ArrayList<>();
            options.add("add");
            options.add("remove");
            options.add("set");
            return showCompletions(player, options, option);
        }

        String option = args[0];
        String playerName = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatHelper.colored("&cNieprawidłowa ilość. Upewnij się, że podajesz liczbę całkowitą."));
            return false;
        }

        YamalDataManager dataManager = new YamalDataManager();
        dataManager.setupDataFile(plugin.getDataFolder());

        if (!dataManager.playerExists(playerName)) {
            player.sendMessage(ChatHelper.colored("&cPodany gracz nie istnieje w bazie danych."));
            return false;
        }

        int currentBalance = dataManager.getPlayerBalance(playerName);
        short newBalance;

        switch (option.toLowerCase()) {
            case "add":
                if (amount <= 0) {
                    player.sendMessage(ChatHelper.colored("&cNieprawidłowa ilość Upewnij się że podajesz liczbę całkowitą dodatnią."));
                    return false;
                }
                if (currentBalance + amount > MAX_BALANCE) {
                    player.sendMessage(ChatHelper.colored("&cNie można dodać tyle pieniędzy ponieważ przekroczyłoby to maksymalny balans"));
                    return false;
                }
                newBalance = (short) (currentBalance + amount);
                player.sendMessage(ChatHelper.colored("&aDodano " + amount + " do portfela gracza " + playerName + ". Nowy stan: " + newBalance));
                break;

            case "remove":
                if (amount <= 0) {
                    player.sendMessage(ChatHelper.colored("&cNieprawidłowa ilość Upewnij się że podajesz liczbę całkowitą dodatnią."));
                    return false;
                }
                if (currentBalance - amount < 0) {
                    player.sendMessage(ChatHelper.colored("&cNie można odjąć tyle pieniędzy ponieważ spowodowałoby to ujemny balans"));
                    return false;
                }
                newBalance = (short) (currentBalance - amount);
                player.sendMessage(ChatHelper.colored("&aOdejmowano " + amount + " z portfela gracza " + playerName + ". Nowy stan: " + newBalance));
                break;

            case "set":
                if (amount < 0) {
                    player.sendMessage(ChatHelper.colored("&cNieprawidłowa ilość Upewnij się że podajesz liczbę całkowitą nieujemną."));
                    return false;
                }
                if (amount > MAX_BALANCE) {
                    player.sendMessage(ChatHelper.colored("cNie można dodać tyle pieniędzy ponieważ przekroczyłoby to maksymalny balans"));
                    return false;
                }
                newBalance = (short) amount;
                player.sendMessage(ChatHelper.colored("&aUstawiono portfel gracza " + playerName + " na " + newBalance));
                break;

            default:
                player.sendMessage(ChatHelper.colored("&cNieprawidłowa opcja. Dostępne opcje: dodaj, odejmij, ustaw."));
                return false;
        }

        dataManager.setPlayerBalance(playerName, newBalance);
        return true;
    }

    private boolean showCompletions(Player player, List<String> completions, String currentArg) {
        List<String> availableCompletions = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(currentArg)) {
                availableCompletions.add(completion);
            }
        }

        if (availableCompletions.isEmpty()) {
            return false;
        }

        availableCompletions.sort(String.CASE_INSENSITIVE_ORDER);
        player.sendMessage(ChatHelper.colored("&7Dostępne opcje: &a" + String.join("&7, &a", availableCompletions)));
        return true;
    }
}
