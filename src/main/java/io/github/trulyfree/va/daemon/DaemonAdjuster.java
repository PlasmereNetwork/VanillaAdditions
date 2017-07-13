package io.github.trulyfree.va.daemon;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;

public class DaemonAdjuster implements Adjuster {

    /**
     * The plugin which owns this DaemonAdjuster.
     */
    @Getter
    private final VanillaAdditionsPlugin plugin;

    /**
     * The listener associated with this DaemonAdjuster.
     */
    @Getter
    private DaemonEventListener listener;

    @Getter
    private DaemonOptions options;

    /**
     * Standard constructor for the DaemonAdjuster.
     *
     * @param plugin The plugin which owns this DaemonAdjuster.
     */
    public DaemonAdjuster(@NonNull VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.listener = new DaemonEventListener(this);
    }

    @Override
    public void applyAdjustments() {
        try {
            options = plugin.getConfigHandler().getConfig("daemon.json", DaemonOptions.class);
            plugin.getProxy().getPluginManager().registerListener(plugin, listener);
            ProcessBuilder processBuilder = new ProcessBuilder(options.getScript()).directory(plugin.getDataFolder());
            Daemon.spawn(processBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAdjustments() {
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }
}
