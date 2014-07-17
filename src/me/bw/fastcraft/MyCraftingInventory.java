package me.bw.fastcraft;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class MyCraftingInventory implements CraftingInventory {
	private Inventory i;
	private Recipe recipe;
	public MyCraftingInventory(Inventory i, Recipe r){
		this.i = i;
		recipe = r;
	}

	@Override public HashMap<Integer, ItemStack> addItem(ItemStack... arg0) throws IllegalArgumentException
	{ return i.addItem(arg0); }

	@Override public HashMap<Integer, ? extends ItemStack> all(int arg0)
	{ return i.all(arg0); }
	@Override public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException
	{ return i.all(arg0); }
	@Override public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0)
	{ return i.all(arg0); }

	@Override public void clear()
	{ i.clear(); }
	@Override public void clear(int arg0)
	{ i.clear(arg0); }

	@Override public boolean contains(int arg0)
	{ return i.contains(arg0); }
	@Override public boolean contains(Material arg0) throws IllegalArgumentException
	{ return i.contains(arg0); }
	@Override public boolean contains(ItemStack arg0)
	{ return i.contains(arg0); }
	@Override public boolean contains(int arg0, int arg1)
	{ return i.contains(arg0, arg1); }
	@Override public boolean contains(Material arg0, int arg1) throws IllegalArgumentException
	{return i.contains(arg0, arg1); }
	@Override public boolean contains(ItemStack arg0, int arg1)
	{ return i.contains(arg0, arg1); }

	@Override public boolean containsAtLeast(ItemStack arg0, int arg1)
	{ return i.containsAtLeast(arg0, arg1); }

	@Override
	public int first(int arg0)
	{ return i.first(arg0); }
	@Override public int first(Material arg0) throws IllegalArgumentException
	{ return i.first(arg0); }
	@Override public int first(ItemStack arg0)
	{ return i.first(arg0); }

	@Override public int firstEmpty()
	{ return i.firstEmpty(); }

	@Override public ItemStack[] getContents()
	{ return i.getContents(); }

	@Override public InventoryHolder getHolder()
	{ return i.getHolder(); }

	@Override public ItemStack getItem(int arg0)
	{ return i.getItem(arg0); }

	@Override public int getMaxStackSize()
	{ return i.getMaxStackSize(); }

	@Override public String getName()
	{ return i.getName(); }

	@Override public int getSize()
	{ return 10; }

	@Override public String getTitle()
	{ return i.getTitle(); }

	@Override public InventoryType getType()
	{ return i.getType(); }

	@Override public List<HumanEntity> getViewers()
	{ return i.getViewers(); }

	@Override public ListIterator<ItemStack> iterator()
	{ return i.iterator(); }
	@Override public ListIterator<ItemStack> iterator(int arg0)
	{ return i.iterator(arg0); }

	@Override public void remove(int arg0)
	{ i.remove(arg0); }
	@Override public void remove(Material arg0) throws IllegalArgumentException
	{ i.remove(arg0); }
	@Override public void remove(ItemStack arg0)
	{ i.remove(arg0); }

	@Override public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException
	{ return i.removeItem(arg0); }

	@Override public void setContents(ItemStack[] arg0) throws IllegalArgumentException
	{ i.setContents(arg0); }

	@Override public void setItem(int arg0, ItemStack arg1)
	{ i.setItem(arg0, arg1); }

	@Override public void setMaxStackSize(int arg0)
	{ i.setMaxStackSize(arg0); }

	@Override public ItemStack[] getMatrix(){
		ItemStack[] result = new ItemStack[9];
		for (int i = 0; i < 9; i++)
			result[i] = getItem(i + 1);
		return result;
	}

	@Override public Recipe getRecipe()
	{ return recipe; }

	@Override public ItemStack getResult()
	{ return i.getItem(0); }

	@Override public void setMatrix(ItemStack[] arg0){
		for (int i = 0; i < 9; i++)
			setItem(i + 1, arg0[i]);
	}

	@Override public void setResult(ItemStack arg0)
	{ i.setItem(0, arg0);	}
}
