package io.github.trulyfree.va;

import io.github.trulyfree.va.command.CommandAdjuster;
import io.github.trulyfree.va.config.ConfigHandler;
import io.github.trulyfree.va.daemon.DaemonAdjuster;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Plugin class for VanillaAdditions.
 */
public class VanillaAdditionsPlugin extends Plugin {

    /**
     * The config handler for this VanillaAdditions instance.
     */
    @Getter
    private final ConfigHandler configHandler;

    /**
     * The command adjuster for this VanillaAdditions instance.
     */
    private CommandAdjuster commandAdjuster;

    /**
     * The daemon adjuster for this VanillaAdditions instance.
     */
    private DaemonAdjuster daemonAdjuster;

    /**
     * Standard constructor for VanillaAdditions. This will be called when the plugin is loaded.
     */
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
