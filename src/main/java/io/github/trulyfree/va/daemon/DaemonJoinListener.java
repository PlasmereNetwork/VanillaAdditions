package io.github.trulyfree.va.daemon;

import io.github.trulyfree.va.daemon.DaemonAdjuster;
import lombok.Setter;
import lombok.Value;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("deprecation")
public class DaemonJoinListener implements Listener {

    private final DaemonAdjuster adjuster;
    @Setter
    private ConnectionCheck daemonConnectionCheck;
    private final AtomicBoolean loggingIn;
    private final AtomicBoolean loggingInDaemon;
    private final Object lock = new Object();
    private final ProxyConfig config;
    private final Field onlineModeField;

    public DaemonJoinListener(DaemonAdjuster adjuster) {
        this.adjuster = adjuster;
        this.config = adjuster.getPlugin().getProxy().getConfig();
        this.loggingIn = new AtomicBoolean(false);
        this.loggingInDaemon = new AtomicBoolean(false);
        Field onlineModeField;
        if (!config.isOnlineMode()) {
            onlineModeField = null;
        } else {
            Class<? extends ProxyConfig> configClass = config.getClass();
            try {
                onlineModeField = configClass.getDeclaredField("onlineMode");
                onlineModeField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                onlineModeField = null;
                e.printStackTrace();
            }
        }
        this.onlineModeField = onlineModeField;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (onlineModeField == null) {
            return;
        }
        while (!loggingIn.compareAndSet(false, true)) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            boolean loggingInDaemon = daemonConnectionCheck.validate(event.getConnection());
            if (loggingInDaemon) {
                this.loggingInDaemon.set(true);
                adjuster.getPlugin().getLogger().info("Attempting to log in daemon " + event.getConnection().getName());
                event.getConnection().setOnlineMode(false);
                onlineModeField.setBoolean(config, false);
            } else {
                adjuster.getPlugin().getLogger().info("Attempting to log in player " + event.getConnection().getName());
            }
        } catch (Exception e) {
            event.setCancelled(true);
            event.setCancelReason("Internal exception: " + e.getClass().getSimpleName());
            loggingIn.set(false);
            synchronized (lock) {
                lock.notify();
            }
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent(PostLoginEvent event) {
        if (onlineModeField == null) {
            return;
        }
        try {
            if (loggingInDaemon.getAndSet(false)) {
                onlineModeField.setBoolean(config, true);
            }

        } catch (Exception e) {
            event.getPlayer().disconnect(new ComponentBuilder("Internal exception: " + e.getClass().getSimpleName()).create());
            e.printStackTrace();
        }
        loggingIn.set(false);
        synchronized (lock) {
            lock.notify();
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
