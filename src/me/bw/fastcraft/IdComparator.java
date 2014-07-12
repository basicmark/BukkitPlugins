package me.bw.fastcraft;

import java.util.Comparator;


public class IdComparator implements Comparator<FastRecipe> {
	@Override
	public int compare(FastRecipe is0, FastRecipe is1) {
		if (is0.getResult().getTypeId() == is1.getResult().getTypeId()){
			return is0.getResult().getDurability() - is1.getResult().getDurability();
		}
		return is0.getResult().getTypeId() - is1.getResult().getTypeId();
	}
}
