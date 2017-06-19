package io.github.trulyfree.va.daemon;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;

import java.io.IOException;

public class DaemonAdjuster implements Adjuster {

    @Getter
    private final VanillaAdditionsPlugin plugin;
    @Getter
    private DaemonJoinListener listener;

    public DaemonAdjuster(VanillaAdditionsPlugin plugin) {
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
