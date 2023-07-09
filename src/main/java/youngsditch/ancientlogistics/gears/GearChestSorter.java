package youngsditch.ancientlogistics.gears;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.src.*;
import youngsditch.ancientlogistics.mixin.ChestAccessor;

public class GearChestSorter extends GearUsable {

	public GearChestSorter(int id) {
		super(id);
	}

	class ChestWithDistance {
			public final ChestAccessor chest;
			public final float distance;

			ChestWithDistance(ChestAccessor chest, float distance) {
					this.chest = chest;
					this.distance = distance;
			}
	}

	@Override
	public int costToUse(World world, int x, int y, int z, EntityPlayer player) {
		
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		if (blockAbove instanceof BlockChest) {
			// first block must have a chest over it, the rest don't matter

			GearInfo<GearChestSorter>[] gearInfo = getConnected(world, x, y, z, GearChestSorter.class);

			ArrayList<ChestWithDistance> chests = new ArrayList<ChestWithDistance>();

			// check above each gear for a chest
			for (int i = 0; i < gearInfo.length; i++) {
				int[] coordinates = gearInfo[i].getCoordinates();
				int[] chestCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
				if (Block.blocksList[world.getBlockId(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2])] instanceof BlockChest) {
					// chests.add((ChestAccessor)(TileEntityChest)world.getBlockTileEntity(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2]));
					chests.add(new ChestWithDistance((ChestAccessor)(TileEntityChest)world.getBlockTileEntity(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2]), gearInfo[i].getDistance()));
				}
			}

			// return check count * 2
			return chests.size() * 2;

		} else {
			// return 0 if no chest above
			return 0;
		}
	}

	@Override
	public int onGearUsed(World world, int x, int y, int z, EntityPlayer player) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		if (blockAbove instanceof BlockChest) {
			// first block must have a chest over it, the rest don't matter

			GearInfo<GearChestSorter>[] gearInfo = getConnected(world, x, y, z, GearChestSorter.class);

			ArrayList<ChestWithDistance> chests = new ArrayList<ChestWithDistance>();

			// check above each gear for a chest
			for (int i = 0; i < gearInfo.length; i++) {
				int[] coordinates = gearInfo[i].getCoordinates();
				int[] chestCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
				if (Block.blocksList[world.getBlockId(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2])] instanceof BlockChest) {
					// chests.add((ChestAccessor)(TileEntityChest)world.getBlockTileEntity(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2]));
					chests.add(new ChestWithDistance((ChestAccessor)(TileEntityChest)world.getBlockTileEntity(chestCoordinates[0], chestCoordinates[1], chestCoordinates[2]), gearInfo[i].getDistance()));
				}
			}

			// sort chests by size and distance largest first, then closest first
			List<ChestWithDistance> sortedChestWithDistance = chests.stream()
					.sorted(Comparator
							.<ChestWithDistance>comparingInt(chestWithDistance -> chestWithDistance.chest.getChestContents().length)
							.reversed()
							.thenComparing(chestWithDistance -> chestWithDistance.distance))
					.collect(Collectors.toList());

			ChestAccessor[] chestAccessors = sortedChestWithDistance.stream()
					.map(chestWithDistance -> chestWithDistance.chest)
					.toArray(ChestAccessor[]::new);

			// to get the maximum possible length, we need to add up the lengths of all the chests - this might account for other mods that increase chest size
			int fullLength = 0;
			for (int i = 0; i < chestAccessors.length; i++) {
				fullLength += chestAccessors[i].getChestContents().length;
			}

			ItemStack[] allItems = new ItemStack[fullLength];

			// copy all items from all chests into allItems
			int index = 0;
			for (int i = 0; i < chestAccessors.length; i++) {
				ItemStack[] chestContents = chestAccessors[i].getChestContents();
				for (int j = 0; j < chestContents.length; j++) {
					allItems[index] = chestContents[j];
					index++;
				}
			}

			// sort allItems by itemID
			ItemStack[] sortedItems = Arrays.stream(allItems)
				.filter(itemStack -> itemStack != null)
  			.sorted(Comparator.comparingInt(itemStack -> itemStack.itemID))
				.toArray(ItemStack[]::new);
				
			// reducedItems tries to merge itemStacks together
			ItemStack[] reducedItems = new ItemStack[sortedItems.length];

			// loop through sortedItems, if the item is the same as the previous item, add the stackSize to the previous item, otherwise add the item to reducedItems
			index = 0;
			for (int i = 0; i < sortedItems.length; i++) {
				if (i == 0) {
					reducedItems[index] = sortedItems[i];
					index++;
				} else {
					if (sortedItems[i].itemID == reducedItems[index - 1].itemID) {
						// there is an ItemStack.getMaxStackSize
						int previousStackSize = reducedItems[index - 1].stackSize;
						int currentStackSize = sortedItems[i].stackSize;
						int previousRoomLeft = reducedItems[index - 1].getMaxStackSize() - previousStackSize;
						int amountToReduce = Math.min(currentStackSize, previousRoomLeft);

						// increase previous and decrease current
						reducedItems[index - 1].stackSize += amountToReduce;
						sortedItems[i].stackSize -= amountToReduce;

						// if current is now empty, delete it
						if(sortedItems[i].stackSize < 1) {
							sortedItems[i] = null;
						} else {
							reducedItems[index] = sortedItems[i];
							index++;
						}
						
					} else {
						reducedItems[index] = sortedItems[i];
						index++;
					}
				}
			}

			// sort by getMaxSize and then getItemName
			reducedItems = Arrays.stream(reducedItems)
				.filter(itemStack -> itemStack != null)
				.sorted(Comparator.comparingInt(ItemStack::getMaxStackSize).thenComparing(ItemStack::getItemName))
				.toArray(ItemStack[]::new);

			// put all items back into chests
			index = 0;
			for (int i = 0; i < chestAccessors.length; i++) {
				ItemStack[] chestContents = chestAccessors[i].getChestContents();
				ItemStack[] newChestContents = new ItemStack[chestContents.length];
				for (int j = 0; j < chestContents.length; j++) {
					if(index < reducedItems.length) {
						newChestContents[j] = reducedItems[index];
					}
					index++;
				}
				chestAccessors[i].setChestContents(newChestContents);
			}
			
			return chestAccessors.length * 2;
		}

		return 0;
	}
}
