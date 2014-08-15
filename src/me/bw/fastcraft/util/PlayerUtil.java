package me.bw.fastcraft.util;

import me.bw.fastcraft.FastCraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {
	public static String getIdentifier(Player p){
		if (canUseUUID())
			return p.getUniqueId().toString();
		return p.getName();
	}

	public static boolean canUseUUID(){
		return Util.getBukkitBuild() >= 3035;
	}

	public static Player getOnlinePlayer(String name){
		for (Player p : Bukkit.getOnlinePlayers())
			if (getIdentifier(p).equals(name))
				return p;
		return null;
	}

	public static boolean getPlayerPreference(Player p, String preference){
		String key = getIdentifier(p) + "." + preference;
		return FastCraft.playerPrefsConfig.getBoolean(key);
	}
	public static void setPlayerPreference(Player p, String preference, boolean value){
		setPlayerPreference(getIdentifier(p), preference, value);
	}
	public static void setPlayerPreference(String p, String preference, boolean value){
		String key = p + "." + preference;
		FastCraft.playerPrefsConfig.set(key, value);
	}
}
