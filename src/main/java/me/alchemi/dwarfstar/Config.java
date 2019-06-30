package me.alchemi.dwarfstar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.base.ConfigBase;
import me.alchemi.dwarfstar.objects.SmeltRecipe;
import me.alchemi.dwarfstar.objects.SmeltRecipeBuilder;

public class Config extends ConfigBase{

	public Config() throws FileNotFoundException, IOException, InvalidConfigurationException {
		super(main.getInstance());
		
		if (ConfigEnum.CONFIG.getConfig().contains("enabledRecipes") 
				&& ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes") != null
				&& !ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes").getValues(false).isEmpty()) 
			for (String key : ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes").getValues(false).keySet()) {
				if (ConfigEnum.CONFIG.getConfig().getBoolean("enabledRecipes." + key)) {
					try {
						SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, key + ".yml")));
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}
		
		else {
			main.getInstance().getMessenger().print("No recipes found! Generating...");
			generateRecipes();
		}
	}
	
	public static enum ConfigEnum implements IConfigEnum {
		CONFIG(new File(main.getInstance().getDataFolder(), "config.yml"), 1),
		MESSAGES(new File(main.getInstance().getDataFolder(), "messages.yml"), 5);
		
		final int version;
		final File file;
		SexyConfiguration config;
		
		private ConfigEnum(File file, int version) {
			this.version = version;
			this.file = file;
			this.config = SexyConfiguration.loadConfiguration(file);
		}
		
		@Override
		public SexyConfiguration getConfig() {
			return config;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public int getVersion() {
			return version;
		}
		
	}
	
	private static Map<List<Material>, SmeltRecipe> recipes = new HashMap<List<Material>, SmeltRecipe>();
	private static Map<String, SmeltRecipe> recipesNames = new HashMap<String, SmeltRecipe>();
	
	public static enum Options implements IConfig {
		defaultSmeltType("default-smelt-type"),
		requestOption("request-option"),
		enableCondenser("enable-condenser"),
		updateChecker("update-checker");

		private final String key;
		private Object value;

		private Options(String key) {
			this.key = key;
			get();		
		}
		
		@Override
		public Object value() {
			return value;
		}

		@Override
		public String key() {
			return key;
			
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@Override
		public void get() {
			this.value = getConfig().get(key);			
		}

		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}

		@Override
		public String asString() {
			return String.valueOf(value);
		}

		@Override
		public Sound asSound() {
			return null;
		}

		@Override
		public List<String> asStringList() {
			return null;
		}

		@Override
		public int asInt() {
			return 0;
		}

		@Override
		public ItemStack asItemStack() {
			return null;
		}

		@Override
		public Material asMaterial() {
			return null;
		}
		
	}
	
	public static enum Messages implements IMessage {
		MOLTEN("DwarfStar.Molten"),
		NO_MELTING("DwarfStar.No-Melting"),
		CONDENSED("DwarfStar.Condensed"),
		NO_CONDENSING("DwarfStar.No-Condensing"),
		COMMANDS_NO_PERMISSION("DwarfStar.Commands.No-Permission"),
		COMMANDS_WRONG_FORMAT("DwarfStar.Commands.Wrong-Format"),
		COMMANDS_UNKNOWN("DwarfStar.Commands.Unknown"),
		COMMANDS_RECIPE_CREATED("DwarfStar.Commands.Recipe.Created"),
		COMMANDS_RECIPE_DELETED("DwarfStar.Commands.Recipe.Deleted"),
		COMMANDS_RECIPE_ERROR("DwarfStar.Commands.Recipe.Error"),
		COMMANDS_RECIPE_NO_RECIPE_NAME("DwarfStar.Commands.Recipe.No-Recipe-Name"),
		COMMANDS_RECIPE_NO_RECIPE_MATERIAL("DwarfStar.Commands.Recipe.No-Recipe-Material"),
		COMMANDS_RECIPE_BAD_NAME("DwarfStar.Commands.Recipe.Bad-Name"),
		COMMANDS_RECIPE_MATERIAL_HAS_RECIPE("DwarfStar.Commands.Recipe.Material-Has-Recipe"),
		COMMANDS_RECIPE_DISABLED("DwarfStar.Commands.Recipe.Disabled"),
		COMMANDS_RECIPE_ENABLED("DwarfStar.Commands.Recipe.Enabled");

		String value;
		String key;

		private Messages(String key) {
			this.key = key;
		}

		public void get() {
			value = getConfig().getString(key, "PLACEHOLDER - STRING NOT FOUND");
		}
  
		public String value() { return value; }

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.MESSAGES.getConfig();
		} 
	
	}

	
	@Override
	public void save() {
		
		for (SmeltRecipe rec : recipes.values()) {
			if (!rec.isEdited()) continue;
			
			rec.save();
			
			if (!ConfigEnum.CONFIG.getConfig().isSet("enabledRecipes." + rec.getKey())) {
				
				ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + rec.getKey(), true);
				
			}
			
			rec.setEdited(false);
		}
		
		if (ConfigEnum.CONFIG.getConfig().isSet("enabledRecipes")) ConfigEnum.CONFIG.getConfig().setComment("enabledRecipes", "# If these recipes are enabled or not. They all default to true.");
		
		try {
			ConfigEnum.CONFIG.getConfig().save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String materialToString(Material mat) {
		
		return mat.getKey().getKey().replaceAll("_", " ").toLowerCase();
		
	}
	
	public static void addRecipe(SmeltRecipe toAdd) {
		
		recipes.put(toAdd.getInput(), toAdd);
		recipesNames.put(toAdd.getKey(), toAdd);
		
	}
	
	public static boolean hasRecipe(Material mat) {
		return recipes.keySet().stream().anyMatch(Materials -> Materials.contains(mat));
	}
	
	public static boolean hasRecipe(String key) {
		return recipesNames.containsKey(key);
	}
	
	public static void removeRecipe(SmeltRecipe toRemove) {
		
		recipes.remove(toRemove.getInput(), toRemove);
		recipesNames.remove(toRemove.getKey());
		
	}
	
	public static SmeltRecipe getRecipe(Material mat) {
		try {
			return recipes.entrySet().parallelStream().filter(Entry -> Entry.getKey().contains(mat)).collect(Collectors.toList()).get(0).getValue();
		} catch (Exception e) {
			throw new IllegalArgumentException(Messenger.formatString(Messages.COMMANDS_RECIPE_NO_RECIPE_MATERIAL.value()
					.replace("$material$", mat.toString())));
		}
	}
	
	public static SmeltRecipe getRecipe(String key) {
		if (recipesNames.containsKey(key)) {
			return recipesNames.get(key);
		} else {
			throw new IllegalArgumentException(Messenger.formatString(Messages.COMMANDS_RECIPE_NO_RECIPE_NAME.value()
					.replace("$name$", key)));
		}
	}
	
	public static Set<String> getRecipeKeys(){
		return recipesNames.keySet();
	}
	
	public static int getRecipeCount() {
		return recipes.size();
	}
	
	public static Set<Material> possibleMaterials() {
		Set<Material> materials = new HashSet<Material>();
		recipes.keySet().forEach(KEY -> materials.addAll(KEY));
		return materials;
	}
	
	public static void regenerateRecipes() {
		ConfigEnum.CONFIG.getConfig().set("enabledRecipes", null);
		for (File file : main.RECIPES_FOLDER.listFiles()) {
			if (file.getName().endsWith(".yml") && file.isFile()) {
				file.delete();
			}
		}
		recipes.clear();
		recipesNames.clear();
		try {
			generateRecipes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void generateRecipes() throws IOException {
		
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (recipe instanceof FurnaceRecipe) {
				FurnaceRecipe fr = (FurnaceRecipe)recipe;
				
				if (fr.getInput().getType() == MaterialWrapper.AIR.getMaterial()) continue;
				
				SmeltRecipe rec = new SmeltRecipeBuilder()
						.input((MaterialChoice)fr.getInputChoice())
						.xp(fr.getExperience())
						.output(fr.getResult())
						.key(fr.getKey().getKey().toLowerCase())
						.create();
				
				rec.save();
				
				if (!ConfigEnum.CONFIG.getConfig().isSet("enabledRecipes." + rec.getKey())) {
					
					ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + rec.getKey(), true);
					
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
							ConfigEnum.CONFIG.getConfig().set("enabledRecipes." + rec.getKey(), false);
					
				}
			}
		}
		main.getInstance().getMessenger().print("Recipes Generated!");
		ConfigEnum.CONFIG.getConfig().save();
		
		recipes.clear();
		recipesNames.clear();
		
		for (String key : ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes").getValues(false).keySet()) {
			if (ConfigEnum.CONFIG.getConfig().getBoolean("enabledRecipes." + key)) {
				SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, key + ".yml")));
			}
		}
		
	}

	@Override
	protected IConfigEnum[] getConfigs() {
		return ConfigEnum.values();
	}

	@Override
	protected Set<IConfig> getEnums() {
		return new HashSet<ConfigBase.IConfig>() {
			{
				addAll(Arrays.asList(Options.values()));
			}
		};
	}

	@Override
	protected Set<IMessage> getMessages() {
		return new HashSet<ConfigBase.IMessage>() {
			{
				addAll(Arrays.asList(Messages.values()));
			}
		};
	}
	
	@Override
	public void reload() {
		super.reload();
		
		if (ConfigEnum.CONFIG.getConfig().contains("enabledRecipes") 
				&& ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes") != null
				&& !ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes").getValues(false).isEmpty()) 
			for (String key : ConfigEnum.CONFIG.getConfig().getConfigurationSection("enabledRecipes").getValues(false).keySet()) {
				if (ConfigEnum.CONFIG.getConfig().getBoolean("enabledRecipes." + key)) {
					try {
						SmeltRecipe.load(SexyConfiguration.loadConfiguration(new File(main.RECIPES_FOLDER, key + ".yml")));
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}
		
		else {
			main.getInstance().getMessenger().print("No recipes found! Generating...");
			try {
				generateRecipes();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
