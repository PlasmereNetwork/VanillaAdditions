package io.github.trulyfree.va;

import io.github.trulyfree.va.command.CommandAdjuster;
import net.md_5.bungee.api.plugin.Plugin;

public class VanillaAdditionsPlugin extends Plugin {

    private final CommandAdjuster commandAdjuster;

    public VanillaAdditionsPlugin() {
        this.commandAdjuster = new CommandAdjuster(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        commandAdjuster.applyAdjustments();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        commandAdjuster.removeAdjustments();
    }
}
