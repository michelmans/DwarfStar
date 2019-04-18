package com.alchemi.dwarfstar.objects.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemsEditEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	private final UUID parent;
	private final List<ItemStack> oldItems;
	private final List<ItemStack> newItems;
	
	public ItemsEditEvent(UUID parent, List<ItemStack> oldItems, List<ItemStack> newItems) {
		this.parent = parent;
		this.oldItems = oldItems;
		this.newItems = newItems;
	}
	
	/**
	 * @return the parent
	 */
	public UUID getParent() {
		return parent;
	}

	/**
	 * @return the oldItems
	 */
	public List<ItemStack> getOldItems() {
		return oldItems;
	}

	/**
	 * @return the newItems
	 */
	public List<ItemStack> getNewItems() {
		return newItems;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
