// Plugin na portfel by MimiCode
// Plugin na licencji!
// proszę nie kopiować <3

package com.top1.portfel.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class YamlDataManager {

    private FileConfiguration dataConfig;
    private File dataFile;
    private Timer dataRefreshTimer;

    public void setupDataFile(File dataFolder) {
        dataFile = new File(dataFolder, "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        startDataRefreshTimer();
    }

    public int getPlayerBalance(String playerName) {
        return dataConfig.getInt("players." + playerName, 0);
    }

    public void setPlayerBalance(String playerName, int balance) {
        dataConfig.set("players." + playerName, balance);
        saveDataFile();
    }

    public boolean playerExists(String playerName) {
        return dataConfig.contains("players." + playerName);
    }

    public void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDataRefreshTimer() {
        dataRefreshTimer = new Timer();
        dataRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    dataConfig.load(dataFile);
                } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }, 15000, 15000);
    }
}
