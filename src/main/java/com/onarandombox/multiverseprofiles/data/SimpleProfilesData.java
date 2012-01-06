package com.onarandombox.multiverseprofiles.data;

import com.onarandombox.multiverseprofiles.util.ProfilesLog;
import com.onarandombox.multiverseprofiles.world.SimpleWorldProfile;
import com.onarandombox.multiverseprofiles.world.WorldProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author dumptruckman
 */
public class SimpleProfilesData implements ProfilesData {
    
    private FileConfiguration data;
    private File dataFile = null;
    
    public SimpleProfilesData(JavaPlugin plugin) throws IOException {
        // Make the data folders
        plugin.getDataFolder().mkdirs();

        // Check if the data file exists.  If not, create it.
        this.dataFile = new File(plugin.getDataFolder(), "worldData.yml");
        if (!this.dataFile.exists()) {
            this.dataFile.createNewFile();
        }

        // Load the data file into memory
        this.data = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    public void save() {
        try {
            this.getData().save(this.dataFile);
        } catch (IOException e) {
            ProfilesLog.severe("Unable to save data!");
            ProfilesLog.severe(e.getMessage());
        }
    }

    public FileConfiguration getData() {
        return this.data;
    }

    public HashMap<String, WorldProfile> getWorldProfiles() {
        HashMap<String, WorldProfile> worldProfiles = new HashMap<String, WorldProfile>();
        Set<String> worldNames = this.getData().getKeys(false);
        if (worldNames == null) {
            ProfilesLog.info("No world data to load");
            return worldProfiles;
        }
        for (String worldName : worldNames) {
            Object obj = this.getData().get(worldName);
            if (obj instanceof ConfigurationSection) {
                WorldProfile worldProfile = SimpleWorldProfile.deserialize(worldName, (ConfigurationSection) obj);
                
                if (worldProfile != null) {
                    worldProfiles.put(worldProfile.getWorld(), worldProfile);
                }
            } else {
                ProfilesLog.warning("Problem loading world data!");
            }
        }
        return worldProfiles;
    }
}
