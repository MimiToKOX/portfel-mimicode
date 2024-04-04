// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel.listeners;

import com.top1.portfel.config.YamalDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.function.Consumer;

public class AddPlayerData implements Listener {
    private final YamalDataManager dataManager;
    private final Consumer<String> logger;
    private final JavaPlugin plugin;

    public AddPlayerData(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataManager = new YamalDataManager();
        dataManager.setupDataFile(plugin.getDataFolder());
        this.logger = message -> plugin.getLogger().info(message);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!dataManager.playerExists(playerName)) {
            dataManager.setPlayerBalance(playerName, 0);
            logger.accept("Stworzono profil dla gracza " + playerName + " w bazie danych");
        }
    }
}
