package co.kepler.fastcraft.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.material.MaterialData;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.Util;

/**
 * Manages the config file items.yml.
 * 
 * @author Kepler_
 */
public class ItemsConfig {
	private final YamlConfiguration config;
	private final File configFile;

	public ItemsConfig() {
		config = new YamlConfiguration();
		configFile = new File(FastCraft.get().getDataFolder(), "items.yml");
	}

	public void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
		if (!configFile.exists()) {
			FastCraft.get().saveResource("items.yml", false);
		}
		Util.loadYaml(config, configFile);
	}

	@SuppressWarnings("deprecation")
	public String getItemName(MaterialData material) {
		if (material == null) {
			return null;
		}

		// Get the key for the config
		StringBuilder key = new StringBuilder();
		key.append(material.getItemTypeId());
		if (material.getData() == -1) {
			key.append("-*");
		} else {
			key.append('-').append(material.getData());
		}

		// Return the name if it's in the config
		String result = config.getString(key.toString());
		if (result != null) return result;

		// Otherwise, get the name internally from Minecraft
		result = material.getItemType().toString();
		if (material.getData() != -1 && material.getData() != 0) {
			result += " (" + material.getData() + ")";
		}

		return WordUtils.capitalizeFully(result.replace("_", " "));
	}
}
