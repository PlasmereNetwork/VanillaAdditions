package io.github.trulyfree.va.lib;

import io.github.trulyfree.va.VanillaAdditionsPlugin;

public interface Adjuster {
    public void applyAdjustments();

    public void removeAdjustments();

    public VanillaAdditionsPlugin getPlugin();
}
