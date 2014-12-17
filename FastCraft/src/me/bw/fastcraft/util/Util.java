package me.bw.fastcraft.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.bw.fastcraft.FastCraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.material.MaterialData;

public class Util {
	public static YamlConfiguration copyNewConfigKeys(YamlConfiguration from, YamlConfiguration to){
		for (String key : from.getKeys(true)){
			if (to.get(key) == null){
				to.set(key, from.get(key));
				System.out.println("[FastCraft] Added key to lang file: " + key);
			}
		}
		return to;
	}

	public static boolean isDouble(String s){
		try {  
			Double.parseDouble(s);  
			return true;  
		} catch(NumberFormatException e){  
			return false;
		}
	}
	public static boolean isInt(String s){
		try {  
			Integer.parseInt(s);
			return true;  
		} catch(NumberFormatException e){  
			return false;
		}
	}
	public static boolean isByte(String s){
		try {  
			Byte.parseByte(s);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}
	}

	public static String getLang(String entry, String... vars){
		String val = FastCraft.customLangConfig.getString(entry);
		if (val == null)
			val = FastCraft.langConfig.getString(entry);
		if (val == null)
			val = "[error-langMissing: " + entry + "]"; 
		else if (vars.length > 0)
			for (int x = 0; x < vars.length; x++)
				val = val.replace("%%" + x, vars[x]);

		return val.replace("&", "§").replace("%%n", "\n");
	}
	public static void sendLang(CommandSender sender, String lang, String... vars){
		String message = getLang(lang, vars);
		if (!message.equals(""))
			for (String m : message.split("\n"))
				sender.sendMessage((sender.equals(Bukkit.getConsoleSender()) ? "[FastCraft] " : "") + m);
	}

	public static MaterialData parseItem(String item){
		String[] split = item.split(":");
		String material = "0";
		String data = "-1";
		switch (split.length){
		case 2: data = split[1];
		case 1: material = split[0];
		}
		if (isInt(material) && isByte(data)){
			return new MaterialData(Integer.parseInt(material), Byte.parseByte(data));
		}
		return new MaterialData(Material.AIR);
	}

	@SuppressWarnings("deprecation")
	public static YamlConfiguration configFromResource(String resource){
		YamlConfiguration result = null;
		try {
			Reader reader = new InputStreamReader(FastCraft.plugin.getResource(resource), "UTF8");
			if (reader != null) {
				result = YamlConfiguration.loadConfiguration(reader);
			}
		}catch (NoSuchMethodError e) {
		}catch (UnsupportedEncodingException e) {}

		if (result == null)
			result = YamlConfiguration.loadConfiguration(FastCraft.plugin.getResource(resource));
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyConfigSection(YamlConfiguration config, String fromPath, String toPath){
		Map<String, Object> vals = config.getConfigurationSection(fromPath).getValues(true);
		String toDot = toPath.equals("") ? "" : ".";
		for (String s : vals.keySet()){
			System.out.println(s);
			Object val = vals.get(s);
			if (val instanceof List)
				val = new ArrayList((List)val);
			config.set(toPath + toDot + s, val);
		}
	}

	private static int build = -1;
	public static int getBukkitBuild() {
		if (build > -1) return build;
		Pattern pattern = Pattern.compile("(b)([0-9]+)(jnks)");
		Matcher matcher = pattern.matcher(Bukkit.getVersion());
		if (matcher.find()) {
			try{
				build = Integer.parseInt(matcher.group(2));
			}catch(NumberFormatException e){}
		}
		return build;
	}
}
