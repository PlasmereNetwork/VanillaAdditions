package io.github.trulyfree.va.command;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.command.commands.RefreshCommandsCommand;
import io.github.trulyfree.va.command.commands.TabbableCommand;
import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import io.github.trulyfree.va.lib.Adjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandAdjuster implements Adjuster {

    @Getter
    private final VanillaAdditionsPlugin plugin;
    @Getter
    private final List<Listener> addedListeners;
    @Getter
    private final List<TabbableCommand> addedCommands;
    private final ExternalCommandsHandler handler;

    public CommandAdjuster(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
        this.addedListeners = Collections.<Listener>singletonList(new TabCompleteListener(this));
        this.addedCommands = new ArrayList<>();
        this.handler = new ExternalCommandsHandler(this);
    }

    @Override
    public void applyAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().registerListener(plugin, listener);
        }

        try {
            refreshCustomCommands();
        } catch (IllegalAccessException | IOException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAdjustments() {
        for (Listener listener : addedListeners) {
            plugin.getProxy().getPluginManager().unregisterListener(listener);
        }
        addedCommands.clear();
        plugin.getProxy().getPluginManager().unregisterCommands(plugin);
    }

    public void refreshCustomCommands() throws IllegalAccessException, IOException, InstantiationException {
        addedCommands.clear();
        plugin.getProxy().getPluginManager().unregisterCommands(plugin);
        plugin.getProxy().getPluginManager().registerCommand(plugin, new RefreshCommandsCommand(this));
        for (TabbableCommand command : handler.getExternalCommands()) {
            addedCommands.add(command);
            plugin.getProxy().getPluginManager().registerCommand(plugin, command);
        }
    }
}
