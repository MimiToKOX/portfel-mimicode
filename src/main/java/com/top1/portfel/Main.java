// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel;

import com.top1.portfel.commands.AdminPortfelCommand;
import com.top1.portfel.commands.PortfelCommand;
import com.top1.portfel.commands.StanKontaCommand;
import com.top1.portfel.config.ConfigManager;
import com.top1.portfel.config.YamalDataManager;
import com.top1.portfel.listeners.AddPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public final class Main extends JavaPlugin {

    private YamalDataManager dataManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        if (!getDescription().getName().equals("Plugin na porftel by mimicode <3")
                && !getDescription().getAuthors().contains("MimiCode")
                && !getDescription().getName().contains("Portfel-MimiCode")
                && !getDescription().getMain().contains("com.top1.portfel.Main")
                && !getDescription().getWebsite().equals("dsc.gg/mimicode")) {

            getLogger().warning("Wykryto nieupoważnioną zmianę danych w plugin.yml w wyniku czego " +
                    "plugin zostaje wyłączony. Prosimy o zmianę danych na prawidłowe. " +
                    "Jeśli to błąd - skontaktuj się z nami na Discordzie: https://discord.gg/weHSn6Bhjd");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        dataManager = new YamalDataManager();
        dataManager.setupDataFile(getDataFolder());

        getServer().getPluginManager().registerEvents(new AddPlayerData(this), this);

        registerCommand("commands.enabled.adminportfel", "adminportfel", new AdminPortfelCommand(this, getConfig()));
        registerCommand("commands.enabled.stankonta", "stankonta", new StanKontaCommand(this, getConfig(), dataManager));
        registerCommand("commands.enabled.portfel", "portfel", new PortfelCommand(this, getConfig(), dataManager));
        new Placeholders(dataManager).register();
    }




    private void registerFeature(String configPath, String functionName, Listener listener) {
        if (getConfig().getBoolean(configPath)) {
            getLogger().info("Pomyslnie załadowano: " + functionName);
            getServer().getPluginManager().registerEvents(listener, this);
        } else {
            getLogger().info("Nie mozna wczytać: " + functionName);
            return;
        }
    }

    private void registerCommand(String configPath, String commandName, CommandExecutor executor) {
        if (getConfig().getBoolean(configPath)) {
            getCommand(commandName).setExecutor(executor);
        } else {
            return;
        }
    }
}
