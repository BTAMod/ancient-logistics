package youngsditch.ancientlogistics.gears;

import youngsditch.ancientlogistics.mixin.TrommelAccessor;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.world.World;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.block.BlockTrommel;

public class GearTrommel extends GearUsable {

	public GearTrommel(String key, int id) {
		super(key, id);
	}

	// returns true if a sieve is found
	public boolean findNextToSieve(TileEntityTrommel tileEntityTrommel) {
		TrommelAccessor trommelAccessor = (TrommelAccessor)tileEntityTrommel;
		for (int i = 1; i <= 4; i++) {
			if (!trommelAccessor.invokeCanProduce(trommelAccessor.getNextToSieve())) {
				trommelAccessor.setNextToSieve((trommelAccessor.getNextToSieve() + 1) % 4);
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public int costToUse(World world, int x, int y, int z, EntityPlayer player, boolean canConnect) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		// if block above is a trommel
		if (blockAbove instanceof BlockTrommel) {
			if(!canConnect) {
				TileEntityTrommel tileEntityTrommel = (TileEntityTrommel)world.getBlockTileEntity(x, y + 1, z);

				// if trommel is not burning and there is something to burn
				if(findNextToSieve(tileEntityTrommel) && tileEntityTrommel.burnTime < 50) {
					return 2;
				}
			} else {
				GearInfo<GearTrommel>[] gearInfo = getConnected(world, x, y, z, GearTrommel.class);

				int trommels = 0;

				// check above each gear for a trommel
				for (int i = 0; i < gearInfo.length; i++) {
					int[] coordinates = gearInfo[i].getCoordinates();
					int[] trommelCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
					if (Block.blocksList[world.getBlockId(trommelCoordinates[0], trommelCoordinates[1], trommelCoordinates[2])] instanceof BlockTrommel) {
						trommels++;
					}
				}

				// return check count * 2
				return trommels * 2;
			}
		}
		return 0;
	}

	private boolean powerTrommel(World world, int x, int y, int z) {
		TileEntityTrommel tileEntityTrommel = (TileEntityTrommel)world.getBlockTileEntity(x, y, z);

		// store burning state before adding burn time
		boolean isBurning = tileEntityTrommel.isBurning();

		// if trommel is not burning and there is something to burn
		if(findNextToSieve(tileEntityTrommel) && tileEntityTrommel.burnTime < 50) {
			tileEntityTrommel.burnTime = Math.min(tileEntityTrommel.burnTime + 25, 50);

			// if burning has changed, update block state and notify
			if(isBurning != tileEntityTrommel.isBurning()) {
				BlockTrommel.updateTrommelBlockState(tileEntityTrommel.burnTime > 0, world, x, y, z);
				tileEntityTrommel.onInventoryChanged();
			}
			return true;
		}
		return false;
	}

	@Override
	public int onGearUsed(World world, int x, int y, int z, EntityPlayer player, boolean canConnect) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		// if block above is a trommel
		if (blockAbove instanceof BlockTrommel) {
			// get all connected gears
			GearInfo<GearTrommel>[] gearInfo;

			if(canConnect) {
				gearInfo = getConnected(world, x, y, z, GearTrommel.class);
			} else {
				gearInfo = getSolo(world, x, y, z, GearTrommel.class);
			}

			int totalCost = 0;

			// check above each gear for a trommel
			for (int i = 0; i < gearInfo.length; i++) {
				int[] coordinates = gearInfo[i].getCoordinates();
				int[] trommelCoordinates = {coordinates[0], coordinates[1] + 1, coordinates[2]};
				if (Block.blocksList[world.getBlockId(trommelCoordinates[0], trommelCoordinates[1], trommelCoordinates[2])] instanceof BlockTrommel) {
					if(powerTrommel(world, trommelCoordinates[0], trommelCoordinates[1], trommelCoordinates[2])){
						totalCost++;
					}
				}
			}

			return totalCost * 2;
		}

		return 0;
	}
}
