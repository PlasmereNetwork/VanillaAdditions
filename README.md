# VanillaAdditions
A BungeeCord plugin for adding functionality to Vanilla servers.

### What's the point?
Imagine that you're a server owner for a Vanilla server. VanillaAdditions
allows you to add commands to your server that get executed asynchronously
from a BungeeCord instance through a Daemon (probably with operator-level
privileges). This means that you can add custom behavior to your
Vanilla server without ever modifying the server jar.

### That sounds a little too good to be true...
There are obviously some functional limitations. For example, if you
use the Daemon to execute some behavior, you have to find a way to cause 
that behavior with the existing Minecraft commands. If you wish to have
behavior that doesn't necessarily need to have effect on the server, just
keep it in the BungeeCord instance. The players will never know the 
difference.

### Okay, cool. Now how do I use it?
It's quite simple. The steps below assume that you're altering an existing
simple vanilla installation.
1. Download [the latest build of BungeeCord](https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar).
2. Put it wherever you think it should go. Probably in its own folder.
3. Run BungeeCord with `java -jar BungeeCord.jar`, then cancel it after it starts up.
4. It is highly suggested to use the configs under "BungeeCord Configs".
  - Make sure to replace the yml constants with what your setup needs.
5. Download the VanillaAdditions plugin from the [releases tab](https://github.com/TrulyFree/VanillaAdditions/releases)
and place it in the `plugins` directory of the BungeeCord server.
6. Go to your MC server.properties. Add one to the port and set
online-mode to "false".
7. Setup a Daemon if you wish to. (see instructions below)
8. Start up the Minecraft Server instance. This should not be externally
accessible.
9. Start up the BungeeCord instance.
10. If applicable, start up the Daemon instance.
11. Profit.

The following directions only apply if you want to have a Daemon player.

1. Under `plugins`, make the `VanillaAdditions` directory. Inside this 
directory, add a JSON file with the following content: `{"username":"<username>"}`,
where you replace `<username>` with the Daemon's username.
2. Create a bot client running from localhost with the username specified in the JSON file.
  - You do not need to own the account for the Daemon instance as 
  VanillaAdditions will automatically allow Daemon instances to enter
  from localhost unauthenticated.
  - I strongly suggest using [mineflayer](https://github.com/PrismarineJS/mineflayer),
  but ensure that you provide a false UUID for the mineflayer instance so that
  VanillaAdditions can register the Daemon correctly.
  
### Awesome. So how do I add commands?
1. Setup a project for the [BungeeCord plugin development](https://www.spigotmc.org/wiki/create-your-first-bungeecord-plugin-proxy/).
2. Add the following repository to your POM:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
3. Add the following dependency to your POM (replacing `VERSION` with your
intended version):
```xml
<dependency>
    <groupId>com.github.TrulyFree</groupId>
    <artifactId>VanillaAdditions</artifactId>
    <version>VERSION</version>
</dependency>
```
4. Write up commands which extend `io.github.trulyfree.va.command.commands.TabbableCommand`.
5. Register instances of these commands with the BungeeCord PluginManager
under your plugin.
6. Compile your plugin

### That's confusing. Example?
Sure. The guy who developed this also developed 
[this thing](https://github.com/TheIcebergMC/CommandAdditions), which is
a working example of how to use the VanillaAdditions API.
