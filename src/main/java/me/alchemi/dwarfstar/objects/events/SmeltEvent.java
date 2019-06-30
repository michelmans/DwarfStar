package me.alchemi.dwarfstar.objects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.alchemi.dwarfstar.objects.SmeltRecipe;

public class SmeltEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	
	private final SmeltRecipe recipe;
	private final Player player;
	private final int smeltTimes;
	
	private boolean cancelled = false;
	
	public SmeltEvent(SmeltRecipe recipe, Player player, int smeltTimes) {
		this.recipe = recipe;
		this.player = player;
		this.smeltTimes = smeltTimes;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	/**
	 * @return the recipe
	 */
	public SmeltRecipe getRecipe() {
		return recipe;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the smeltTimes
	 */
	public int getSmeltTimes() {
		return smeltTimes;
	}

	
	
}
