package com.hyuchiha.village_defense.Utils;

import com.hyuchiha.village_defense.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Barra de vida sobre la cabeza de los mobs usando el nameplate nativo
 * (setCustomName), sin NMS ni armor stands.
 * <p>
 * La barra aparece cuando el mob recibe dano y se oculta sola tras
 * HealthBar.hide-after segundos sin recibir mas. El nombre base del mob
 * (por ejemplo "[BOSS] Nombre") se conserva y se restaura al ocultar.
 * <p>
 * Coste por golpe: cero allocations y cero tareas nuevas. Las variantes de la
 * barra estan precalculadas y el nameplate solo se reenvia cuando cambia el
 * numero de segmentos, no en cada golpe. Un unico timer compartido se ocupa de
 * ocultar, y se cancela solo cuando no queda ninguna barra activa.
 *
 * @author hyuchiha
 */
public final class HealthBarUtils {

  // Separador entre el nombre base del mob y la barra.
  private static final String SEPARATOR = ChatColor.DARK_GRAY + " | " + ChatColor.RESET;
  private static final String HEART = "❤";
  private static final String SEGMENT = "■";
  // Resolucion del ocultado. Medio segundo basta y el timer solo corre cuando
  // hay barras vivas.
  private static final long SWEEP_PERIOD_TICKS = 10L;

  private static final Map<UUID, Bar> active = new HashMap<>();

  // rendered[n] = barra con n segmentos llenos. Precalculado en init().
  private static String[] rendered = new String[0];
  private static boolean enabled = false;
  private static int length = 10;
  private static long hideAfterMillis = 5000L;
  private static BukkitTask sweeper;

  private HealthBarUtils() {
  }

  /**
   * Estado por entidad. Guarda el nombre base para no tener que volver a
   * parsearlo del nameplate en cada golpe.
   */
  private static final class Bar {
    final LivingEntity entity;
    final String base;
    long lastHit;
    int filled = -1;

    Bar(LivingEntity entity) {
      this.entity = entity;
      this.base = baseName(entity.getCustomName());
    }
  }

  public static void init(Configuration config) {
    if (sweeper != null) {
      sweeper.cancel();
      sweeper = null;
    }
    active.clear();

    enabled = config.getBoolean("HealthBar.enabled", true);
    length = Math.max(1, Math.min(20, config.getInt("HealthBar.length", 10)));
    hideAfterMillis = Math.max(0, config.getInt("HealthBar.hide-after", 5)) * 1000L;

    precompute("hearts".equalsIgnoreCase(config.getString("HealthBar.style", "bar")) ? HEART : SEGMENT);
  }

  public static boolean isEnabled() {
    return enabled;
  }

  /**
   * Pinta la barra con la vida indicada.
   *
   * @param health vida que tendra el mob (el evento de dano aun no la ha aplicado)
   */
  public static void update(LivingEntity entity, double health) {
    if (!enabled || entity == null || !entity.isValid()) {
      return;
    }

    double max = entity.getMaxHealth();
    if (max <= 0) {
      return;
    }

    UUID id = entity.getUniqueId();
    Bar bar = active.get(id);
    if (bar == null) {
      bar = new Bar(entity);
      active.put(id, bar);
      startSweeper();
    }
    bar.lastHit = System.currentTimeMillis();

    int filled = segmentsFor(Math.max(0, Math.min(health, max)) / max);
    if (bar.filled == filled) {
      // Mismo dibujo: reenviar el nameplate solo gastaria un packet de metadata
      // a cada jugador que trackea la entidad.
      return;
    }
    bar.filled = filled;

    entity.setCustomName(bar.base.isEmpty() ? rendered[filled] : bar.base + SEPARATOR + rendered[filled]);
    entity.setCustomNameVisible(true);
  }

  private static void startSweeper() {
    if (sweeper != null) {
      return;
    }
    sweeper = Bukkit.getScheduler().runTaskTimer(
        Main.getInstance(), HealthBarUtils::sweep, SWEEP_PERIOD_TICKS, SWEEP_PERIOD_TICKS);
  }

  /**
   * Oculta las barras vencidas y suelta las entidades que ya no existen. Se
   * cancela sola en cuanto no queda nada que vigilar.
   */
  private static void sweep() {
    long now = System.currentTimeMillis();

    Iterator<Map.Entry<UUID, Bar>> iterator = active.entrySet().iterator();
    while (iterator.hasNext()) {
      Bar bar = iterator.next().getValue();

      // Muerta o en chunk descargado: no dejar viva la referencia a la entidad.
      if (!bar.entity.isValid()) {
        iterator.remove();
        continue;
      }

      // hide-after 0 = la barra no se oculta nunca, pero la entrada se mantiene
      // para seguir deduplicando repintados.
      if (hideAfterMillis > 0 && now - bar.lastHit >= hideAfterMillis) {
        restore(bar);
        iterator.remove();
      }
    }

    if (active.isEmpty()) {
      sweeper.cancel();
      sweeper = null;
    }
  }

  /**
   * Quita la barra dejando solo el nombre base del mob (si tenia uno).
   */
  private static void restore(Bar bar) {
    bar.entity.setCustomName(bar.base.isEmpty() ? null : bar.base);
    bar.entity.setCustomNameVisible(!bar.base.isEmpty());
  }

  private static void precompute(String symbol) {
    rendered = new String[length + 1];

    for (int filled = 0; filled <= length; filled++) {
      StringBuilder bar = new StringBuilder(colorFor((double) filled / length).toString());
      for (int i = 0; i < length; i++) {
        if (i == filled) {
          bar.append(ChatColor.GRAY);
        }
        bar.append(symbol);
      }
      rendered[filled] = bar.toString();
    }
  }

  private static int segmentsFor(double percentage) {
    int filled = (int) Math.ceil(percentage * length);
    if (filled <= 0 && percentage > 0) {
      // Un mob vivo nunca debe verse con la barra vacia.
      filled = 1;
    }
    return Math.min(filled, length);
  }

  private static String baseName(String customName) {
    if (customName == null) {
      return "";
    }
    int index = customName.indexOf(SEPARATOR);
    if (index >= 0) {
      return customName.substring(0, index);
    }
    // Nameplate que ya era solo barra (mob sin nombre al que sobrevivio la barra
    // a un /reload): no es nombre base.
    return isBarOnly(customName) ? "" : customName;
  }

  private static boolean isBarOnly(String customName) {
    String stripped = ChatColor.stripColor(customName).trim();
    if (stripped.isEmpty()) {
      return true;
    }
    for (int i = 0; i < stripped.length(); i++) {
      String character = stripped.substring(i, i + 1);
      if (!character.equals(HEART) && !character.equals(SEGMENT)) {
        return false;
      }
    }
    return true;
  }

  private static ChatColor colorFor(double percentage) {
    if (percentage > 0.66) {
      return ChatColor.GREEN;
    }
    return percentage > 0.33 ? ChatColor.YELLOW : ChatColor.RED;
  }
}
