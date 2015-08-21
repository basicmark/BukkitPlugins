package co.kepler.fastcraft.recipe;

import java.util.HashSet;

import net.minecraft.server.v1_8_R1.CraftingManager;
import net.minecraft.server.v1_8_R1.IRecipe;
import net.minecraft.server.v1_8_R1.RecipeArmorDye;
import net.minecraft.server.v1_8_R1.RecipeBookClone;
import net.minecraft.server.v1_8_R1.RecipeFireworks;
import net.minecraft.server.v1_8_R1.RecipeMapClone;
import net.minecraft.server.v1_8_R1.RecipeMapExtend;
import net.minecraft.server.v1_8_R1.RecipeRepair;
import net.minecraft.server.v1_8_R1.RecipesBanner;
import net.minecraft.server.v1_8_R1.ShapedRecipes;
import net.minecraft.server.v1_8_R1.ShapelessRecipes;

public class RecipeUtil_v1_8_R1 extends RecipeUtil {

	public RecipeUtil_v1_8_R1() {
		badHashes = new HashSet<String>();
		for (Object o : CraftingManager.getInstance().getRecipes()) {
			IRecipe r = (IRecipe) o;
			if ((r instanceof ShapedRecipes || r instanceof ShapelessRecipes) && (
					(r instanceof RecipeArmorDye) ||
					(r instanceof RecipeBookClone) ||
					(r instanceof RecipeMapClone) ||
					(r instanceof RecipeMapExtend) ||
					(r instanceof RecipeFireworks) ||
					(r instanceof RecipeRepair) ||
					RecipesBanner.class.equals(r.getClass().getEnclosingClass()))) {
				badHashes.add(new FastRecipe(r.toBukkitRecipe()).getHash());
			}
		}
	}
}
