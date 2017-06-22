package io.github.trulyfree.va.ping;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;

public class PingAdjuster implements Adjuster {

    @Getter
    private final VanillaAdditionsPlugin plugin;

    @Getter
    private PingListener pingListener;

    public PingAdjuster(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void applyAdjustments() {
        pingListener = new PingListener(plugin);
        getPlugin().getProxy().getPluginManager().registerListener(plugin, pingListener);
    }

    @Override
    public void removeAdjustments() {
        getPlugin().getProxy().getPluginManager().unregisterListener(pingListener);
    }
}
