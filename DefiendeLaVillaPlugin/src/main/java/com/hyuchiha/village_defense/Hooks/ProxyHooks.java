package com.hyuchiha.village_defense.Hooks;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Messages.Translator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Unico punto de envio de jugadores al lobby del proxy.
 *
 * <p>Un solo camino para BungeeCord/Waterfall y Velocity: Velocity contesta el mismo
 * canal {@code BungeeCord} mientras {@code bungee-plugin-message-channel = true} en
 * {@code velocity.toml} (su default). En 1.13+ Bukkit traduce el nombre legacy a
 * {@code bungeecord:main} solo.
 *
 * <p>Config: {@code proxy.enabled}, {@code proxy.lobby-server}, {@code proxy.send-on-end},
 * leidas en cada envio (pasa una vez por jugador por partida) de la config de Bukkit
 * ({@code plugin.getConfig()}), que ya trae los defaults del config.yml del jar. Si el
 * archivo del admin no trae {@code proxy.enabled} (server actualizado desde antes de
 * {@code proxy:}), se leen las claves viejas {@code EnableBungeeComunication},
 * {@code ServerToConnect} y {@code AutoReturnToLobby}: se comporta igual que antes.
 *
 * <p>Quien llama ya dejo al jugador en el lobby principal de ESTE server
 * ({@code GamePlayer.sendPlayerToLobby}) antes de pedir el cambio: si el lobby del proxy
 * esta caido o mal escrito, el proxy lo deja aqui y no pierde nada.
 */
public final class ProxyHooks {

  public static final String CHANNEL = "BungeeCord";

  private ProxyHooks() {
  }

  /** Se registra siempre: no hace nada si no se usa. */
  public static void register(Main plugin) {
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
  }

  /** Interruptor general: la brujula "Regresar al lobby" y cualquier envio. */
  public static boolean isEnabled() {
    return isEnabled(Main.getInstance().getConfig());
  }

  /** Salida manual (brujula del lobby principal). Requiere {@code proxy.enabled}. */
  public static void sendToLobby(Player player) {
    Main plugin = Main.getInstance();
    Configuration config = plugin.getConfig();
    if (!isEnabled(config) || player == null || !player.isOnline()) {
      return;
    }
    String server = lobbyServer(config);
    player.sendMessage(Translator.getPrefix()
        + Translator.getColoredString("GAME.RETURNING_TO_LOBBY").replace("%SERVER%", server));
    player.sendPluginMessage(plugin, CHANNEL, connect(server));
  }

  /** Fin de partida o salida de una arena. Requiere ademas {@code proxy.send-on-end}. */
  public static void autoSendToLobby(Player player) {
    if (sendOnEnd(Main.getInstance().getConfig())) {
      sendToLobby(player);
    }
  }

  // isSet ignora los defaults del jar (copyDefaults off); contains() los veria.
  private static boolean legacy(Configuration c) {
    return !c.isSet("proxy.enabled");
  }

  // Claves nuevas sin default explicito: getBoolean(k, def) se saltaria el default del jar.
  // Las viejas ya no estan en el jar: ausentes leen false / "lobby".
  static boolean isEnabled(Configuration c) {
    return legacy(c) ? c.getBoolean("EnableBungeeComunication") : c.getBoolean("proxy.enabled");
  }

  static String lobbyServer(Configuration c) {
    return legacy(c) ? c.getString("ServerToConnect", "lobby") : c.getString("proxy.lobby-server");
  }

  static boolean sendOnEnd(Configuration c) {
    return isEnabled(c)
        && (legacy(c) ? c.getBoolean("AutoReturnToLobby") : c.getBoolean("proxy.send-on-end"));
  }

  /** Mensaje del sub-canal {@code Connect}: dos strings UTF. */
  static byte[] connect(String server) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (DataOutputStream out = new DataOutputStream(bytes)) {
      out.writeUTF("Connect");
      out.writeUTF(server);
    } catch (IOException e) {
      // ByteArrayOutputStream no lanza.
      throw new IllegalStateException(e);
    }
    return bytes.toByteArray();
  }

  public static void main(String[] args) throws Exception {
    java.io.DataInputStream in = new java.io.DataInputStream(
        new java.io.ByteArrayInputStream(connect("lobby-1")));
    assert "Connect".equals(in.readUTF()) : "primero el sub-canal";
    assert "lobby-1".equals(in.readUTF()) : "luego el server destino";
    assert in.read() == -1 : "nada despues del nombre del server";

    // config.yml real del jar (target/classes) como defaults, igual que Bukkit.
    String shipped = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(
        ProxyHooks.class.getResource("/config.yml").toURI())), java.nio.charset.StandardCharsets.UTF_8);
    YamlConfiguration jar = new YamlConfiguration();
    jar.loadFromString(shipped);
    assert !jar.contains("EnableBungeeComunication") : "el jar ya no trae las claves viejas";

    // Instalacion nueva: el archivo es el del jar.
    YamlConfiguration c = file(shipped, jar);
    assert !isEnabled(c) && !sendOnEnd(c) && "lobby".equals(lobbyServer(c)) : "default: apagado";

    c = file("proxy:\n  enabled: true\n  lobby-server: hub\n", jar);
    assert isEnabled(c) && "hub".equals(lobbyServer(c)) && sendOnEnd(c) : "nuevas ganan; send-on-end del jar";
    c = file("proxy:\n  enabled: true\n  send-on-end: false\n", jar);
    assert isEnabled(c) && !sendOnEnd(c) && "lobby".equals(lobbyServer(c)) : "send-on-end respetado";
    c = file("proxy:\n  enabled: false\nEnableBungeeComunication: true\n", jar);
    assert !isEnabled(c) && !sendOnEnd(c) : "con proxy.enabled las viejas se ignoran";

    // Server actualizado: archivo viejo sin proxy:.
    c = file("EnableBungeeComunication: true\nServerToConnect: old\n", jar);
    assert isEnabled(c) && "old".equals(lobbyServer(c)) && !sendOnEnd(c) : "legado: brujula si, auto no";
    c = file("EnableBungeeComunication: true\nAutoReturnToLobby: true\n", jar);
    assert sendOnEnd(c) && "lobby".equals(lobbyServer(c)) : "legado con AutoReturnToLobby";
    c = file("proxy:\n  lobby-server: hub\nEnableBungeeComunication: true\n", jar);
    assert isEnabled(c) && "lobby".equals(lobbyServer(c)) : "sin proxy.enabled manda el legado";

    c = file("Game:\n  money: 1\n", jar);
    assert !isEnabled(c) && !sendOnEnd(c) : "ni nuevas ni viejas: apagado";
    System.out.println("ProxyHooks self-check OK");
  }

  private static YamlConfiguration file(String yaml, YamlConfiguration jar) throws Exception {
    YamlConfiguration c = new YamlConfiguration();
    c.loadFromString(yaml);
    c.setDefaults(jar);
    return c;
  }
}
