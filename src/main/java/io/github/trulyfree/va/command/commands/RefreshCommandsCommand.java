package io.github.trulyfree.va.command.commands;

import io.github.trulyfree.va.command.CommandAdjuster;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.io.IOException;
import java.util.Arrays;

public class RefreshCommandsCommand extends TabbableCommand {

    private final CommandAdjuster adjuster;

    public RefreshCommandsCommand(CommandAdjuster adjuster) {
        super("refresh");
        this.adjuster = adjuster;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        adjuster.getPlugin().getLogger().info(Arrays.toString(strings));
        if (commandSender.equals(adjuster.getPlugin().getProxy().getConsole())) {
            try {
                adjuster.refreshCustomCommands();
            } catch (IllegalAccessException | IOException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            commandSender.sendMessage(new ComponentBuilder("").color(ChatColor.RED).append("Unknown command. Try /help for a list of commands").create());
        }
    }
}
