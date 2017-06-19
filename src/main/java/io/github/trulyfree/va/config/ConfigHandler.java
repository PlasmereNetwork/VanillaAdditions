package io.github.trulyfree.va.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import io.github.trulyfree.va.VanillaAdditionsPlugin;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    @Getter
    private final Gson gson;
    @Getter
    private final VanillaAdditionsPlugin plugin;

    public ConfigHandler(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
    }

    public <T> T getConfig(String configName, Class<T> type) throws IOException {
        return getConfig(new File(plugin.getDataFolder(), configName), type);
    }

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
