package org.hideplayer.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hideplayer.HidePlayer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Maneja la creación, carga y guardado de archivos de configuración YAML.
 */
public class ConfigManager {

    private final HidePlayer plugin;
    private final String fileName;
    private File configFile;
    private FileConfiguration config;

    public ConfigManager(HidePlayer plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        setup();
    }

    /**
     * Inicializa el archivo de configuración.
     */
    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    configFile.getParentFile().mkdirs();
                    configFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "No se pudo crear " + fileName, e);
                }
            }
        }

        reload();
    }

    /**
     * Recarga la configuración desde el disco.
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Obtiene la configuración actual.
     * 
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            reload();
        }
        return config;
    }

    /**
     * Guarda los cambios en el disco.
     */
    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "No se pudo guardar " + fileName, e);
        }
    }
}
