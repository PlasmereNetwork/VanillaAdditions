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

    private final DaemonAdjuster adjuster;
    @Setter
    private ConnectionCheck daemonConnectionCheck;

    DaemonJoinListener(DaemonAdjuster adjuster) {
        this.adjuster = adjuster;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (daemonConnectionCheck.validate(event.getConnection())) {
            event.getConnection().setOnlineMode(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent(PostLoginEvent event) {
        if (daemonConnectionCheck.validate(event.getPlayer().getPendingConnection())) {
            Daemon.makeInstance(event.getPlayer());
            adjuster.getPlugin().getLogger().info("Daemon instance assigned to " + event.getPlayer().getName());
        }
    }

    @Value
    public static class ConnectionCheck {
        String username;

        private boolean validate(PendingConnection connection) {
            return connection.getName().equals(this.getUsername()) && connection.getAddress().getHostString().equals("127.0.0.1");
        }
    }
}
