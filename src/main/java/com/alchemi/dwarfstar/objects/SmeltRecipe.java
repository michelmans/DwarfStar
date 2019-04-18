package com.alchemi.dwarfstar.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.alchemi.al.Library;
import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.dwarfstar.Config;
import com.alchemi.dwarfstar.main;

public class SmeltRecipe {

	private List<Material> input = new ArrayList<Material>();
	private List<ItemStack> output = new ArrayList<ItemStack>();
	private final float xp;
	
	private String key;
	
	private boolean edited = false;
	
	public SmeltRecipe(Material input) {
		this.input.add(input);
		this.xp = 0;
		Config.addRecipe(this);
		edited = true;
	}
	
	public SmeltRecipe(Material input, float xp) {
		this.input.add(input);
		this.xp = xp;
		Config.addRecipe(this);
		edited = true;
	}
	
	public SmeltRecipe(Material input, ItemStack... output) {
		this.input.add(input);
		
		for (ItemStack item : output) {
			this.output.add(item);
		}
		
		this.xp = 0;
		
		Config.addRecipe(this);
		edited = true;
	}
	
	public SmeltRecipe(Material input, float xp, ItemStack... output) {
		this.input.add(input);
		
		for (ItemStack item : output) {
			this.output.add(item);
		}
		
		this.xp = xp;
		
		Config.addRecipe(this);
		edited = true;
	}
	
	public SmeltRecipe(Material input, float xp, boolean register, ItemStack... output) {
		this.input.add(input);
		
		for (ItemStack item : output) {
			this.output.add(item);
		}
		
		this.xp = xp;
		
		if (register) Config.addRecipe(this);
		edited = true;
	}
	
	public static SmeltRecipe load(SexyConfiguration config) { return load(config, true); }
	
	@SuppressWarnings("unchecked")
	public static SmeltRecipe load(SexyConfiguration config, boolean register) {
		List<Material> in = new ArrayList<Material>();
		List<ItemStack> out = new ArrayList<ItemStack>();
		
		if (config.getList("input") != null) {
			for (String string : config.getStringList("input")) {
				in.add(Material.getMaterial(string.toUpperCase()));
			}
		} else in.add(Material.getMaterial(config.getString("input", "AIR")));
		
		if (config.getList("output") != null) {
			 out = (List<ItemStack>) config.getList("output", new ArrayList<ItemStack>());
		} else out.add(config.getItemStack("output", new ItemStack(Material.AIR)));
		
		float xp = (float) config.getDouble("xp", 0.0);
		
		String key = config.getFile().getName().replace(".yml", "");
		
		SmeltRecipe returnR = new SmeltRecipe(in.get(0), xp, register, out.toArray(new ItemStack[out.size()]));
		if (!in.isEmpty()) returnR.setInput(in);
		
		returnR.setKey(key);
		return returnR;
		
	}
	
	public void smelt(Player player) { smelt(player, 1); }
	
	public void smelt(Player player, int smeltXTimes) {
		PlayerInventory inv = player.getInventory();
		
		boolean success = false;
		
		for (Material type : input) {
			if (inv.contains(type, smeltXTimes)) {
				inv.removeItem(new ItemStack(type, smeltXTimes));
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
			
		} else {
			player.giveExp((int) (this.xp * smeltXTimes));
		}
	}
	
	public void save() {
		try {
			SexyConfiguration config = new SexyConfiguration(new File(main.RECIPES_FOLDER, getKey() + ".yml"));
			
			if (input.size() > 1) {
				List<String> strings = input.stream().map(Material::name).collect(Collectors.toList());
				config.set("input", strings);
			} else config.set("input", input.get(0).name());
			
			if (output.size() > 1) config.set("output", output);
			else if (output.size() == 1) config.set("output", output.get(0));
			
			config.set("xp", xp);
			config.save();
			
		} catch (IOException e) {
			e.printStackTrace();
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
		if (this.input.containsAll(input)) {
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
