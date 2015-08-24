package co.kepler.fastcraft.recipe;

import java.util.HashSet;

import net.minecraft.server.v1_6_R1.CraftingManager;
import net.minecraft.server.v1_6_R1.IRecipe;
import net.minecraft.server.v1_6_R1.RecipeArmorDye;
import net.minecraft.server.v1_6_R1.RecipeFireworks;
import net.minecraft.server.v1_6_R1.RecipeMapClone;
import net.minecraft.server.v1_6_R1.RecipeMapExtend;
import net.minecraft.server.v1_6_R1.ShapedRecipes;
import net.minecraft.server.v1_6_R1.ShapelessRecipes;

public class RecipeUtil_v1_6_R1 extends RecipeUtil {

	public RecipeUtil_v1_6_R1() {
		badHashes = new HashSet<String>();
		for (Object o : CraftingManager.getInstance().getRecipes()) {
			IRecipe r = (IRecipe) o;
			if ((r instanceof ShapedRecipes || r instanceof ShapelessRecipes) && (
					(r instanceof RecipeArmorDye) ||
					(r instanceof RecipeMapClone) ||
					(r instanceof RecipeMapExtend) ||
					(r instanceof RecipeFireworks))) {
				badHashes.add(new FastRecipe(r.toBukkitRecipe()).getHash());
			}
		}
	}
}
