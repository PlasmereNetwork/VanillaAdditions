package io.github.trulyfree.va.ping;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.daemon.Daemon;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PingListener implements Listener {

    @SuppressWarnings("unused")
    private final VanillaAdditionsPlugin plugin;

    public PingListener(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProxyPingEvent(ProxyPingEvent event) {
        if (Daemon.hasInstance()) {
            Players players = event.getResponse().getPlayers();
            players.setOnline(players.getOnline() - 1);
        }
    }

}
