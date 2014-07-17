package me.bw.fastcraft;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryManager implements Listener {
	public HashMap<String, FastCraftInv> inventories = new HashMap<String, FastCraftInv>();
	public HashMap<String, Boolean> openingFastInv = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> openingOtherInv = new HashMap<String, Boolean>();
	public HashMap<String, Boolean> playerToggle = new HashMap<String, Boolean>();

	public boolean getPlayerToggle(String player){
		Boolean result = playerToggle.get(player);
		if (result == null){
			String path = player + ".fastCraftEnabled";
			if (!FastCraft.playerPrefsConfig.contains(path))
				return Settings.fastCraftDefaultEnabled;
			return FastCraft.playerPrefsConfig.getBoolean(path);
		}
		return result;
	}
	public boolean togglePlayer(String player){
		return togglePlayer(player, !getPlayerToggle(player));
	}
	public boolean togglePlayer(String player, boolean value){
		playerToggle.put(player, value);
		FastCraft.playerPrefsConfig.set(player + ".fastCraftEnabled", value);
		return value;
	}

	public void closeInventories(){
		for (String s : inventories.keySet()){
			if (Bukkit.getPlayer(s) != null)
				Bukkit.getPlayer(s).closeInventory();
		}
	}

	private boolean isInvOpen(HumanEntity player){
		return inventories.get(player.getName()) != null;
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent event){
		if (event.isCancelled()) return;
		boolean openFastInv = openingFastInv.get(event.getPlayer().getName()) != null;
		boolean openOtherInv = openingOtherInv.get(event.getPlayer().getName()) != null;

		if (event.getPlayer() instanceof Player && !openFastInv && !openOtherInv && event.getInventory().getType() == InventoryType.WORKBENCH && getPlayerToggle(event.getPlayer().getName())){
			if (Permissions.playerHas((Player)event.getPlayer(), "fastcraft.use")){
				openingFastInv.put(event.getPlayer().getName(), true);
				FastCraftInv inv = new FastCraftInv();
				inv.setIngredients(new IngredientList(event.getPlayer().getInventory().getContents()));
				inv.updateInv(event.getInventory().getHolder());
				inventories.put(event.getPlayer().getName(), inv);
				event.setCancelled(true);
				event.getPlayer().openInventory(inv.getCurrentInv());
			}
		}
		if (openFastInv) openingFastInv.remove(event.getPlayer().getName());
		if (openOtherInv) openingOtherInv.remove(event.getPlayer().getName());
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		inventories.remove(event.getPlayer().getName());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		inventories.remove(event.getPlayer().getName());
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryDrag(InventoryDragEvent event){
		if (!isInvOpen(event.getWhoClicked()) || event.isCancelled()) return;
		for (int i : event.getRawSlots()){
			if (i < event.getView().getTopInventory().getSize()){
				event.setResult(Result.DENY);
				return;
			}
		}
		final FastCraftInv inv = inventories.get(event.getWhoClicked().getName());
		final Inventory topInv = event.getView().getTopInventory();
		Inventory bottomInv = event.getView().getBottomInventory();
		inv.setIngredients(new IngredientList(bottomInv.getContents()));
		for (ItemStack is : event.getNewItems().values()){
			inv.getIngredients().add(new Ingredient(is, is.getAmount()));
		}
		inv.updateInv(topInv.getHolder());

		Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
			public void run(){
				topInv.setContents(inv.getCurrentInv().getContents());
			}
		}, 1);

	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent event){
		if (event instanceof CraftItemEvent) return;
		if (!isInvOpen(event.getWhoClicked()) || event.isCancelled() || event.getResult() == Result.DENY || event.getRawSlot() < 0) return;
		
		final Player player = (Player)event.getWhoClicked();
		FastCraftInv inv = inventories.get(event.getWhoClicked().getName());
		Inventory topInv = event.getView().getTopInventory();
		Inventory bottomInv = event.getView().getBottomInventory();
		ItemStack clickedItem = event.getView().getItem(event.getRawSlot());

		boolean updateInv = false;
		if (event.getRawSlot() >= 0 && event.getRawSlot() < event.getView().getTopInventory().getSize()){
			event.setResult(Result.DENY);
			if (clickedItem != null && clickedItem.getType() != Material.AIR){
				boolean click = true;
				if (event.getSlot() == Settings.invButtonPrevSlot){
					inv.setPage(inv.getPage() - 1);
					updateInv = true;
				}else if (event.getSlot() == Settings.invButtonNextSlot){
					inv.setPage(inv.getPage() + 1);
					updateInv = true;
				}else if (event.getSlot() == Settings.invButtonCraftSlot){
					openingOtherInv.put(player.getName(), true);
					Bukkit.getScheduler().scheduleSyncDelayedTask(FastCraft.plugin, new Runnable(){
						public void run(){
							player.openWorkbench(null, true);
						}
					}, 1);
				}else if (event.getSlot() == Settings.invButtonRefreshSlot){
					inv.resetCraftableItems();
					updateInv = true;
				}else if (event.getSlot() == Settings.invButtonHelpSlot){
					//Do nothing
				}else{
					click = false;
					boolean removeIngredients = false;
					boolean shiftAdd = false;
					FastRecipe curRecipe = inv.getCraftableItems().get(inv.getPage() * Settings.craftItemRows * 9 + event.getSlot());
					
					final Inventory fakeTopInv = curRecipe.createCraftingInventory(event.getWhoClicked());
					InventoryView fakeView = new InventoryView(){
						@Override public Inventory getBottomInventory()
						{ return event.getView().getBottomInventory(); }
						@Override public HumanEntity getPlayer()
						{ return event.getWhoClicked(); }
						@Override public Inventory getTopInventory()
						{ return fakeTopInv; }
						@Override public InventoryType getType()
						{ return fakeTopInv.getType(); }
					};
					
					CraftItemEvent craftEvent = new CraftItemEvent(
							curRecipe.getRecipe(),
							fakeView,
							SlotType.RESULT,
							0, //Crafting result slot ID
							event.getClick(),
							event.getAction()
					);
					
					ItemStack startItem = craftEvent.getCurrentItem();
					Bukkit.getServer().getPluginManager().callEvent(craftEvent);
					
					boolean isCancelled = craftEvent.isCancelled();
					boolean resultDeny = craftEvent.getResult() == Result.DENY;
					
					if (isCancelled || resultDeny){
						event.setCancelled(craftEvent.isCancelled());
						event.setResult(craftEvent.getResult());
						return;
					}
					
					ItemStack recipeResult = craftEvent.getCurrentItem();
					craftEvent.setCurrentItem(startItem);

					if (curRecipe.canCraft(inv.getIngredients())){
						switch (event.getClick()){
						case LEFT:
						case RIGHT:
						case DOUBLE_CLICK:
							if (event.getCursor().getType() == Material.AIR){
								player.setItemOnCursor(recipeResult);
								removeIngredients = true;
							}else if (event.getCursor().isSimilar(recipeResult) && recipeResult.getAmount() + event.getCursor().getAmount() <= recipeResult.getMaxStackSize()){
								event.getCursor().setAmount(event.getCursor().getAmount() + recipeResult.getAmount());
								removeIngredients = true;
							}
							break;
						case SHIFT_LEFT:
						case SHIFT_RIGHT:
							if (canShiftAddToInv(recipeResult, recipeResult.getAmount(), player.getInventory())){
								removeIngredients = true;
								shiftAdd = true;
							}
							break;
						case CONTROL_DROP:
						case DROP:
							removeIngredients = true;
							event.getView().setItem(InventoryView.OUTSIDE, recipeResult);
							break;
						case MIDDLE:
							if (player.getGameMode() == GameMode.CREATIVE && player.getItemOnCursor().getType() == Material.AIR){
								ItemStack newCursor = new ItemStack(event.getCurrentItem());
								newCursor.setAmount(newCursor.getMaxStackSize());
								player.setItemOnCursor(newCursor);
							}
							break;
						case NUMBER_KEY:
							//TODO
							break;
						default:
							//Shouldn't be possible to get to this point
							break;
						}
						if (removeIngredients){
							curRecipe.getIngredients().removeFromInv(player.getInventory());
							updateInv = true;
						}
						if (shiftAdd){
							ItemStack notAdded = shiftAddToInv(recipeResult, recipeResult.getAmount(), player.getInventory());
							if (notAdded != null){
								event.getView().setItem(InventoryView.OUTSIDE, notAdded);
							}
							updateInv = true;
						}
					}
				}
				if (click) player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
			}
		}else if (event.getRawSlot() > 0){
			event.setResult(Result.DENY);
			switch (event.getClick()){
			case CONTROL_DROP:
				player.getInventory().setItem(InventoryView.OUTSIDE, event.getCurrentItem()); //TODO Not dropping in the right direction
				bottomInv.setItem(event.getSlot(), null);
				break;
			case DROP:
				ItemStack toDrop = new ItemStack(event.getCurrentItem());
				if (event.getCurrentItem().getAmount() > 1){
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
					toDrop.setAmount(1);
				}else{
					event.setCurrentItem(null);
				}
				player.getInventory().setItem(InventoryView.OUTSIDE, toDrop);
				break;
			case LEFT:
				if (event.getCurrentItem().getType() == Material.AIR){
					event.setCurrentItem(player.getItemOnCursor());
					player.setItemOnCursor(null);
				}else if (player.getItemOnCursor().getType() == Material.AIR){
					player.setItemOnCursor(event.getCurrentItem());
					event.setCurrentItem(null);
				}else if (event.getCurrentItem().isSimilar(player.getItemOnCursor())){
					int move = Math.min(event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount(), player.getItemOnCursor().getAmount());
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + move);
					if (move == player.getItemOnCursor().getAmount()){
						player.setItemOnCursor(null);
					}else{
						player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - move);
					}
				}else{
					ItemStack newCursor = event.getCurrentItem();
					ItemStack newInvItem = player.getItemOnCursor();
					player.setItemOnCursor(newCursor);
					event.setCurrentItem(newInvItem);
				}
				break;
			case RIGHT:
				if (event.getCurrentItem().getType() == Material.AIR){
					if (player.getItemOnCursor().getAmount() > 1){
						event.setCurrentItem(player.getItemOnCursor());
						player.setItemOnCursor(null);
					}else{
						event.setCurrentItem(player.getItemOnCursor());
						event.getCurrentItem().setAmount(1);
						player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - 1);
					}
				}else if (player.getItemOnCursor().getType() == Material.AIR){
					int take = (int)Math.ceil(event.getCurrentItem().getAmount() / 2.);
					if (take == event.getCurrentItem().getAmount()){
						player.setItemOnCursor(event.getCurrentItem());
						event.setCurrentItem(null);
					}else{
						ItemStack newCursor = new ItemStack(event.getCurrentItem());
						newCursor.setAmount(take);
						player.setItemOnCursor(newCursor);
						event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - take);
					}
				}else if (event.getCurrentItem().isSimilar(player.getItemOnCursor())){
					int move = Math.min(event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount(), 1);
					event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + move);
					if (move == player.getItemOnCursor().getAmount()){
						player.setItemOnCursor(null);
					}else{
						player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - move);
					}
				}else{
					ItemStack newCursor = event.getCurrentItem();
					ItemStack newInvItem = player.getItemOnCursor();
					player.setItemOnCursor(newCursor);
					event.setCurrentItem(newInvItem);
				}
				break;
			case MIDDLE:
				if (player.getGameMode() == GameMode.CREATIVE && player.getItemOnCursor().getType() == Material.AIR){
					ItemStack newCursor = new ItemStack(event.getCurrentItem());
					newCursor.setAmount(newCursor.getMaxStackSize());
					player.setItemOnCursor(newCursor);
				}
				break;
			case NUMBER_KEY:
				if (event.getHotbarButton() != event.getSlot()){
					ItemStack oldNumbItem = bottomInv.getItem(event.getHotbarButton());
					bottomInv.setItem(event.getHotbarButton(), event.getCurrentItem());
					event.setCurrentItem(oldNumbItem);
				}
				break;
			case WINDOW_BORDER_LEFT:
				event.getView().setItem(InventoryView.OUTSIDE, player.getItemOnCursor());
				player.setItemOnCursor(null);
				break;
			case WINDOW_BORDER_RIGHT:
				if (player.getItemOnCursor().getAmount() <= 1){
					event.getView().setItem(InventoryView.OUTSIDE, player.getItemOnCursor());
					player.setItemOnCursor(null);
				}else{
					ItemStack itemToDrop = new ItemStack(player.getItemOnCursor());
					itemToDrop.setAmount(1);
					event.getView().setItem(InventoryView.OUTSIDE, itemToDrop);
					player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - 1);
				}
				break;
			case UNKNOWN:
				updateInv = true;
			case DOUBLE_CLICK:
				//TODO
				break;
			case SHIFT_RIGHT:
				//TODO
				break;
			case SHIFT_LEFT:
				//TODO
				break;
			case CREATIVE:
				//TODO
				break;
			default:
				break;
			}
			updateInv = true;
		}
		if (updateInv){
			inv.setIngredients(new IngredientList(bottomInv.getContents()));
			inv.updateInv(topInv.getHolder());
			topInv.setContents(inv.getCurrentInv().getContents());
		}
	}

	private ItemStack shiftAddToInv(ItemStack item, int amount, Inventory inv){
		if (amount == 0) return null;
		int remaining = amount;
		for (int i = 0; i <= 1; i++){
			for (int rawRow = inv.getSize() / 9; rawRow > 0; rawRow--){
				int row = (rawRow == inv.getSize() / 9)?0:rawRow;
				for (int column = 8; column >= 0; column--){
					int slot = row * 9 + column;
					ItemStack curItem = inv.getItem(slot);
					if (i == 0 && curItem != null && curItem.isSimilar(item)){
						int toAdd = Math.min(item.getMaxStackSize() - curItem.getAmount(), remaining);
						curItem.setAmount(curItem.getAmount() + toAdd);
						remaining -= toAdd;
					}else if (i == 1 && curItem == null){
						int toAdd = Math.min(remaining, item.getMaxStackSize());
						ItemStack newItem = new ItemStack(item);
						newItem.setAmount(toAdd);
						remaining -= toAdd;
						inv.setItem(slot, newItem);
					}
					if (remaining == 0) return null;
				}
			}
		}
		ItemStack notAdded = new ItemStack(item);
		notAdded.setAmount(remaining);
		return notAdded;
	}
	
	private boolean canShiftAddToInv(ItemStack item, int amount, Inventory inv){
		ItemStack newItem = new ItemStack(item);
		Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize());
		newInv.setContents(inv.getContents());
		return shiftAddToInv(newItem, amount, newInv) == null;
	}
}



























