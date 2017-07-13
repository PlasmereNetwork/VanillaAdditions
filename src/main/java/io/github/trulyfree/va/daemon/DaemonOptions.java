package io.github.trulyfree.va.daemon;

import lombok.Value;
import net.md_5.bungee.api.connection.PendingConnection;

/**
 * Connection check class. This class exists only as a layer for Gson to load information about the Daemon through.
 */
@Value
public class DaemonOptions {
    /**
     * The username that the daemon will join with.
     */
    String username;
    String[] script;

    /**
     * Validator for PendingConnections to ensure that they are the Daemon instance. Daemon instances will be
     * restricted to localhost-only source connections.
     *
     * @param connection The connection to validate against.
     * @return valid A boolean representing the validity of the connection as a daemon.
     */
    public boolean validate(PendingConnection connection) {
        return connection.getName().equals(this.getUsername()) && connection.getAddress().getHostString().equals("127.0.0.1");
    }
}
