package io.github.trulyfree.va.command;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;

public class CommandAdjuster implements Adjuster {

    /**
     * The plugin which owns this adjuster.
     */
    @Getter
    private final VanillaAdditionsPlugin plugin;

    /**
     * The TabCompleteListener owned by this adjuster.
     */
    @Getter
    private final TabCompleteListener tabCompleteListener;

    /**
     * Standard constructor for CommandAdjuster.
     *
     * @param plugin The plugin which owns this adjuster.
     */
    public CommandAdjuster(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.tabCompleteListener = new TabCompleteListener(this);
    }

    @Override
    public void applyAdjustments() {
        plugin.getProxy().getPluginManager().registerListener(plugin, tabCompleteListener);
    }

    @Override
    public void removeAdjustments() {
        plugin.getProxy().getPluginManager().unregisterListener(tabCompleteListener);
    }
}
