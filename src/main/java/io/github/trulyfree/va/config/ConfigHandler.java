package io.github.trulyfree.va.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    /**
     * The Gson instance used by this config handler.
     */
    @Getter
    private final Gson gson;

    /**
     * The plugin which owns this config handler.
     */
    @Getter
    private final Plugin plugin;

    /**
     * Standard constructor for ConfigHandler.
     *
     * @param plugin The plugin which owns this config handler.
     */
    public ConfigHandler(Plugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    /**
     * Returns a config instantiated as the specified type.
     *
     * @param configName The name of the config to look for in the data folder.
     * @param type       The type to instantiate the config as.
     * @param <T>        The type to instantiate the config as (for generic system).
     * @return config The instantiated config, or null if the config does not exist.
     * @throws IOException If the file cannot be read.
     */
    public <T> T getConfig(String configName, Class<T> type) throws IOException {
        return getConfig(new File(plugin.getDataFolder(), configName), type);
    }

    /**
     * Returns a config instantiated as the specified type.
     *
     * @param file The file instance of the config file to read.
     * @param type The type to instantiate the config as.
     * @param <T>  The type to instantiate the config as (for generic system).
     * @return config The instantiated config, or null if the config does not exist.
     * @throws IOException If the file cannot be read.
     */
    private <T> T getConfig(File file, Class<T> type) throws IOException {
        T temp = null;
        if (file.exists()) {
            StringBuilder builder = new StringBuilder();
            for (String line : Files.readLines(file, Charsets.UTF_8)) {
                builder.append(line);
                builder.append('\n');
            }
            temp = gson.fromJson(builder.toString(), type);
        }
        return temp;
    }

}
