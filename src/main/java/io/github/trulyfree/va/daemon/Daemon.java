package io.github.trulyfree.va.daemon;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public final class Daemon {

    /**
     * Latch to ensure that we have an instance to use.
     */
    private static final AtomicReference<CountDownLatch> latch = new AtomicReference<>(new CountDownLatch(1));

    /**
     * The instance of the daemon. This is held in an atomic reference to ensure thread sanity.
     */
    private static final AtomicReference<Daemon> instance = new AtomicReference<>();

    /**
     * The player instance which will be used as the daemon.
     */
    @Getter
    private final ProxiedPlayer player;

    /**
     * An executor for commands to ensure that all commands get sent in the right order, but without
     * blocking the executing threads.
     */
    private final ExecutorService commandHandler;

    /**
     * Standard constructor for the Daemon, privately accessible to only permit static instantiation.
     *
     * @param player The player to be used by this daemon instance.
     */
    private Daemon(ProxiedPlayer player) {
        this.player = player;
        this.commandHandler = Executors.newSingleThreadExecutor();
    }

    /**
     * Static constructor for the daemon instance. We don't want any loose daemons running around.
     *
     * @param player The player to be used by this daemon instance.
     */
    public static void makeInstance(ProxiedPlayer player) {
        instance.set(new Daemon(player));
        latch.get().countDown();
    }

    /**
     * Blocking method to get the daemon instance. You should only call this if you _really_ need the daemon.
     *
     * @return daemon The daemon instance.
     * @throws InterruptedException If the thread waiting for the daemon is interrupted.
     */
    public static Daemon getInstance() throws InterruptedException {
        latch.get().await();
        return instance.get();
    }

    public static boolean hasInstance() {
        return latch.get().getCount() == 0;
    }

    public static void deleteInstance() {
        latch.set(new CountDownLatch(1));
    }

    /**
     * Submits a list of commands to execute, in player chat notation. These will be executed serverside, so you cannot
     * use commands that are only known by the BungeeCord instance.
     *
     * @param commands The list of commands to execute, in order of execution.
     */
    public void submitCommands(List<String> commands) {
        final List<String> actual = Collections.unmodifiableList(commands);
        commandHandler.submit(new Runnable() {
            @Override
            public void run() {
                for (String command : actual) {
                    player.chat(command);
                }
            }
        });
    }

}
