package io.github.trulyfree.va.command.commands;

import io.github.trulyfree.va.command.listeners.TabCompleteListener;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;

/**
 * TabbableCommand class. All commands which need to have tab complete functionality handled by VanillaAdditions should
 * extend this class and define their tab complete functionality according to the passed TabCompleteEvent.
 */
public abstract class TabbableCommand extends Command {

    // See Command's constructors.
    public TabbableCommand(String name) {
        super(name);
        TabCompleteListener.addCommand(this);
    }

    // See Command's constructors.
    public TabbableCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        TabCompleteListener.addCommand(this);
    }

    /**
     * The method that will be called when the VanillaAdditions plugin detects that this commands needs to handle a
     * tab complete.
     *
     * @param event The tab complete event that triggered this method.
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void handleTabCompleteEvent(TabCompleteEvent event) {
    }

}
