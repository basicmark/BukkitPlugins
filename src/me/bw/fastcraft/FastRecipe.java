package me.bw.fastcraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class FastRecipe {
	private IngredientList ingredients;
	private ItemStack result;

	public FastRecipe(Recipe recipe){
		ingredients = new IngredientList();
		if (recipe instanceof ShapedRecipe){
			ShapedRecipe cur = (ShapedRecipe)recipe;
			for (ItemStack is : cur.getIngredientMap().values()){
				if (is != null && is.getType() != Material.AIR){
					ingredients.add(new Ingredient(is, 1));
				}
			}
			result = recipe.getResult();
		}else if (recipe instanceof ShapelessRecipe){
			ShapelessRecipe cur = (ShapelessRecipe)recipe;
			for (ItemStack i : cur.getIngredientList()){
				ingredients.add(new Ingredient(i, 1));
			}
			result = recipe.getResult();
		}
	}
	public IngredientList getIngredients(){
		return ingredients;
	}
	public static boolean isCraftRecipe(Recipe recipe){
		return (recipe instanceof ShapedRecipe) || (recipe instanceof ShapelessRecipe);
	}
	public boolean canCraft(IngredientList withItems){
		return withItems.containsAll(ingredients);
	}
	public ItemStack getResult(){
		return result;
	}
	public boolean equals(Object obj){
		if (!(obj instanceof FastRecipe)) return false;
		FastRecipe test = (FastRecipe)obj;
		if (!this.getIngredients().equals(test.getIngredients())) return false;
		if (!this.getResult().equals(test.getResult())) return false;
		return true;
	}
}
