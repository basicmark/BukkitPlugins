package me.bw.fastcraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import me.bw.fastcraft.util.PlayerUtil;
import me.bw.fastcraft.util.Util;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FastCraft extends JavaPlugin {
	public static String pluginDir = "plugins/FastCraft/";
	public static File configFile = new File(pluginDir, "config.yml");
	public static File customLangConfigFile = new File(pluginDir, "language.yml");
	public static File ingredientsConfigFile = new File(pluginDir, "ingredients.yml");
	public static File playerPrefsConfigFile = new File(pluginDir, "playerPrefs.yml");
	public static File permsConfigFile = new File(pluginDir, "permissions.yml");
	public static YamlConfiguration config;
	public static YamlConfiguration customLangConfig;
	public static YamlConfiguration langConfig;
	public static YamlConfiguration ingredientsConfig;
	public static YamlConfiguration playerPrefsConfig;
	public static YamlConfiguration permsConfig;
	public static JavaPlugin plugin;
	public static int version;

	public static HashMap<Material, Achievement> achievements = new HashMap<Material, Achievement>();
	
	public static InventoryManager inventoryManager;

	public void onEnable(){
		plugin = this;
		
		inventoryManager = new InventoryManager();

		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(inventoryManager, this);
		manager.registerEvents(new EventListener(), this);

		Permissions.setupPermissions();
		version = Integer.parseInt(this.getDescription().getVersion());
		
		setupAchievements();
		
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

		if (!customLangConfigFile.exists())
			this.saveResource("language.yml", false);
		if (!ingredientsConfigFile.exists())
			this.saveResource("ingredients.yml", false);
		if (!playerPrefsConfigFile.exists())
			this.saveResource("playerPrefs.yml", false);
		if (!permsConfigFile.exists())
			this.saveResource("permissions.yml", false);
		customLangConfig = YamlConfiguration.loadConfiguration(customLangConfigFile);
		ingredientsConfig = YamlConfiguration.loadConfiguration(ingredientsConfigFile);
		playerPrefsConfig = YamlConfiguration.loadConfiguration(playerPrefsConfigFile);
		permsConfig = YamlConfiguration.loadConfiguration(permsConfigFile);
		
		
		InputStream languageResource = getResource("languages/" + config.getString("language").toUpperCase() + ".yml");
		if (languageResource == null)
			config.set("language", "EN");
		langConfig = Util.configFromResource("languages/" + config.getString("language").toUpperCase() + ".yml");
		
		Settings.load();
		
		for (Player p : Bukkit.getOnlinePlayers())
			PluginUpdater.notifyOfUpdateIfNeeded(p);
	}
	public static void setupAchievements(){
		addAchievement(Material.STONE_PICKAXE, "BUILD_BETTER_PICKAXE");
		addAchievement(Material.FURNACE, "BUILD_FURNACE");
		addAchievement(Material.WOOD_HOE, "BUILD_HOE");
		addAchievement(Material.WOOD_PICKAXE, "BUILD_PICKAXE");
		addAchievement(Material.WOOD_SWORD, "BUILD_SWORD");
		addAchievement(Material.WORKBENCH, "BUILD_WORKBENCH");
	}
	public static void addAchievement(Material m, String achievement){
		try{
			achievements.put(m, Achievement.valueOf(achievement));
		}catch(Exception e){}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equals("fastcraft")){
			if (!Permissions.playerHas(sender, "fastcraft.use")){
				Util.sendLang(sender, "errNoPerm", "fastcraft.use");
				return true;
			}
			if (args.length == 0){
				Util.sendLang(sender, "outFc");
			}else{
				if (args[0].equalsIgnoreCase("admin")){
					if (!Permissions.playerHas(sender, "fastcraft.admin")){
						Util.sendLang(sender, "errNoPerm", "fastcraft.admin");
						return true;
					}
					if (args.length == 1){
						Util.sendLang(sender, "outFcAdmin");
					}else if (args[1].equalsIgnoreCase("reload")){
						if (!Permissions.playerHas(sender, "fastcraft.admin.reload")){
							Util.sendLang(sender, "errNoPerm", "fastcraft.admin.reload");
							return true;
						}
						if (args.length == 2){
							reload();
							Util.sendLang(sender, "outFcAdminReload");
						}else{
							Util.sendLang(sender, "cmdFcAdminReload");
						}
					}else if (args[1].equalsIgnoreCase("update")){
						if (!Permissions.playerHas(sender, "fastcraft.admin.update")){
							Util.sendLang(sender, "errNoPerm", "fastcraft.admin.update");
							return true;
						}
						if (args.length == 2){
							PluginUpdater.update(sender);
						}else{
							Util.sendLang(sender, "cmdFcAdminUpdate");
						}
					}else{
						Util.sendLang(sender, "cmdFcAdmin");
					}
				}else if (args[0].equalsIgnoreCase("toggle")){
					if (!Permissions.playerHas(sender, "fastcraft.toggle")){
						Util.sendLang(sender, "errNoPerm", "fastcraft.toggle");
					}else{
						if (args.length == 1){
							if (sender instanceof Player){
								Player player = (Player)sender;
								boolean newVal = inventoryManager.togglePlayer(PlayerUtil.getIdentifier(player));
								if (newVal){
									Util.sendLang(player, "outFcToggleOn");
								}else{
									Util.sendLang(player, "outFcToggleOff");
								}
							}else{
								Util.sendLang(sender, "cmdFcToggle");
							}	
						}else if (args.length == 2){
							if (sender instanceof Player){
								Player player = (Player)sender;
								String identifier = PlayerUtil.getIdentifier(player);
								if (args[1].equalsIgnoreCase("on")){
									inventoryManager.togglePlayer(identifier, true);
									Util.sendLang(player, "outFcToggleOn");
								}else if (args[1].equalsIgnoreCase("off")){
									inventoryManager.togglePlayer(identifier, false);
									Util.sendLang(player, "outFcToggleOff");
								}else if (args[1].equalsIgnoreCase("toggle")){
									boolean newVal = inventoryManager.togglePlayer(identifier);
									if (newVal){
										Util.sendLang(player, "outFcToggleOn");
									}else{
										Util.sendLang(player, "outFcToggleOff");
									}
								}else{
									Util.sendLang(sender, "cmdFcToggle");
								}
							}else{
								Util.sendLang(sender, "errCmdPlayerOnly");
							}
						}else if (args.length == 3){
							if (!Permissions.playerHas(sender, "fastcraft.toggle.other")){
								Util.sendLang(sender, "errNoPerm", "fastcraft.toggle.other");
							}else{
								Player player = PlayerUtil.getOnlinePlayer(args[2]);
								if (player != null){
									String identifier = PlayerUtil.getIdentifier(player);
									if (args[1].equalsIgnoreCase("on")){
										inventoryManager.togglePlayer(identifier, true);
										Util.sendLang(player, "outFcToggleOn");
										Util.sendLang(sender, "outFcToggleOnOther", identifier);
									}else if (args[1].equalsIgnoreCase("off")){
										inventoryManager.togglePlayer(identifier, false);
										Util.sendLang(player, "outFcToggleOff");
										Util.sendLang(sender, "outFcToggleOffOther", identifier);
									}else if (args[1].equalsIgnoreCase("toggle")){
										boolean newVal = inventoryManager.togglePlayer(identifier);
										if (newVal){
											Util.sendLang(sender, "outFcToggleOnOther", identifier);
											Util.sendLang(player, "outFcToggleOn");
										}else{
											Util.sendLang(sender, "outFcToggleOffOther", identifier);
											Util.sendLang(player, "outFcToggleOff");
										}
									}else{
										Util.sendLang(sender, "cmdFcToggle");
									}
								}else{
									Util.sendLang(sender, "errPlayerNotFound", args[2]);
									Util.sendLang(sender, "cmdFcToggle");
								}
							}
						}else if (args.length > 3){
							Util.sendLang(sender, "fcToggle");
						}
					}
				}else{
					Util.sendLang(sender, "cmdFc");
				}
			}
		}
		return true;
	}
}
