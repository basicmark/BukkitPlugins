package me.bw.fastcraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

public class FastCraftInv {
	private IngredientList ingredients;
	private List<FastRecipe> craftableItems;
	private int page;
	private Inventory curInv;
	
	public FastCraftInv(){
		ingredients = new IngredientList();
		craftableItems = new ArrayList<FastRecipe>();
		page = 0;
		curInv = null;
	}

	public void setPage(int newPage){
		page = Math.max(0, Math.min(newPage, getLastPage()));
	}
	public int getPage(){
		return page;
	}
	public int getLastPage(){
		int lastItem = craftableItems.size() - 1;
		while (lastItem > 0 && !craftableItems.get(lastItem).canCraft(ingredients)) lastItem--;
		return (int)Math.ceil(Math.max(0, lastItem) / (Settings.craftItemRows * 9));
	}
	public List<FastRecipe> getCraftableItems(){
		return craftableItems;
	}
	
	public void setIngredients(IngredientList i){
		ingredients = i;
	}
	public IngredientList getIngredients(){
		return ingredients;
	}
	private void updateCraftableItems(){
		List<FastRecipe> newRecipes = new ArrayList<FastRecipe>();
		Iterator<Recipe> serverRecipes = Bukkit.getServer().recipeIterator();
		while (serverRecipes.hasNext()){
			Recipe cur = serverRecipes.next();
			
			FastRecipe fr = new FastRecipe(cur);
			if (FastRecipe.isCraftRecipe(cur) && !craftableItems.contains(fr) && fr.canCraft(ingredients) && canAddRecipe(fr)){
				newRecipes.add(fr);
			}
		}
		Collections.sort(newRecipes, new IdComparator());
		craftableItems.addAll(newRecipes);
	}
	
	private boolean canAddRecipe(FastRecipe recipe){
		/*
		for (Ingredient i : Settings.disabledRecipes)
			if (i.isSimilar(new Ingredient(recipe.getResult()))) return false;
		
		for (Ingredient a : Settings.disabledIngredients)
			for (Ingredient b : recipe.getIngredients().getList())
				if (a.isSimilar(b)) return false;
		
		return true;
		*/
		
		if (Settings.disabledRecipes.contains(new Ingredient(recipe.getResult(), 1))) return false;
		for (Ingredient i : recipe.getIngredients().getList())
			if (Settings.disabledIngredients.contains(new Ingredient(i, 1))) return false;
		return true;
		
	}
	
	public void resetCraftableItems(){
		craftableItems.clear();
		updateCraftableItems();
	}

	public void updateInv(InventoryHolder holder){
		Inventory inv;
		int rows = Settings.craftItemRows * 9;
		inv = Bukkit.createInventory(holder, 54, Settings.getInvTitle());
		updateCraftableItems();
		
		if (page > 0) inv.setItem(Settings.invButtonPrevSlot, Settings.getInvButtonPrevItem(page, getLastPage() + 1));
		if (page < getLastPage()) inv.setItem(Settings.invButtonNextSlot, Settings.getInvButtonNextItem(page + 2, getLastPage() + 1));
		inv.setItem(Settings.invButtonHelpSlot, Settings.getInvButtonHelpItem());
		inv.setItem(Settings.invButtonCraftSlot, Settings.getInvButtonCraftItem());
		inv.setItem(Settings.invButtonRefreshSlot, Settings.getInvButtonRefreshItem());
		
		int startIndex = getPage() * rows;
		for(int i = startIndex; i < Math.min(startIndex + rows, craftableItems.size()); i++){
			if (craftableItems.get(i).canCraft(ingredients)){
				ItemStack item = new ItemStack(craftableItems.get(i).getResult());
				ItemMeta im = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				for (Ingredient curIng : craftableItems.get(i).getIngredients().getList()){
					lore.add(Methods.getLang("ingredientsFormat", curIng.getAmount()+"", curIng.getName()));
				}
				if (im.getLore() == null){
					im.setLore(lore);
				}else{
					im.getLore().add(" ");
					im.getLore().addAll(lore);
				}
				item.setItemMeta(im);
				inv.setItem(i - startIndex, item);
			}
		}
		curInv = inv;
	}
	public Inventory getCurrentInv(){
		return curInv;
	}
}


























