// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel;

import com.top1.portfel.config.YamalDataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {

    private final YamalDataManager dataManager;

    public Placeholders(YamalDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public String getIdentifier() {
        return "MimiCodePortfel";
    }

    @Override
    public String getAuthor() {
        return "MimiCode";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("stankonta")) {
            return String.valueOf(dataManager.getPlayerBalance(player.getName()));
        }

        return null;
    }
}
