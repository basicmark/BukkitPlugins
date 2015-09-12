package co.kepler.fastcraft.recipe;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import co.kepler.fastcraft.FastCraft;

/**
 * An ingredient to a recipe.
 * 
 * @author Kepler_
 */
public class Ingredient {
	private MaterialData material;
	private int amount;
	private boolean hasLore;

	public Ingredient(MaterialData material, int amount) {
		this.material = material;
		this.amount = amount;
		this.hasLore = false;
	}

	public Ingredient(MaterialData material) {
		this(material, 1);
	}

	public Ingredient(ItemStack item, int amount) {
		this(item.getData(), amount);
		if (item.hasItemMeta()) {
			hasLore = item.getItemMeta().hasLore();
		}
	}

	public Ingredient(ItemStack item) {
		this(item.getData(), 1);
		if (item.hasItemMeta()) {
			hasLore = item.getItemMeta().hasLore();
		}
	}

	public Ingredient(Ingredient ingredient, int amount) {
		this(ingredient.material, amount);
	}

	public Ingredient(Ingredient ingredient) {
		this(ingredient, ingredient.amount);
	}

	@SuppressWarnings("deprecation")
	public boolean hasDataWildcard() {
		return material.getData() == -1;
	}

	public MaterialData getMaterial() {
		return material;
	}

	public void setMaterial(MaterialData material) {
		this.material = material;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(int amount) {
		return new ItemStack(material.getItemType(), amount, material.getData());
	}

	@SuppressWarnings("deprecation")
	public boolean isSimilar(Ingredient i) {
		if (hasLore) {
			return false;
		}
		if (material.getItemType() != i.material.getItemType()) {
			return false;
		}
		if (material.getData() != i.material.getData() && !(hasDataWildcard() || i.hasDataWildcard())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Ingredient) {
			Ingredient i = (Ingredient) o;
			if (!isSimilar(i)) {
				return false;
			}
			if (amount != i.amount) {
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public String getName() {
		if (material == null) {
			return null;
		}
		String result = FastCraft.configs().items.getItemName(material);
		if (result == null) {
			result = material.getItemType().toString();
			if (material.getData() != -1 && material.getData() != 0) {
				result += " (" + material.getData() + ")";
			}
			result = WordUtils.capitalizeFully(result.replace("_", " "));
		}
		return result;
	}
}
