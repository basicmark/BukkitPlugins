package me.bw.fastcraft.api;

import me.bw.fastcraft.FastCraft;
import me.bw.fastcraft.FastCraftInv;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FastCraftApi {
	public static boolean isFastCraftInventory(Inventory inventory){
		for (FastCraftInv i : FastCraft.inventoryManager.inventories.values()){
			Inventory check = i.getCurrentInv();
			if (!inventory.getHolder().equals(check.getHolder())) continue;
			if (!inventory.getName().equals(check.getName())) continue;
			if (inventory.getSize() != check.getSize()) continue;
			if (!inventory.getTitle().equals(check.getTitle())) continue;
			if (!inventory.getType().equals(check.getType())) continue;
			if (inventory.getSize() != check.getSize()) continue;
			if (inventory.getViewers().size() != check.getViewers().size()) continue;
			boolean equal = true;
			for (int index = 0; index < inventory.getSize() && equal; index++){
				ItemStack a = inventory.getItem(index);
				ItemStack b = check.getItem(index);
				if (!((a == null && b == null) || a.equals(b))) equal = false;
			}
			for (int index = 0; index < inventory.getViewers().size() && equal; index++){
				HumanEntity a = inventory.getViewers().get(index);
				HumanEntity b = check.getViewers().get(index);
				if (!((a == null && b == null) || a.equals(b))) equal = false;
			}
			if (!equal) continue;
			return true;
		}
		return false;
	}
	public static boolean compareInvs(Inventory inv0, Inventory inv1){
		if (!inv0.getHolder().equals(inv1.getHolder())) return false;
		if (!inv0.getName().equals(inv1.getName())) return false;
		if (inv0.getSize() != inv1.getSize()) return false;
		if (!inv0.getTitle().equals(inv1.getTitle())) return false;
		if (!inv0.getType().equals(inv1.getType())) return false;
		if (inv0.getSize() != inv1.getSize()) return false;
		if (inv0.getViewers().size() != inv1.getViewers().size()) return false;
		for (int index = 0; index < inv0.getSize(); index++){
			ItemStack a = inv0.getItem(index);
			ItemStack b = inv1.getItem(index);
			if (!((a == null && b == null) || a.equals(b))) return false;
		}
		for (int index = 0; index < inv0.getViewers().size(); index++){
			HumanEntity a = inv0.getViewers().get(index);
			HumanEntity b = inv1.getViewers().get(index);
			if (!((a == null && b == null) || a.equals(b))) return false;
		}
		return true;
	}
}
