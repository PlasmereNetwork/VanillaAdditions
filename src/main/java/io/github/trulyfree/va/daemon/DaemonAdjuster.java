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
    private DaemonJoinListener listener;

    /**
     * Standard constructor for the DaemonAdjuster.
     *
     * @param plugin The plugin which owns this DaemonAdjuster.
     */
    public DaemonAdjuster(@NonNull VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.listener = new DaemonJoinListener(this);
    }

    @Override
    public void applyAdjustments() {
        try {
            listener.setDaemonConnectionCheck(plugin.getConfigHandler().getConfig("daemon.json", DaemonJoinListener.ConnectionCheck.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    @Override
    public void removeAdjustments() {
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }
}
