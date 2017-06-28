package io.github.trulyfree.va.daemon;

import io.github.trulyfree.va.events.DaemonChatEvent;
import lombok.Setter;
import lombok.Value;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ChatEvent;
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
     * The connection check instance; this validates the Daemon user on login.
     */
    @Setter
    private ConnectionCheck daemonConnectionCheck;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        Daemon daemon = Daemon.getInstanceNow();
        if (daemon != null && event.getPlayer().equals(daemon.getPlayer())) {
            Daemon.deleteInstance();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatEvent(ChatEvent event) {
        Daemon daemon = Daemon.getInstanceNow();
        if (daemon != null && event.getSender().equals(daemon.getPlayer())) {
            ProxyServer.getInstance().getPluginManager().callEvent(new DaemonChatEvent(event));
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
