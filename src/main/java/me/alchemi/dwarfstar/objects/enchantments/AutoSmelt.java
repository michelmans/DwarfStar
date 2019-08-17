package me.alchemi.dwarfstar.objects.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.dwarfstar.Star;

public class AutoSmelt extends Enchantment{

	public static final NamespacedKey KEY = new NamespacedKey(Star.getInstance(), "autosmelt");
	
	public AutoSmelt() {
		super(KEY);
		
		if (isAcceptingRegistrations()) {
			registerEnchantment(this);
			Star.getInstance().getMessenger().print("AutoSmelt enchantment registered!");
		}
	}

	@Override
	public String getName() {
		return KEY.getKey();
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getStartLevel() {
		return 0;
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
		return EnchantmentWrapper.SILK_TOUCH == other;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType() == MaterialWrapper.DIAMOND_PICKAXE.getMaterial();
	}

}
