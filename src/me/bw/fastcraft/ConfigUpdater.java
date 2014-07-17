package me.bw.fastcraft;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class ConfigUpdater {
	public static void update(){
		YamlConfiguration conf = FastCraft.config;
		File file = FastCraft.configFile;

		boolean updateLang = false;
		switch (conf.getInt("version-DoNotEdit", -1)){
		case -1: vx_v1(conf, file);
		case  1: v1_v2(conf, file); updateLang = true;
		case  2: v2_v3(conf, file); updateLang = true;
		case  3:
		case  4:
		case  5:
		case  6:
		case  7: v7_v8(conf, file); updateLang = true;
		case  8: v8_v9(conf, file); updateLang = true;
		case  9: v9_v10(conf, file); updateLang = true;
		case 10:
		default://Current Version
		}
		conf.set("version-DoNotEdit", FastCraft.version);

		conf = Methods.copyNewConfigKeys(YamlConfiguration.loadConfiguration(FastCraft.plugin.getResource("config.yml")), conf);
		try {
			conf.save(FastCraft.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FastCraft.config = conf;
		
		if (updateLang){
			File destParent = new File(FastCraft.langConfigFile.getParentFile().getAbsolutePath(), "/Old Language Files");
			destParent.mkdir();
			File dest = new File(destParent.getAbsoluteFile(), new SimpleDateFormat("yyyy-MM-dd, HH;mm;ss").format(new Date()) + ".yml");
			try {
				Files.move(FastCraft.langConfigFile, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void v9_v10(YamlConfiguration conf, File file){
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
	private static void v1_v2(YamlConfiguration conf, File file){
	}
	private static void vx_v1(YamlConfiguration conf, File file){
		FastCraft.plugin.saveDefaultConfig();
		conf = YamlConfiguration.loadConfiguration(FastCraft.configFile);
	}
}
