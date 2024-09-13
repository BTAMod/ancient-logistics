package youngsditch.ancientlogistics.gears;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockChest;
import net.minecraft.core.block.entity.*;
import net.minecraft.core.entity.player.*;
import net.minecraft.core.world.World;
import net.minecraft.core.item.ItemStack;
import youngsditch.ancientlogistics.AncientLogistics;
import youngsditch.ancientlogistics.mixin.ChestAccessor;

public class GearChestSorter extends GearUsable {

	public GearChestSorter(String key, int id) {
		super(key, id);
	}

	class ChestWithDistance {
			public final TileEntityChest chest;
			public final float distance;

			ChestWithDistance(TileEntityChest chest, float distance) {
					this.chest = chest;
					this.distance = distance;
			}
	}

	@Override
	public int costToUse(World world, int x, int y, int z, EntityPlayer player, boolean canConnect) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		if (blockAbove instanceof BlockChest) {
			// first block must have a chest over it, the rest don't matter
			if(!canConnect) {
				return 2;
			} else {

				// get all connected gears
				GearInfo<GearChestSorter>[] gearInfo = getConnected(world, x, y, z, GearChestSorter.class);

				int chests = 0;

				// check above each gear for a chest
				for (int i = 0; i < gearInfo.length; i++) {
					int[] coordinates = gearInfo[i].getCoordinates();
					int[] chestCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
					if (Block.blocksList[world.getBlockId(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2])] instanceof BlockChest) {
						chests++;
					}
				}

				// return check count * 2
				return chests * 2;
			}
		}

		// return 0 if no chest above
		return 0;
	}

	@Override
	public int onGearUsed(World world, int x, int y, int z, EntityPlayer player, boolean canConnect) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		if (blockAbove instanceof BlockChest) {
			// first block must have a chest over it, the rest don't matter

			GearInfo<GearChestSorter>[] gearInfo;

			if(canConnect) {
				gearInfo = getConnected(world, x, y, z, GearChestSorter.class);
			} else {
				gearInfo = getSolo(world, x, y, z, GearChestSorter.class);
			}

			AncientLogistics.LOGGER.info("Found " + gearInfo.length + " connected gears");

			ChestWithDistance[] chests = new ChestWithDistance[gearInfo.length];

			// check above each gear for a chest
			for (int i = 0; i < gearInfo.length; i++) {
				int[] coordinates = gearInfo[i].getCoordinates();
				int[] chestCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
				TileEntity tileEntity = world.getBlockTileEntity(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2]);
				if (tileEntity instanceof TileEntityChest) {
					chests[i] = new ChestWithDistance((TileEntityChest)tileEntity, gearInfo[i].getDistance());
				}
			}

			// filter out null chests
			chests = Arrays.stream(chests).filter(chest -> chest != null).toArray(ChestWithDistance[]::new);

			// sort chests by distance
			Arrays.sort(chests, new Comparator<ChestWithDistance>() {
				@Override
				public int compare(ChestWithDistance a, ChestWithDistance b) {
					return Float.compare(a.distance, b.distance);
				}
			});

			// get all items from all chests
			ArrayList<ItemStack> allItems = new ArrayList<ItemStack>();

			// go through all chests and add all items to allItems, then clear the chest
			for (int i = 0; i < chests.length; i++) {
				ItemStack[] chestContents = ((ChestAccessor)chests[i].chest).getChestContents();
				for (int j = 0; j < chestContents.length; j++) {
					if (chestContents[j] != null) {
						allItems.add(chestContents[j]);
						chestContents[j] = null;
					}
				}
			}

			// sort allItems by max stack size, then by name
			allItems.sort(new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack a, ItemStack b) {
					int maxSizeCompare = Integer.compare(a.getMaxStackSize(), b.getMaxStackSize());
					if (maxSizeCompare == 0) {
						return a.getItem().getTranslatedName(a).compareTo(b.getItem().getTranslatedName(b));
					} else {
						return maxSizeCompare;
					}
				}
			});

			// merge like itemstacks
			// itemstacks have getMaxStackSize() and getItem
			// if itemstack.getItem() == item.getItem(), consider merging,
			int index = 0;
			// until we've checked them all
			while(index < allItems.size()) {
				ItemStack currentItem = allItems.get(index);
				// if the next item is the same item and the current item is not full, keep going
				while(index + 1 < allItems.size() && currentItem.getItem() == allItems.get(index + 1).getItem() && currentItem.stackSize < currentItem.getMaxStackSize()) {
					ItemStack nextItem = allItems.get(index + 1);
					// if the next item can fit in the current item, merge it
					if (currentItem.stackSize + nextItem.stackSize <= currentItem.getMaxStackSize()) {
						currentItem.stackSize += nextItem.stackSize;
						allItems.remove(index + 1);
					} else {
						// otherwise, fill the current item and move on
						nextItem.stackSize -= currentItem.getMaxStackSize() - currentItem.stackSize;
						currentItem.stackSize = currentItem.getMaxStackSize();
						break;
					}
				}
				index++;
			}

			// go through all chests and add items from allItems to the chest
			for (int i = 0; i < chests.length; i++) {
				ItemStack[] chestContents = ((ChestAccessor)chests[i].chest).getChestContents();
				for (int j = 0; j < chestContents.length; j++) {
					if (chestContents[j] == null && allItems.size() > 0) {
						chestContents[j] = allItems.remove(0);
					}
				}
			}

			return chests.length * 2;
		}

		return 0;
	}
}
