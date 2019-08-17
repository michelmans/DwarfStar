package me.alchemi.dwarfstar.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import me.alchemi.al.Library;
import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.dwarfstar.Config;
import me.alchemi.dwarfstar.Config.ConfigEnum;
import me.alchemi.dwarfstar.Config.Messages;
import me.alchemi.dwarfstar.Config.Options;
import me.alchemi.dwarfstar.Star;

public class SmeltRecipe {

	private List<Material> input = new ArrayList<Material>();
	private List<ItemStack> output = new ArrayList<ItemStack>();
	private final float xp;
	
	private String key;
	
	private boolean edited = false;
	
	public SmeltRecipe(MaterialChoice input, float xp, boolean register, String key, ItemStack... output) {
		this.input = input.getChoices();
		
		for (ItemStack item : output) {
			this.output.add(item);
		}
		
		this.xp = xp;
		
		this.key = key;
		
		if (register) Config.addRecipe(this);
		edited = true;
	}
	
	public static SmeltRecipe load(String key) {
		
		try {
			return load(SexyConfiguration.loadConfiguration(new File(Star.RECIPES_FOLDER, key + ".yml")));
		} catch (Exception e) {
			throw new IllegalArgumentException(Messenger.formatString(Messages.COMMANDS_RECIPE_NO_RECIPE_NAME.value()
					.replace("$name$", key)));
		}
		
	}
	
	public static SmeltRecipe load(SexyConfiguration config) throws IllegalStateException { return load(config, true); }
	
	@SuppressWarnings("unchecked")
	public static SmeltRecipe load(SexyConfiguration config, boolean register) throws IllegalStateException {
		List<Material> in = new ArrayList<Material>();
		List<ItemStack> out = new ArrayList<ItemStack>();
		
		if (config.getList("input") != null) {
			for (String string : config.getStringList("input")) {
				in.add(MaterialWrapper.valueOf(string.toUpperCase()).getMaterial());
			}
		} else in.add(MaterialWrapper.valueOf(config.getString("input", "AIR").toUpperCase()).getMaterial());
		
		if (config.getList("output") != null) {
			 out = (List<ItemStack>) config.getList("output", new ArrayList<ItemStack>());
		} else out.add(config.getItemStack("output", new ItemStack(MaterialWrapper.AIR.getMaterial())));
		
		in.removeIf(ITEM -> ITEM == null || ITEM == MaterialWrapper.AIR.getMaterial());
		out.removeIf(ITEM -> ITEM == null || MaterialWrapper.getFromItemStack(ITEM) == MaterialWrapper.AIR.getMaterial());
		
		if (in.isEmpty() || out.isEmpty()) throw new IllegalStateException("The input or output cannot be empty. File = " + config.getFile().getName());
		
		float xp = (float) config.getDouble("xp", 0.0);
		
		String key = config.getFile().getName().replace(".yml", "");
		
		SmeltRecipe returnR = new SmeltRecipe(new MaterialChoice(in), xp, false, key, out.toArray(new ItemStack[out.size()]));
		
		if (register) {
			Config.addRecipe(returnR);
		}
		return returnR;	
	}
	
	public SmeltRecipe unload() {
		
		Config.removeRecipe(this);
		ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + getKey(), false);
		return this;
		
	}
	
	public void save() {
		try {
			SexyConfiguration config = SexyConfiguration.loadConfiguration(new File(Star.RECIPES_FOLDER, getKey() + ".yml"));
			
			if (input.size() > 1) {
				List<String> strings = input.stream().map(Material::name).collect(Collectors.toList());
				config.set("input", strings);
			} else config.set("input", input.get(0).getKey().getKey());
			
			if (output.size() > 1) config.set("output", output);
			else if (output.size() == 1) config.set("output", output.get(0));
			
			config.set("xp", xp);
			config.save();
			
			ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + this.key, true);
			ConfigEnum.CONFIG.getConfig().save();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SmeltRecipe modify() {
		Config.removeRecipe(this);
		return this;
	}
	
	public SmeltRecipe delete() {
		
		unload();
		ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + getKey(), null);
		File file = new File(Star.RECIPES_FOLDER, getKey() + ".yml");
		if (file.exists()) file.delete();
		
		return this;
		
	}
	
	public void smeltPlayerless(Block block, ItemStack tool) {

		for (Material type : input) {
			if (type == block.getType()) {
				
				int fortune = 1;
				
				if (Options.AUTOSMELT_APPLY_FORTUNE.asBoolean()
						&& tool.containsEnchantment(EnchantmentWrapper.LOOT_BONUS_BLOCKS)) {
					
					int level = tool.getEnchantmentLevel(EnchantmentWrapper.LOOT_BONUS_BLOCKS);
					double chance = 1.0/(level + 2) * 10000;

					int nextInt = new Random().nextInt(10000);
					
					for (int l = 1; l <= level + 1; l++) {
						if (nextInt <= chance * l) {
							fortune = l + 1;
							break;
						}
					}
				}
				
				for (ItemStack item : output) {
					ItemStack cloner = item.clone();
					cloner.setAmount(item.getAmount() * fortune);
					block.getWorld().dropItemNaturally(block.getLocation(), cloner);;
				}
				dropExp(block.getLocation());
				effects(block.getLocation());
			}
		}
		
	}
	
	public void effects(Location location) {
		if (Options.AUTOSMELT_PARTICLES.asBoolean()) location.getWorld().spawnParticle(Particle.FLAME, location.add(0.5, 0.5, 0.5), 10, 0.4, 0.4, 0.4, 0.01);
		if (Options.AUTOSMELT_SOUND_EFFECT.asBoolean()) location.getWorld().playEffect(location, Effect.EXTINGUISH, 0);
	}
	
	public void dropExp(Location location) {
		if (xp == 0.0) return; 
		
		ExperienceOrb orb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
		orb.setExperience(Math.round(xp));
	}
	
	public void smelt(Player player) { smelt(player, 1); }
	
	public void smelt(Player player, int smeltXTimes) {
		PlayerInventory inv = player.getInventory();
		
		boolean success = false;
		
		for (Material type : input) {
			if (inv.contains(type, smeltXTimes)) {

				int toSmelt = smeltXTimes;
				for (ItemStack stack : inv.getContents()) {
					
					if (toSmelt == 0) break;
					
					else if (stack == null) continue;
					
					else if (stack.getType() == type) {
						int index = inv.first(stack);
						ItemStack newStack = stack.clone();
						newStack.setAmount(stack.getAmount() - toSmelt);
						inv.setItem(index, newStack);
						toSmelt = 0;
						player.updateInventory();
					}
				}
				
				for (ItemStack item : output) {
					ItemStack cloner = item.clone();
					cloner.setAmount(item.getAmount() * smeltXTimes);
					Library.giveItemStack(cloner, player);
				}
				success = true;
				break;
			}
		}
		
		if (!success) {
			player.sendMessage(Messenger.formatString(Messages.NO_MELTING.value()));
		} else {
			player.giveExp((int) (this.xp * smeltXTimes));
			player.sendMessage(Messenger.formatString(Messages.MOLTEN.value()));
		}
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
		edited = true;
	}
	
	/**
	 * @return the output
	 */
	public List<ItemStack> getOutput() {
		return output;
	}
	
	/**
	 * Sets the output
	 * 
	 * @param output
	 */
	public void setOutput(List<ItemStack> output) {
		if (!this.output.containsAll(output)) {
			this.output = output;
			edited = true;
		}
	}

	/**
	 * 
	 * @return the xp
	 */
	public float getXp() {
		return xp;
	}

	/**
	 * @return the inputs
	 */
	public List<Material> getInput() {
		return input;
	}

	/**
	 * @param input the inputs to set
	 */
	public void setInput(List<Material> input) {
		if (!this.input.containsAll(input)) {
			this.input = input;
			this.edited = true;
			Config.removeRecipe(this);
			Config.addRecipe(this);
		}
	}

	/**
	 * @return the edited
	 */
	public boolean isEdited() {
		return edited;
	}

	/**
	 * @param edited the edited to set
	 */
	public void setEdited(boolean edited) {
		this.edited = edited;
	}	
}
