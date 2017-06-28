package io.github.trulyfree.va.events;

import net.md_5.bungee.api.event.ChatEvent;

public class DaemonChatEvent extends ChatEvent {
    public DaemonChatEvent(ChatEvent event) {
        super(event.getSender(), event.getReceiver(), event.getMessage());
        event.setCancelled(true);
    }

    @Override
    public boolean isCancelled() {
        return true;
    }
}
