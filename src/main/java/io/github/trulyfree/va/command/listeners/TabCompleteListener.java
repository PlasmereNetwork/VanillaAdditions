package io.github.trulyfree.va.command.listeners;

import io.github.trulyfree.va.command.CommandAdjuster;
import io.github.trulyfree.va.command.commands.TabbableCommand;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TabCompleteListener implements Listener {

    /**
     * The adjuster which owns this listener. This is not necessary to have, but may prove useful to have in future
     * versions.
     */
    @SuppressWarnings("unused")
    private final CommandAdjuster adjuster;

    /**
     * A reference to the proxy's command map. This is reflectively captured from ProxyConfig, and is bound to change
     * between BungeeCord versions.
     */
    private final Map<String, Command> commandMap;

    private final List<String> nonOpVanillaCommands = Arrays.asList("help", "list", "me", "msg", "tell", "w");

    private final List<String> opVanillaCommands = Arrays.asList("advancement", "ban", "banlist", "blockdata", "clear", "clone", "debug", "defaultgamemode", "deop", "difficulty", "effect", "enchant", "entitydata", "execute", "fill", "function", "gamemode", "gamerule", "give", "kick", "kill", "locate", "op", "pardon", "particle", "playsound", "reload", "replaceitem", "save-all", "save-off", "say", "scoreboard", "seed", "setblock", "setidletimeout", "setmaxplayers", "setworldspawn", "spawnpoint", "spreadplayers", "stats", "stop", "stopsound", "summon", "teleport", "tellraw", "testfor", "testforblock", "testforblocks", "time", "title", "toggledownfall", "tp", "trigger", "weather", "whitelist", "worldborder", "xp");

    /**
     * Standard constructor for TabCompleteListener. Note that the proxy config's command map will be reflectively
     * accessed within this method - report any stack traces with your bungeecord version.
     *
     * @param adjuster The adjuster which owns this listener.
     */
    @SuppressWarnings("unchecked")
    public TabCompleteListener(@NonNull CommandAdjuster adjuster) {
        this.adjuster = adjuster;
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
        if (commandMap == null || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        String cursor = event.getCursor();
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (cursor.startsWith("/")) {
            int firstSpace = cursor.indexOf(' ');
            if (firstSpace == -1) {
                String commandStringStart = cursor.substring(1);
                for (Map.Entry<String, Command> entry : commandMap.entrySet()) {
                    if (!ProxyServer.getInstance().getDisabledCommands().contains(entry.getKey())) {
                        if (!player.getPermissions().contains(entry.getValue().getPermission())) {
                            continue;
                        }
                        if (entry.getValue().getName().startsWith(commandStringStart) && !event.getSuggestions().contains(entry.getValue().getName())) {
                            event.getSuggestions().add("/" + entry.getValue().getName());
                        }
                        for (String alias : entry.getValue().getAliases()) {
                            if (alias.startsWith(commandStringStart) && !event.getSuggestions().contains(alias)) {
                                event.getSuggestions().add("/" + alias);
                            }
                        }
                    }
                }
                if (player.getPermissions().contains("nonop")) {
                    for (String command : nonOpVanillaCommands) {
                        if (command.startsWith(commandStringStart)) {
                            event.getSuggestions().add("/" + command);
                        }
                    }
                }
                if (player.getPermissions().contains("op")) {
                    for (String command : opVanillaCommands) {
                        if (command.startsWith(commandStringStart)) {
                            event.getSuggestions().add("/" + command);
                        }
                    }
                }
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

}
