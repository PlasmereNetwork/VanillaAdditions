package io.github.trulyfree.va.lib;

import io.github.trulyfree.va.VanillaAdditionsPlugin;

@SuppressWarnings("unused")
public interface Adjuster {
    /**
     * Applies the adjustments that this adjuster wants to set in place.
     */
    void applyAdjustments();

    /**
     * Removes the adjustments that this adjuster wants to set in place.
     */
    void removeAdjustments();

    /**
     * Returns the plugin which owns the adjuster.
     *
     * @return plugin The plugin which owns the adjuster.
     */
    VanillaAdditionsPlugin getPlugin();
}
