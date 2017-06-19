package io.github.trulyfree.va.command;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;

import java.util.Collections;
import java.util.List;

public class CommandAdjuster implements Adjuster {

    @Getter
    private final VanillaAdditionsPlugin plugin;
    @Getter
    private final List<Listener> addedListeners;

    public CommandAdjuster(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.addedListeners = Collections.<Listener>singletonList(new TabCompleteListener(this));
    }

    @Override
    public void applyAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().registerListener(plugin, listener);
        }
    }

    @Override
    public void removeAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().unregisterListener(listener);
        }
    }
}
