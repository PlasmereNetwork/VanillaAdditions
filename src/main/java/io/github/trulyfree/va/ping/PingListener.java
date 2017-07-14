package io.github.trulyfree.va.ping;

import io.github.trulyfree.va.VanillaAdditionsPlugin;
import io.github.trulyfree.va.daemon.Daemon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;

public class PingListener implements Listener {

    @SuppressWarnings("unused")
    private final VanillaAdditionsPlugin plugin;

    PingListener(VanillaAdditionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPingEvent(ProxyPingEvent event) {
        if (Daemon.hasInstance()) {
            ServerPing original = event.getResponse();
            Players players = original.getPlayers();
            ServerPing.PlayerInfo[] sample = players.getSample();
            if (sample != null) {
                ArrayList<ServerPing.PlayerInfo> sampleReplacement = new ArrayList<>(sample.length - 1);
                for (ServerPing.PlayerInfo item : sample) {
                    if (!item.getName().equals(Daemon.getInstanceNow().getPlayer().getName())) {
                        sampleReplacement.add(item);
                    }
                }
                sample = sampleReplacement.toArray(new ServerPing.PlayerInfo[sample.length - 1]);
            }
            Players changed = new Players(players.getMax() - 1, players.getOnline() - 1, sample);
            original.setPlayers(changed);
        }
    }

}
