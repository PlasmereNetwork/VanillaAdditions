package io.github.trulyfree.va.command.listeners;

import io.github.trulyfree.va.command.CommandAdjuster;
import io.github.trulyfree.va.command.commands.TabbableCommand;
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

    private final CommandAdjuster adjuster;
    private final ConcurrentHashMap<Connection, List<String>> awaitingSuggestions;
    private final Map<String, Command> commandMap;

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
                for (Command command : commandMap.values()) {
                    if (command.getName().startsWith(commandStringStart)) {
                        awaitingSuggestionResponses.add("/" + command.getName());
                    }
                    for (String alias : command.getAliases()) {
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
