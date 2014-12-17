package me.bw.fastcraft;

import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions {
	private static Permission permissions;
	public static void setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider;
		try{
			permissionProvider = FastCraft.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		}catch(NoClassDefFoundError e){
			permissionProvider = null;
		}
		
		if (permissionProvider == null){
			permissions = null;
		}else{
			permissions = permissionProvider.getProvider();
		}
	}

	public static boolean playerHas(CommandSender sender, String permission){
		if (sender == Bukkit.getConsoleSender())
			return true;		
		if (permissions != null)
			return permissions.has(sender, permission);
		
		YamlConfiguration perms = FastCraft.permsConfig;
		List<String> userPerms;
		if (perms.getConfigurationSection("users").contains(sender.getName()))
			 userPerms = perms.getStringList("users." + sender.getName());
		else
			userPerms = perms.getStringList(sender.isOp() ? "op" : "default");
		return userPerms.contains(permission);
	}
}
