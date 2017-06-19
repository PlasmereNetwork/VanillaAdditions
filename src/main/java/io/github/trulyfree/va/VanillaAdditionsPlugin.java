package io.github.trulyfree.va;

import io.github.trulyfree.va.command.CommandAdjuster;
import io.github.trulyfree.va.config.ConfigHandler;
import io.github.trulyfree.va.daemon.DaemonAdjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class VanillaAdditionsPlugin extends Plugin {

    @Getter
    private final ConfigHandler configHandler;
    private CommandAdjuster commandAdjuster;
    private DaemonAdjuster daemonAdjuster;

    public VanillaAdditionsPlugin() {
        this.configHandler = new ConfigHandler(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.commandAdjuster = new CommandAdjuster(this);
        commandAdjuster.applyAdjustments();
        this.daemonAdjuster = new DaemonAdjuster(this);
        daemonAdjuster.applyAdjustments();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        commandAdjuster.removeAdjustments();
        daemonAdjuster.removeAdjustments();
    }
}
