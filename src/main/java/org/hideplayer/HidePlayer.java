package org.hideplayer;

import org.hideplayer.command.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.hideplayer.config.ConfigManager;
import org.hideplayer.manager.PlayerManager;
import org.hideplayer.manager.SkinManager;
import org.hideplayer.util.LoggerUtils;

/**
 * Clase principal del plugin HidePlayer.
 * Sigue el patrón Singleton y maneja el ciclo de vida del plugin.
 */
public final class HidePlayer extends JavaPlugin {

    private static HidePlayer instance;
    private ConfigManager settings;
    private SkinManager skinManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Asignar instancia Singleton
        instance = this;

        LoggerUtils.info("Iniciando HidePlayer...");

        try {
            // Inicializar Configuración
            setupConfig();

            // Inicializar Managers
            this.skinManager = new SkinManager();
            this.playerManager = new PlayerManager(this);

            // Registrar Comandos
            MainCommand mainCmd = new MainCommand(this);
            getCommand("hideplayer").setExecutor(mainCmd);
            getCommand("hideplayer").setTabCompleter(mainCmd);
            // registerEvents();

            LoggerUtils.info("Plugin habilitado correctamente.");
        } catch (Exception e) {
            LoggerUtils.logException("Carga inicial del plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        LoggerUtils.info("Deshabilitando HidePlayer...");
        instance = null;
    }

    /**
     * Configura el manejo de archivos YML.
     */
    private void setupConfig() {
        settings = new ConfigManager(this, "config.yml");
        // Ejemplo de lectura:
        // boolean debug = settings.getConfig().getBoolean("debug", false);
    }

    /**
     * Obtiene la instancia principal del plugin.
     * 
     * @return HidePlayer instance
     */
    public static HidePlayer getInstance() {
        return instance;
    }

    /**
     * Obtiene el manejador de configuración.
     * 
     * @return ConfigManager
     */
    public ConfigManager getSettings() {
        return settings;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Envía un mensaje formateado a un receptor (jugador o consola).
     * Reemplaza el placeholder %prefix% por el prefijo de la config.
     *
     * @param sender El receptor del mensaje
     * @param path   La ruta del mensaje en la configuración
     */
    public void sendMessage(org.bukkit.command.CommandSender sender, String path) {
        String message = settings.getConfig().getString(path);
        if (message == null || message.isEmpty())
            return;

        String prefix = settings.getConfig().getString("prefix", "");
        message = message.replace("%prefix%", prefix);

        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }
}
