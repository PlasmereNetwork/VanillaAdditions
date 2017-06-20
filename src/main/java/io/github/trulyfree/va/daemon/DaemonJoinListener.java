package io.github.trulyfree.va.daemon;

import lombok.Setter;
import lombok.Value;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@SuppressWarnings("deprecation")
public class DaemonJoinListener implements Listener {

    /**
     * The adjuster which owns this DaemonJoinListener.
     */
    private final DaemonAdjuster adjuster;

    /**
     * The connection check instance; this validates the Daemon user on login.
     */
    @Setter
    private ConnectionCheck daemonConnectionCheck;

    /**
     * Standard constructor for DaemonJoinListener.
     *
     * @param adjuster The adjuster which owns this DaemonJoinListener.
     */
    DaemonJoinListener(DaemonAdjuster adjuster) {
        this.adjuster = adjuster;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (daemonConnectionCheck == null) {
            return;
        }
        if (daemonConnectionCheck.validate(event.getConnection())) {
            event.getConnection().setOnlineMode(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent(PostLoginEvent event) {
        if (daemonConnectionCheck == null) {
            return;
        }
        if (daemonConnectionCheck.validate(event.getPlayer().getPendingConnection())) {
            Daemon.makeInstance(event.getPlayer());
            adjuster.getPlugin().getLogger().info("Daemon instance assigned to " + event.getPlayer().getName());
        }
    }

    /**
     * Connection check class. This class exists only as a layer for Gson to load information about the Daemon through.
     */
    @Value
    public static class ConnectionCheck {
        /**
         * The username that the daemon will join with.
         */
        String username;

        /**
         * Validator for PendingConnections to ensure that they are the Daemon instance. Daemon instances will be
         * restricted to localhost-only source connections.
         *
         * @param connection The connection to validate against.
         * @return valid A boolean representing the validity of the connection as a daemon.
         */
        private boolean validate(PendingConnection connection) {
            return connection.getName().equals(this.getUsername()) && connection.getAddress().getHostString().equals("127.0.0.1");
        }
    }
}
