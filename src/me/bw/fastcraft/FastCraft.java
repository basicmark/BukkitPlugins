package me.bw.fastcraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FastCraft extends JavaPlugin {
	public static String pluginDir = "plugins" + File.separator + "FastCraft" + File.separator;
	public static File configFile = new File(pluginDir + "config.yml");
	public static File langConfigFile = new File(pluginDir + "language.yml");
	public static File ingredientsConfigFile = new File(pluginDir + "ingredients.yml");
	public static File playerPrefsConfigFile = new File(pluginDir + "playerPrefs.yml");
	public static File permsConfigFile = new File(pluginDir + "permissions.yml");
	public static YamlConfiguration config;
	public static YamlConfiguration langConfig;
	public static YamlConfiguration ingredientsConfig;
	public static YamlConfiguration playerPrefsConfig;
	public static YamlConfiguration permsConfig;
	public static JavaPlugin plugin;
	public static int version;

	public static InventoryManager inventoryManager;

	public void onEnable(){
		plugin = this;
		
		inventoryManager = new InventoryManager();

		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(inventoryManager, this);
		manager.registerEvents(new EventListener(), this);

		Permissions.setupPermissions();
		version = Integer.parseInt(this.getDescription().getVersion());
		
		reload();
		MetricsStarter.start(this);
		PluginUpdater.setup(this, this.getFile());
	}
	public void onDisable(){
		inventoryManager.closeInventories();
		try {
			if (playerPrefsConfig != null) playerPrefsConfig.save(playerPrefsConfigFile);
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void reload(){
		onDisable();
		
		if (!new File(pluginDir).exists()){
			new File(pluginDir).mkdir();
		}
		if (!configFile.exists()){
			this.saveDefaultConfig();
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		ConfigUpdater.update();

		if (!langConfigFile.exists())
			this.saveResource("language.yml", false);
		if (!ingredientsConfigFile.exists())
			this.saveResource("ingredients.yml", false);
		if (!playerPrefsConfigFile.exists())
			this.saveResource("playerPrefs.yml", false);
		if (!permsConfigFile.exists())
			this.saveResource("permissions.yml", false);
		langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
		ingredientsConfig = YamlConfiguration.loadConfiguration(ingredientsConfigFile);
		playerPrefsConfig = YamlConfiguration.loadConfiguration(playerPrefsConfigFile);
		permsConfig = YamlConfiguration.loadConfiguration(permsConfigFile);
		
		Settings.load();
		
		for (Player p : Bukkit.getOnlinePlayers())
			PluginUpdater.notifyOfUpdateIfNeeded(p);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equals("fastcraft")){
			if (!Permissions.playerHas(sender, "fastcraft.use")){
				Methods.sendLang(sender, "errNoPerm", "fastcraft.use");
				return true;
			}
			if (args.length == 0){
				Methods.sendLang(sender, "outFc");
			}else{
				if (args[0].equalsIgnoreCase("admin")){
					if (!Permissions.playerHas(sender, "fastcraft.admin")){
						Methods.sendLang(sender, "errNoPerm", "fastcraft.admin");
						return true;
					}
					if (args.length == 1){
						Methods.sendLang(sender, "outFcAdmin");
					}else if (args[1].equalsIgnoreCase("reload")){
						if (!Permissions.playerHas(sender, "fastcraft.admin.reload")){
							Methods.sendLang(sender, "errNoPerm", "fastcraft.admin.reload");
							return true;
						}
						if (args.length == 2){
							reload();
							Methods.sendLang(sender, "outFcAdminReload");
						}else{
							Methods.sendLang(sender, "cmdFcAdminReload");
						}
					}else if (args[1].equalsIgnoreCase("update")){
						if (!Permissions.playerHas(sender, "fastcraft.admin.update")){
							Methods.sendLang(sender, "errNoPerm", "fastcraft.admin.update");
							return true;
						}
						if (args.length == 2){
							PluginUpdater.update(sender);
						}else{
							Methods.sendLang(sender, "cmdFcAdminUpdate");
						}
					}else{
						Methods.sendLang(sender, "cmdFcAdmin");
					}
				}else if (args[0].equalsIgnoreCase("toggle")){
					if (!Permissions.playerHas(sender, "fastcraft.toggle")){
						Methods.sendLang(sender, "errNoPerm", "fastcraft.toggle");
					}else{
						if (args.length == 1){
							if (sender instanceof Player){
								Player player = (Player)sender;
								boolean newVal = inventoryManager.togglePlayer(player.getName());
								if (newVal){
									Methods.sendLang(player, "outFcToggleOn");
								}else{
									Methods.sendLang(player, "outFcToggleOff");
								}
							}else{
								Methods.sendLang(sender, "cmdFcToggle");
							}	
						}else if (args.length == 2){
							if (sender instanceof Player){
								Player player = (Player)sender;
								if (args[1].equalsIgnoreCase("on")){
									inventoryManager.togglePlayer(player.getName(), true);
									Methods.sendLang(player, "outFcToggleOn");
								}else if (args[1].equalsIgnoreCase("off")){
									inventoryManager.togglePlayer(player.getName(), false);
									Methods.sendLang(player, "outFcToggleOff");
								}else if (args[1].equalsIgnoreCase("toggle")){
									boolean newVal = inventoryManager.togglePlayer(player.getName());
									if (newVal){
										Methods.sendLang(player, "outFcToggleOn");
									}else{
										Methods.sendLang(player, "outFcToggleOff");
									}
								}else{
									Methods.sendLang(sender, "cmdFcToggle");
								}
							}else{
								Methods.sendLang(sender, "errCmdPlayerOnly");
							}
						}else if (args.length == 3){
							if (!Permissions.playerHas(sender, "fastcraft.toggle.other")){
								Methods.sendLang(sender, "errNoPerm", "fastcraft.toggle.other");
							}else{
								Player player = Methods.getPlayer(args[2]);
								if (player != null){
									if (args[1].equalsIgnoreCase("on")){
										inventoryManager.togglePlayer(player.getName(), true);
										Methods.sendLang(player, "outFcToggleOn");
										Methods.sendLang(sender, "outFcToggleOnOther", player.getName());
									}else if (args[1].equalsIgnoreCase("off")){
										inventoryManager.togglePlayer(player.getName(), false);
										Methods.sendLang(player, "outFcToggleOff");
										Methods.sendLang(sender, "outFcToggleOffOther", player.getName());
									}else if (args[1].equalsIgnoreCase("toggle")){
										boolean newVal = inventoryManager.togglePlayer(player.getName());
										if (newVal){
											Methods.sendLang(sender, "outFcToggleOnOther", player.getName());
											Methods.sendLang(player, "outFcToggleOn");
										}else{
											Methods.sendLang(sender, "outFcToggleOffOther", player.getName());
											Methods.sendLang(player, "outFcToggleOff");
										}
									}else{
										Methods.sendLang(sender, "cmdFcToggle");
									}
								}else{
									Methods.sendLang(sender, "errPlayerNotFound", args[2]);
									Methods.sendLang(sender, "cmdFcToggle");
								}
							}
						}else if (args.length > 3){
							Methods.sendLang(sender, "fcToggle");
						}
					}
				}else{
					Methods.sendLang(sender, "cmdFc");
				}
			}
		}
		return true;
	}
}
