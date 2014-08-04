package me.bw.fastcraft;

import me.bw.fastcraft.util.PlayerUtil;
import me.bw.fastcraft.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
	@EventHandler
	public void onCommandProcess(PlayerCommandPreprocessEvent event){
		//Essentials Recipe command conflict
		String command = event.getMessage().split(" ")[0].substring(1);
		boolean isRecipeCommand = false;
		String[] recipeCommands = new String[]{"recipe", "formula", "eformula", "method", "emethod", "erecipe", "recipes", "erecipes"};
		for (String s : recipeCommands){
			if (s.equalsIgnoreCase(command)){
				isRecipeCommand = true;
				break;
			}
		}
		final String identifier = PlayerUtil.getIdentifier(event.getPlayer());
		if (isRecipeCommand && FastCraft.inventoryManager.getPlayerToggle(identifier)){
			FastCraft.inventoryManager.togglePlayer(identifier, false);
			Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
				public void run() {
					FastCraft.inventoryManager.togglePlayer(identifier, true);
				}
			}, 1);
		}
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event){
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();
		String name = player.getName();
		if (PlayerUtil.canUseUUID()){
			YamlConfiguration prefs = FastCraft.playerPrefsConfig;
			if (prefs.contains(name)){
				if (!prefs.contains(uuid))
					Util.copyConfigSection(prefs, name, uuid);
				prefs.set(name, null);
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
			public void run() {
				PluginUpdater.notifyOfUpdateIfNeeded(event.getPlayer());
			}
		}, 20);
	}
}
