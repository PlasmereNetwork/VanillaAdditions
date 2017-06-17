package io.github.trulyfree.va.command;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.command.commands.TabbableCommand;
import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

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
        this.addedCommands = Collections.<TabbableCommand>unmodifiableList(Arrays.asList(new TabbableCommand[0]));
    }

    @Override
    public void applyAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().registerListener(plugin, listener);
        }

        for (Command command : addedCommands) {
            plugin.getProxy().getPluginManager().registerCommand(plugin, command);
        }
    }

    @Override
    public void removeAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().unregisterListener(listener);
        }
        plugin.getProxy().getPluginManager().unregisterCommands(plugin);
    }
}
