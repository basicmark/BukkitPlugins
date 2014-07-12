package me.bw.fastcraft;

import org.bukkit.Bukkit;
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
		final String name = event.getPlayer().getName();
		if (isRecipeCommand && FastCraft.inventoryManager.getPlayerToggle(name)){
			FastCraft.inventoryManager.togglePlayer(name, false);
			Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
				public void run() {
					FastCraft.inventoryManager.togglePlayer(name, true);
				}
			}, 1);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event){
		Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
			public void run() {
				PluginUpdater.notifyOfUpdateIfNeeded(event.getPlayer());
			}
		}, 20);
	}
}
