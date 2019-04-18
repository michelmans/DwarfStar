package com.alchemi.dwarfstar;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.dwarfstar.objects.SmeltRecipe;

public class Config {

	public static SexyConfiguration config;
	public static SexyConfiguration messages;
	
	public static Map<List<Material>, SmeltRecipe> recipes = new HashMap<List<Material>, SmeltRecipe>();
	
	public static enum MESSAGES {
		NO_PERMISSION("DwarfStar.Commands.NoPermission"),
		COMMANDS_WRONG_FORMAT("DwarfStar.Commands.WrongFormat"),
		COMMANDS_UNKNOWN("DwarfStar.Commands.Unknown");

		String value;
		String key;

		private MESSAGES(String key) {
			this.key = key;
		}

		public void get() {
			value = messages.getString(key, "PLACEHOLDER - STRING NOT FOUND");

		}
  
		public String value() { return value; } 
	
	}

	public static void enable() throws IOException, InvalidConfigurationException, IllegalArgumentException {
		
		config = SexyConfiguration.loadConfiguration(main.CONFIG_FILE);
		messages = SexyConfiguration.loadConfiguration(main.MESSAGES_FILE);
		
		for (SexyConfiguration file : new SexyConfiguration[] {config, messages}) {
			int version;
			if (file.equals(config)) version = main.CONFIG_FILE_VERSION; 
			else if (file.equals(messages)) version = main.MESSAGES_FILE_VERSION; 
			else version = 0;
				  
			if(!file.getFile().exists()) main.instance.saveResource(file.getFile().getName(), false);
			  
			if(!file.isSet("File-Version-Do-Not-Edit") ||
					!file.get("File-Version-Do-Not-Edit").equals(version)) {
				
				main.messenger.print("Your $file$ is outdated! Updating...".replace("$file$", file.getFile().getName())); 
				file.load(new InputStreamReader(main.instance.getResource(file.getFile().getName())));
				file.update(SexyConfiguration.loadConfiguration(new InputStreamReader(main.instance.getResource(file.getFile().getName()))));
				file.set("File-Version-Do-Not-Edit", version);
				file.save();
				main.messenger.print("File successfully updated!");
			} 
		}
		
		for (MESSAGES value : MESSAGES.values()) value.get();
		
		if (config.getConfigurationSection("enabledRecipes") != null) 
			for (String key : config.getConfigurationSection("enabledRecipes").getValues(false).keySet()) {
				if (config.getBoolean("enabledRecipes." + key)) {
					SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, key + ".yml")));
				}
			}
		
		if (!config.contains("enabledRecipes") || recipes.isEmpty()) {
			main.messenger.print("No recipes found! Generating...");
			generateRecipes();
		}
		
	}
	
	public static void reload() {
		config = SexyConfiguration.loadConfiguration(main.CONFIG_FILE);
		messages = SexyConfiguration.loadConfiguration(main.MESSAGES_FILE);
		
		for (MESSAGES value : MESSAGES.values()) value.get();
		
	}
	
	public static void save() {
		
		for (SmeltRecipe rec : recipes.values()) {
			if (!rec.isEdited()) continue;
			
			rec.save();
			
			if (!config.isSet("enabledRecipes." + rec.getKey())) {
				
				config.set("enabledRecipes." + rec.getKey(), true);
				
			}
			
			rec.setEdited(false);
		}
		
		if (config.isSet("enabledRecipes")) config.setComment("enabledRecipes", "# If these recipes are enabled or not. They all default to true.");
		
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String materialToString(Material mat) {
		
		return mat.getKey().getKey().replaceAll("_", " ").toLowerCase();
		
	}
	
	public static void addRecipe(SmeltRecipe toAdd) {
		
		recipes.put(toAdd.getInput(), toAdd);
		
	}
	
	public static boolean hasRecipe(Material mat) {
		return recipes.keySet().stream().anyMatch(Materials -> Materials.contains(mat));
	}
	
	public static void removeRecipe(SmeltRecipe toRemove) {
		
		recipes.remove(toRemove.getInput(), toRemove);
		
	}
	
	public static SmeltRecipe getRecipe(Material mat) {
		try {
			return recipes.entrySet().parallelStream().filter(Entry -> Entry.getKey().contains(mat)).collect(Collectors.toList()).get(0).getValue();
		} catch (Exception e) {
			throw new IllegalArgumentException("There is no recipe with material " + materialToString(mat));
		}
	}
	
	private static void generateRecipes() throws IOException {
		
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (recipe instanceof FurnaceRecipe) {
				FurnaceRecipe fr = (FurnaceRecipe)recipe;
				SmeltRecipe rec = new SmeltRecipe(fr.getInput().getType(), fr.getExperience(), fr.getResult());
				rec.setKey(fr.getKey().getKey().toLowerCase());
				
				if (fr.getInputChoice() instanceof MaterialChoice && ((MaterialChoice)fr.getInputChoice()).getChoices().size() > 1) {
					rec.setInput(((MaterialChoice)fr.getInputChoice()).getChoices());
				}
				
				rec.save();
				
				if (!config.isSet("enabledRecipes." + rec.getKey())) {
					
					config.set("enabledRecipes." + rec.getKey(), true);
					
					if (rec.getInput().stream().anyMatch(Material -> materialToString(Material).contains("axe") ||
							materialToString(Material).contains("pickaxe") ||
							materialToString(Material).contains("sword") ||
							materialToString(Material).contains("hoe") ||
							materialToString(Material).contains("flint") ||
							materialToString(Material).contains("shears") ||
							materialToString(Material).contains("compass") ||
							materialToString(Material).contains("helmet") ||
							materialToString(Material).contains("chestplate") ||
							materialToString(Material).contains("leggings") ||
							materialToString(Material).contains("boots") ||
							materialToString(Material).contains("armor")))
							config.set("enabledRecipes." + rec.getKey(), false);
					
				}
				
			}
		}
		main.messenger.print("Recipes Generated!");
		config.save();
		
		recipes.clear();
		
		for (String key : config.getConfigurationSection("enabledRecipes").getValues(false).keySet()) {
			if (config.getBoolean("enabledRecipes." + key)) {
				SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, key + ".yml")));
			}
		}
		
	}
	
}
