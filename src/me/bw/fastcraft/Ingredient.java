package me.bw.fastcraft;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Ingredient {
	private MaterialData material;
	private int amount;

	public Ingredient(MaterialData material, int amount){
		this.material = material;
		this.amount = amount;
	}
	public Ingredient(MaterialData material){
		this(material, 1);
	}
	public Ingredient(ItemStack item, int amount){
		this(item.getData(), amount);
	}
	public Ingredient(ItemStack item){
		this(item.getData(), 1);
	}
	public Ingredient(Ingredient ingredient, int amount){
		this(ingredient.material, amount);
	}
	public Ingredient(Ingredient ingredient){
		this(ingredient, ingredient.amount);
	}

	public boolean hasDataWildcard(){
		return material.getData() == -1;
	}

	public MaterialData getMaterial(){
		return material;
	}
	public void setMaterial(MaterialData material){
		this.material = material;
	}
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = amount;
	}

	public boolean isSimilar(Ingredient i){
		if (material.getItemType() != i.material.getItemType()) return false;
		if (material.getData() != i.material.getData() && !(hasDataWildcard() || i.hasDataWildcard())) return false;
		return true;
	}
	public boolean equals(Object o){
		if (o instanceof Ingredient){
			Ingredient i = (Ingredient)o;
			if (!isSimilar(i)) return false;
			if (amount != i.amount) return false;
			return true;
		}
		return false;
	}
	public String getName(){
		if (material == null) return null;
		String key = material.getItemTypeId() + "-" + ((material.getData() == -1)?"*":material.getData());
		String result = FastCraft.ingredientsConfig.getString(key);
		if (result == null){
			result = material.getItemType().toString();
			if (material.getData() != -1 && material.getData() != 0) result += " (" + material.getData() + ")";
			result = WordUtils.capitalizeFully(result.replace("_", " "));
		}
		return result;
	}
}
