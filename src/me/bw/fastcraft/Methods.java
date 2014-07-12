package me.bw.fastcraft;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class Methods {
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

	public static Player getPlayer(String name){
		Player result = Bukkit.getPlayer(name);
		if (result != null) return result;
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getName().toLowerCase().contains(name.toLowerCase())) return p;
		}
		return null;
	}
	
	public static String getLang(String entry, String... vars){
		String val = FastCraft.langConfig.getString(entry);
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
		return new MaterialData(0);
	}
}
