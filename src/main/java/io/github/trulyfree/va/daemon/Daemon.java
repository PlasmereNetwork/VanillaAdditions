package io.github.trulyfree.va.daemon;

import io.github.trulyfree.va.events.DaemonChatEvent;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
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
     * The process associated with the Daemon instance.
     */
    private static final AtomicReference<Process> process = new AtomicReference<>();

    /**
     * ExecutorService handling the output of the child daemon process.
     */
    private static final AtomicReference<ExecutorService> executor = new AtomicReference<>();

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
    static void makeInstance(ProxiedPlayer player) {
        instance.set(new Daemon(player));
        latch.get().countDown();
    }

    /**
     * Blocking method to get the daemon instance. You should only call this if you _really_ need the daemon.
     *
     * @return daemon The daemon instance.
     * @throws InterruptedException If the thread waiting for the daemon is interrupted.
     */
    @SuppressWarnings("unused")
    public static Daemon getInstance() throws InterruptedException {
        latch.get().await();
        return instance.get();
    }

    /**
     * Non-blocking method to get the daemon instance. This may return null.
     *
     * @return daemon The daemon instance as of right now.
     */
    static Daemon getInstanceNow() {
        return instance.get();
    }

    /**
     * Non-blocking method to check if a daemon exists. This should only be used for existence-checking. Otherwise,
     * use getInstanceNow and check for null.
     *
     * @return hasInstance Whether or not the instance exists.
     */
    public static boolean hasInstance() {
        return latch.get().getCount() == 0;
    }

    /**
     * Delete the current daemon instance and reset the latch.
     */
    static void deleteInstance() {
        latch.set(new CountDownLatch(1));
        instance.set(null);
    }

    static void spawn(final ProcessBuilder builder) throws IOException {
        if (executor.get() != null && !executor.get().isShutdown()) {
            return;
        }
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        final AtomicBoolean shouldContinue = new AtomicBoolean(true);
        final AtomicReference<CountDownLatch> latch = new AtomicReference<>(new CountDownLatch(1));
        final AtomicReference<CountDownLatch> check = new AtomicReference<>(new CountDownLatch(2));
        final AtomicReference<BufferedReader> output = new AtomicReference<>();
        final AtomicReference<BufferedReader> error = new AtomicReference<>();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                while (shouldContinue.get()) {
                    try {
                        latch.get().await();
                        check.get().countDown();
                    } catch (InterruptedException ignored) {
                    }
                    try {
                        for (String line; (line = output.get().readLine()) != null; ) {
                            ProxyServer.getInstance().getPluginManager().callEvent(new DaemonChatEvent(line));
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                while (shouldContinue.get()) {
                    try {
                        latch.get().await();
                        check.get().countDown();
                        if (!shouldContinue.get()) {
                            break;
                        }
                    } catch (InterruptedException ignored) {
                    }
                    try {
                        for (String line; (line = error.get().readLine()) != null; ) {
                            ProxyServer.getInstance().getLogger().warning(line);
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                do {
                    long startup = System.currentTimeMillis();
                    try {
                        process.set(builder.start());
                        output.set(new BufferedReader(new InputStreamReader(process.get().getInputStream())));
                        error.set(new BufferedReader(new InputStreamReader(process.get().getErrorStream())));
                        latch.get().countDown();
                    } catch (IOException e) {
                        break;
                    }
                    if (Thread.interrupted()) {
                        break;
                    }
                    try {
                        check.get().await();
                        latch.set(new CountDownLatch(1));
                        process.get().waitFor();
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (System.currentTimeMillis() - startup < 10 * 1000) {
                        ProxyServer.getInstance().getLogger().severe("Daemon process lasted less than 10 seconds, killing...");
                        executor.shutdownNow();
                        break;
                    }
                } while (!Thread.interrupted());
                process.get().destroy();
                shouldContinue.set(false);
                latch.get().countDown();
            }
        });
    }

    /**
     * Submits a list of commands to execute, in player chat notation. These will be executed serverside, so you cannot
     * use commands that are only known by the BungeeCord instance.
     *
     * @param commands The list of commands to execute, in order of execution.
     */
    @SuppressWarnings("unused")
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
