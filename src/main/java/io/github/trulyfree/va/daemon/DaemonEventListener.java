package io.github.trulyfree.va.daemon;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@SuppressWarnings("deprecation")
public class DaemonEventListener implements Listener {

    /**
     * The adjuster which owns this DaemonJoinListener.
     */
    private final DaemonAdjuster adjuster;

    /**
     * Standard constructor for DaemonJoinListener.
     *
     * @param adjuster The adjuster which owns this DaemonJoinListener.
     */
    DaemonEventListener(DaemonAdjuster adjuster) {
        this.adjuster = adjuster;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (adjuster.getOptions().validate(event.getConnection())) {
            event.getConnection().setOnlineMode(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent(final PostLoginEvent event) {
        adjuster.getPlugin().getBackgroundExecutor().submit(new Runnable() {
            public void run() {
                if (adjuster.getOptions().validate(event.getPlayer().getPendingConnection())) {
                    Daemon.makeInstance(event.getPlayer());
                    adjuster.getPlugin().getLogger().info(String.format("Daemon instance assigned to %s.", event.getPlayer().getName()));
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnectEvent(final PlayerDisconnectEvent event) {
        adjuster.getPlugin().getBackgroundExecutor().submit(new Runnable() {
            public void run() {
                Daemon daemon = Daemon.getInstanceNow();
                if (daemon != null && event.getPlayer().equals(daemon.getPlayer())) {
                    Daemon.deleteInstance();
                }
            }
        });
    }
}
