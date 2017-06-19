package io.github.trulyfree.va.lib;

import io.github.trulyfree.va.VanillaAdditionsPlugin;

@SuppressWarnings("unused")
public interface Adjuster {
    void applyAdjustments();

    void removeAdjustments();

    VanillaAdditionsPlugin getPlugin();
}
