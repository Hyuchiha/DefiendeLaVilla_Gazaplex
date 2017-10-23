package com.hyuchiha.village_defense.Config;

import com.hyuchiha.village_defense.Main;
import com.hyuchiha.village_defense.Output.Output;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.TreeMap;

public class ConfigManager {
    private static final TreeMap<String, Configuration> configs = new TreeMap<String, Configuration>(
            String.CASE_INSENSITIVE_ORDER);

    private final Main plugin;
    private final File configFolder;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        configFolder = plugin.getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
    }

    public void loadConfigFile(String filename) {
        loadConfigFiles(filename);
    }

    public void loadConfigFiles(String... filenames) {
        for (String filename : filenames) {
            File configFile = new File(configFolder, filename);
            Configuration config;
            try {
                if (!configFile.exists()) {
                    configFile.createNewFile();
                    InputStream in = plugin.getResource(filename);
                    if (in != null) {
                        try {
                            OutputStream out = new FileOutputStream(configFile);
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            out.close();
                            in.close();
                        } catch (IOException e) {
                            Output.logError("Error leyendo la configuracion");
                        }
                    } else {
                        plugin.getLogger().warning(
                                "Default configuration for " + filename
                                        + " missing");
                    }
                }
                config = new Configuration(configFile);
                config.load();
                configs.put(filename, config);
            } catch (    IOException e) {
                Output.logError("Error leyendo la configuracion");
            }
            catch(InvalidConfigurationException e) {
                Output.logError("Error leyendo la configuracion");
            }
        }
    }

    public void save(String filename) {
        if (configs.containsKey(filename)) {
            try {
                configs.get(filename).save();
            } catch (IOException e) {
                printException(e, filename);
            }
        }
    }
    
    public void reload(String filename) {
        if (configs.containsKey(filename)) {
            try {
                configs.get(filename).load();
            } catch (IOException e) {
                printException(e, filename);
            } catch (InvalidConfigurationException e) {
                printException(e, filename);
            }

        }
    }

    public YamlConfiguration getConfig(String filename) {
        if (configs.containsKey(filename))
            return configs.get(filename).getConfig();
        else
            return null;
    }

    private void printException(Exception e, String filename) {
        if (e instanceof IOException) {
            plugin.getLogger().severe(
                    "I/O exception while handling " + filename);
        } else if (e instanceof InvalidConfigurationException) {
            plugin.getLogger().severe("Invalid configuration in " + filename);
        }

    }

    private static class Configuration {

        private final File configFile;
        private YamlConfiguration config;

        public Configuration(File configFile) {
            this.configFile = configFile;
            config = new YamlConfiguration();
        }

        public YamlConfiguration getConfig() {
            return config;
        }

        public void load() throws IOException, InvalidConfigurationException {
            config.load(configFile);
        }

        public void save() throws IOException {
            config.save(configFile);
        }
    }
}
