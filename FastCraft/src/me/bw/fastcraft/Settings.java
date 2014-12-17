package me.bw.fastcraft;

import java.util.ArrayList;
import java.util.List;

import me.bw.fastcraft.util.Util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Settings {
	public static int craftItemRows = 5;
	
	public static String invTitle;
	public static String getInvTitle(){
		return invTitle;
	}
	
	public static ItemStack invButtonPrevItem;
	public static ItemStack invButtonNextItem;
	public static ItemStack invButtonHelpItem;
	public static ItemStack invButtonCraftItem;
	public static ItemStack invButtonRefreshItem;
	
	public static ItemStack getInvButtonPrevItem(int prevPage, int totalPages){
		ItemStack result = invButtonPrevItem.clone();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(Util.getLang("buttonPrevName", prevPage+"", totalPages+""));
		im.setLore(makeLore(Util.getLang("buttonPrevLore", prevPage+"", totalPages+"")));
		result.setItemMeta(im);
		return result;
	}
	public static ItemStack getInvButtonNextItem(int nextPage, int totalPages){
		ItemStack result = invButtonNextItem.clone();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(Util.getLang("buttonNextName", nextPage+"", totalPages+""));
		im.setLore(makeLore(Util.getLang("buttonNextLore", nextPage+"", totalPages+"")));
		result.setItemMeta(im);
		return result;
	}
	public static ItemStack getInvButtonHelpItem(){
		ItemStack result = invButtonHelpItem.clone();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(Util.getLang("buttonHelpName"));
		im.setLore(makeLore(Util.getLang("buttonHelpLore")));
		result.setItemMeta(im);
		return result;
	}
	public static ItemStack getInvButtonCraftItem(){
		ItemStack result = invButtonCraftItem.clone();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(Util.getLang("buttonCraftName"));
		im.setLore(makeLore(Util.getLang("buttonCraftLore")));
		result.setItemMeta(im);
		return result;
	}
	public static ItemStack getInvButtonRefreshItem(){
		ItemStack result = invButtonRefreshItem.clone();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(Util.getLang("buttonRefreshName"));
		im.setLore(makeLore(Util.getLang("buttonRefreshLore")));
		result.setItemMeta(im);
		return result;
	}
	
	
	public static int invButtonPrevSlot = 45;
	public static int invButtonNextSlot = 53;
	public static int invButtonHelpSlot = 48;
	public static int invButtonCraftSlot = 49;
	public static int invButtonRefreshSlot = 50;
	
	public static List<Ingredient> disabledRecipes;
	public static List<Ingredient> disabledIngredients;

	public static boolean fastCraftDefaultEnabled;
	
	public static void load(){
		invTitle = Util.getLang("invTitle");
		
		invButtonPrevItem = Util.parseItem(FastCraft.config.getString("buttonPrevItem")).toItemStack(1);
		invButtonNextItem = Util.parseItem(FastCraft.config.getString("buttonNextItem")).toItemStack(1);
		invButtonHelpItem = Util.parseItem(FastCraft.config.getString("buttonHelpItem")).toItemStack(1);
		invButtonCraftItem = Util.parseItem(FastCraft.config.getString("buttonCraftItem")).toItemStack(1);
		invButtonRefreshItem = Util.parseItem(FastCraft.config.getString("buttonRefreshItem")).toItemStack(1);
		
		fastCraftDefaultEnabled = FastCraft.config.getBoolean("fastCraftDefaultEnabled");
		
		disabledRecipes = new ArrayList<Ingredient>();
		for (String s : FastCraft.config.getStringList("disabledRecipes"))
			disabledRecipes.add(new Ingredient(Util.parseItem(s)));
		disabledIngredients = new ArrayList<Ingredient>();
		for (String s : FastCraft.config.getStringList("disabledIngredients"))
			disabledIngredients.add(new Ingredient(Util.parseItem(s)));
	}
	
	private static List<String> makeLore(String lines){
		List<String> result = new ArrayList<String>();
		for (String s : lines.split("\n")) result.add(s);
		return result;
	}
	
	
}
