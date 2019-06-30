package me.alchemi.dwarfstar.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

public class SmeltRecipeBuilder {

	private MaterialChoice input;
	private float xp = 0;
	private boolean register = true;
	private String key;
	private ItemStack[] output;
	
	
	//MaterialChoice input, float xp, boolean register, String key, ItemStack...output
	public SmeltRecipeBuilder() {}
	
	public static SmeltRecipeBuilder fromRecipe(SmeltRecipe in) {
		return new SmeltRecipeBuilder()
				.input(new MaterialChoice(in.getInput()))
				.xp(in.getXp())
				.key(in.getKey())
				.output(in.getOutput());
	}
	
	public SmeltRecipe create() {
		return new SmeltRecipe(input, xp, register, key, output);
	}
	
	public SmeltRecipeBuilder input(MaterialChoice input) {
		this.input = input;
		this.key = input.getChoices().get(0).getKey().getKey();
		return this;
	}
	
	public SmeltRecipeBuilder xp(float xp) {
		this.xp = xp;
		return this;
	}
	
	public SmeltRecipeBuilder register(boolean register) {
		this.register = register;
		return this;
	}
	
	public SmeltRecipeBuilder key(String key) {
		this.key = key;
		return this;
	}
	
	public SmeltRecipeBuilder output(ItemStack...output) {
		this.output = output;
		return this;
	}
	
	public SmeltRecipeBuilder output(List<ItemStack> output) {
		this.output = output.toArray(new ItemStack[output.size()]);
		return this;
	}
	
	public SmeltRecipeBuilder output(ItemStack output) {
		this.output = new ItemStack[] {output};
		return this;
	}

	public final MaterialChoice getInput() {
		return input;
	}

	public final float getXp() {
		return xp;
	}

	public final boolean isRegister() {
		return register;
	}

	public final String getKey() {
		return key;
	}

	public final ItemStack[] getOutput() {
		return output != null ? output : new ItemStack[]{};
	}
	
	
}
