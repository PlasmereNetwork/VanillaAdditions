package io.github.trulyfree.va.command.listeners;

import io.github.trulyfree.va.command.CommandAdjuster;
import io.github.trulyfree.va.command.commands.TabbableCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TabCompleteListener implements Listener {

    /**
     * The adjuster which owns this listener. This is not necessary to have, but may prove useful to have in future
     * versions.
     */
    @SuppressWarnings("unused")
    private final CommandAdjuster adjuster;

    /**
     * The suggestions that we need to add to the connections upon tab complete response. This allows us to add vanilla
     * stuff without touching the server.
     */
    private final ConcurrentHashMap<Connection, List<String>> awaitingSuggestions;

    /**
     * A reference to the proxy's command map. This is reflectively captured from ProxyConfig, and is bound to change
     * between BungeeCord versions.
     */
    private final Map<String, Command> commandMap;

    /**
     * Standard constructor for TabCompleteListener. Note that the proxy config's command map will be reflectively
     * accessed within this method - report any stack traces with your bungeecord version.
     *
     * @param adjuster The adjuster which owns this listener.
     */
    @SuppressWarnings("unchecked")
    public TabCompleteListener(CommandAdjuster adjuster) {
        this.adjuster = adjuster;
        this.awaitingSuggestions = new ConcurrentHashMap<>();
        PluginManager manager = adjuster.getPlugin().getProxy().getPluginManager();
        Map<String, Command> commandMap;
        try {
            Field commandMapField = manager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (Map<String, Command>) commandMapField.get(manager);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            commandMap = null;
            e.printStackTrace();
        }
        this.commandMap = commandMap;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTabComplete(TabCompleteEvent event) {
        if (commandMap == null) {
            return;
        }
        String cursor = event.getCursor();
        if (cursor.startsWith("/")) {
            int firstSpace = cursor.indexOf(' ');
            if (firstSpace == -1) {
                List<String> awaitingSuggestionResponses = new ArrayList<>();
                String commandStringStart = cursor.substring(1);
                for (Map.Entry<String, Command> entry : commandMap.entrySet()) {
                    if (ProxyServer.getInstance().getDisabledCommands().contains(entry.getKey())) {
                        continue;
                    }
                    if (entry.getValue().getName().startsWith(commandStringStart)) {
                        awaitingSuggestionResponses.add("/" + entry.getValue().getName());
                    }
                    for (String alias : entry.getValue().getAliases()) {
                        if (alias.startsWith(commandStringStart)) {
                            awaitingSuggestionResponses.add("/" + alias);
                        }
                    }
                }
                awaitingSuggestions.put(event.getSender(), awaitingSuggestionResponses);
            } else {
                String commandString = cursor.substring(1, firstSpace);
                for (Command command : commandMap.values()) {
                    if (command instanceof TabbableCommand) {
                        if (command.getName().equals(commandString)) {
                            ((TabbableCommand) command).handleTabCompleteEvent(event);
                            return;
                        }
                        for (String alias : command.getAliases()) {
                            if (alias.equals(commandString)) {
                                ((TabbableCommand) command).handleTabCompleteEvent(event);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTabCompleteResponse(TabCompleteResponseEvent event) {
        if (event.getSuggestions().isEmpty()) {
            return;
        }

        List<String> awaitingSuggestionResponses = awaitingSuggestions.remove(event.getReceiver());
        if (awaitingSuggestionResponses != null) {
            event.getSuggestions().addAll(awaitingSuggestionResponses);
        }
    }

}
