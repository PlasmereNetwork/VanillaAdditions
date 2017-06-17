package io.github.trulyfree.va.command.commands;

import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;

public abstract class TabbableCommand extends Command {

    public TabbableCommand(String name) {
        super(name);
    }

    public TabbableCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    public void handleTabCompleteEvent(TabCompleteEvent event) {
    }

}
