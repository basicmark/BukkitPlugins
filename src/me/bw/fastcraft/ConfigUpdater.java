package me.bw.fastcraft;

import java.io.File;
import java.io.IOException;

import me.bw.fastcraft.util.Util;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUpdater {
	public static void update(){
		YamlConfiguration conf = FastCraft.config;
		File file = FastCraft.configFile;

		switch (conf.getInt("version-DoNotEdit", -1)){
		default:
		case -1: vx_v1(conf, file);
		case  1:
		case  2: v2_v3(conf, file);
		case  3:
		case  4:
		case  5:
		case  6:
		case  7: v7_v8(conf, file);
		case  8: v8_v9(conf, file);
		case  9:
		case 10:
		case 11: v11_v12(conf, file);
		case 12:
		case 13:
		case 14:
		}
		conf.set("version-DoNotEdit", FastCraft.version);

		conf = Util.copyNewConfigKeys(Util.configFromResource("config.yml"), conf);
		try {
			conf.save(FastCraft.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FastCraft.config = conf;
	}

	private static void v11_v12(YamlConfiguration conf, File file){
		conf.set("language", "EN");
	}
	private static void v8_v9(YamlConfiguration conf, File file){
		conf.set("notifyPlayersOnUpdate", new String[0]);
	}
	private static void v7_v8(YamlConfiguration conf, File file){
		conf.set("disabledRecipes", new String[]{"358","401"});
		conf.set("disabledIngredients", new String[]{"358","395","403"});
	}
	private static void v2_v3(YamlConfiguration conf, File file){
		conf.set("buttonHelpItem", "387:0");
		conf.set("fastCraftDefaultEnabled", true);
	}
	private static void vx_v1(YamlConfiguration conf, File file){
		FastCraft.plugin.saveDefaultConfig();
		conf = YamlConfiguration.loadConfiguration(FastCraft.configFile);
	}
}
