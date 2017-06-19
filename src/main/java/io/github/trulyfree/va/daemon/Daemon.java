package io.github.trulyfree.va.daemon;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public final class Daemon {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final AtomicReference<Daemon> instance = new AtomicReference<>();

    private final ProxiedPlayer player;
    private final ExecutorService commandHandler;


    Daemon(ProxiedPlayer player) {
        this.player = player;
        this.commandHandler = Executors.newSingleThreadExecutor();
        instance.set(this);
        latch.countDown();
    }

    public static Daemon getInstance() throws InterruptedException {
        latch.await();
        return instance.get();
    }

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
