package io.github.trulyfree.va.events;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.md_5.bungee.api.plugin.Event;

@EqualsAndHashCode(callSuper = true)
@Value
public class DaemonChatEvent extends Event {

    String message;

}
