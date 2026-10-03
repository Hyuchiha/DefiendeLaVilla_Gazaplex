package com.hyuchiha.village_defense.Config;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class ConfigManager {
  private static final TreeMap<String, Configuration> configs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  // Solo archivos de ajustes. arenas/shops/kits son datos del admin: un default del jar
  // resucitaria arenas/kits/items borrados, y en <=1.15 getConfigurationSection de una
  // seccion ausente la crea vacia en el archivo (arenas.yml se guarda).
  private static final List<String> JAR_DEFAULTS = Arrays.asList("config.yml", "messages.yml");

  private final Main plugin;

  private final File configFolder;

  public ConfigManager(Main plugin) {
    this.plugin = plugin;
    this.configFolder = plugin.getDataFolder();

    if (!this.configFolder.exists()) {
      this.configFolder.mkdirs();
    }
  }

  public void loadConfigFile(String filename) {
    loadConfigFiles(filename);
  }

  public void loadConfigFiles(String... filenames) {
    for (String filename : filenames) {
      File configFile = new File(this.configFolder, filename);

      try {
        if (!configFile.exists()) {
          if (!configFile.createNewFile()) {
            this.plugin.getLogger().warning("Could not create configuration file " + filename);
          }
          InputStream in = this.plugin.getResource(filename);
          if (in != null) {
            try (InputStream input = in;
                 OutputStream out = new FileOutputStream(configFile)) {
              byte[] buf = new byte[1024];
              int len;
              while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
              }
            } catch (IOException e) {
              Output.logError("Error reading configuration");
            }
          } else {
            this.plugin.getLogger().warning("Default configuration for " + filename + " missing");
          }
        }

        Configuration config = new Configuration(configFile);
        config.load();
        if (JAR_DEFAULTS.contains(filename)) {
          applyJarDefaults(config.getConfig(), filename);
        }
        configs.put(filename, config);
      } catch (IOException | InvalidConfigurationException e) {
        Output.logError("Error in the configuration");
        e.printStackTrace();
      }
    }
  }

  public void save(String filename) {
    if (configs.containsKey(filename)) {
      try {
        configs.get(filename).save();
      } catch (IOException | InvalidConfigurationException e) {
        printException(e, filename);
      }
    }
  }

  public void reload(String filename) {
    if (configs.containsKey(filename)) {
      try {
        configs.get(filename).load();
      } catch (IOException | InvalidConfigurationException e) {
        printException(e, filename);
      }
    }
  }

  public void reloadAll() {
    for (String filename : configs.keySet()) {
      reload(filename);
    }
  }

  public YamlConfiguration getConfig(String filename) {
    if (!configs.containsKey(filename)) {
      this.plugin.getLogger().warning("Configuration " + filename + " not loaded; loading on-demand");
      loadConfigFiles(filename);
    }
    if (configs.containsKey(filename)) {
      return configs.get(filename).getConfig();
    }
    this.plugin.getLogger().severe("Configuration " + filename + " could not be loaded; returning empty config");
    return new YamlConfiguration();
  }

  /**
   * Respalda el archivo con la copia del jar: una clave ausente lee el valor de fabrica.
   * Solo lectura (copyDefaults sigue en false, nada se escribe al archivo del admin) y
   * Bukkit conserva los defaults en un load() posterior, asi que reload() los mantiene.
   */
  private void applyJarDefaults(YamlConfiguration config, String filename) {
    InputStream in = this.plugin.getResource(filename);
    if (in == null) {
      return;
    }
    try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      config.setDefaults(YamlConfiguration.loadConfiguration(reader));
    } catch (IOException e) {
      printException(e, filename);
    }
  }

  private void printException(Exception e, String filename) {
    if (e instanceof IOException) {
      this.plugin.getLogger().severe("I/O exception while handling " + filename);
    } else if (e instanceof InvalidConfigurationException) {
      this.plugin.getLogger().severe("Invalid configuration in " + filename);
    }
  }

  private static class Configuration {
    private final File configFile;
    private final YamlConfiguration config;

    public Configuration(File configFile) {
      this.configFile = configFile;
      this.config = new YamlConfiguration();
    }


    public YamlConfiguration getConfig() {
      return this.config;
    }


    public void load() throws IOException, InvalidConfigurationException {
      this.config.load(this.configFile);
    }


    public void save() throws IOException, InvalidConfigurationException {
      this.config.save(this.configFile);
    }
  }
}
