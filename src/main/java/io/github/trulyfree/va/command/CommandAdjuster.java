package io.github.trulyfree.va.command;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.command.commands.RefreshCommandsCommand;
import io.github.trulyfree.va.command.commands.TabbableCommand;
import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandAdjuster implements Adjuster {

    @Getter
    private final VanillaAdditionsPlugin plugin;
    @Getter
    private final List<Listener> addedListeners;
    @Getter
    private final List<TabbableCommand> addedCommands;

    public CommandAdjuster(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.addedListeners = Collections.<Listener>unmodifiableList(Arrays.asList(new TabCompleteListener(this)));
        this.addedCommands = new ArrayList<>();
    }

    @Override
    public void applyAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().registerListener(plugin, listener);
        }

        plugin.getProxy().getPluginManager().registerCommand(plugin, new RefreshCommandsCommand(this));
        refreshCustomCommands();
    }

    @Override
    public void removeAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().unregisterListener(listener);
        }
        addedCommands.clear();
        plugin.getProxy().getPluginManager().unregisterCommands(plugin);
    }

    public void refreshCustomCommands() {
        // TODO
    }
}
