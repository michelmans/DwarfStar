package me.alchemi.dwarfstar.objects.enchantments;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.dwarfstar.Config;
import me.alchemi.dwarfstar.Config.Options;
import me.alchemi.dwarfstar.Star;
import me.alchemi.dwarfstar.objects.SmeltRecipe;

public class AutoSmelt extends Enchantment implements Listener {

	public static final NamespacedKey KEY = new NamespacedKey(Star.getInstance(), "autosmelt".toUpperCase());
	
	public AutoSmelt() {
		super(KEY);
		
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);

			registerEnchantment(this);
			Star.getInstance().getMessenger().print("AutoSmelt enchantment registered!");

			stopAcceptingRegistrations();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Bukkit.getPluginManager().registerEvents(this, Star.getInstance());
			
		
	}

	@Override
	public String getName() {
		return "AUTOSMELT";
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return EnchantmentWrapper.SILK_TOUCH.equals(other);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType() == MaterialWrapper.DIAMOND_PICKAXE.getMaterial() 
				&& noneConflict(item.getEnchantments().keySet());
	}
	
	public boolean noneConflict(Enchantment...enchantments) {
		for (Enchantment ench : enchantments) {
			if (conflictsWith(ench)) return false;
		}
		return true;
	}
	
	public boolean noneConflict(Collection<Enchantment> enchantments) {
		for (Enchantment ench : enchantments) {
			if (conflictsWith(ench)) return false;
		}
		return true;
	}
	
	@EventHandler
	public void onEnchant(EnchantItemEvent e) {

		if (Options.AUTOSMELT_CHANCE.asInt() != 0 
				&& e.getEnchanter().hasPermission("dwarfstar.smelt.enchantment") 
				&& e.getExpLevelCost() == 30
				&& noneConflict(e.getEnchantsToAdd().keySet())
				&& new Random().nextInt(100) <= Options.AUTOSMELT_CHANCE.asDouble()) {
			
			e.getEnchantsToAdd().put(this, 1);
			ItemMeta meta = e.getItem().getItemMeta();

			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				lore.add(Messenger.formatString("&7AutoSmelt"));
				meta.setLore(lore);
			} else {
				meta.setLore(Arrays.asList(Messenger.formatString("&7AutoSmelt")));
			}

			e.getItem().setItemMeta(meta);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBreak(BlockBreakEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand().getEnchantments().keySet().contains(this)
				&& e.isDropItems() && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			
			try {
				SmeltRecipe smelt = Config.getRecipe(e.getBlock().getType());
				smelt.smeltPlayerless(e.getBlock(), e.getPlayer().getInventory().getItemInMainHand());
				e.setDropItems(false);
			} catch(IllegalArgumentException ex) {}			
		}
	}
}
