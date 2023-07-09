package youngsditch.ancientlogistics.gears;

import youngsditch.ancientlogistics.mixin.TrommelAccessor;
import net.minecraft.src.*;

public class GearTrommel extends GearUsable {

	public GearTrommel(int id) {
		super(id);
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
	public int costToUse(World world, int x, int y, int z, EntityPlayer player) {
		
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		// if block above is a trommel
		if (blockAbove instanceof BlockTrommel) {
			TileEntityTrommel tileEntityTrommel = (TileEntityTrommel)world.getBlockTileEntity(x, y + 1, z);

			// if trommel is not burning and there is something to burn
			if(findNextToSieve(tileEntityTrommel) && tileEntityTrommel.burnTime < 50) {
				return 2;
			}
		}
		
		return 0;
	}

	@Override
	public int onGearUsed(World world, int x, int y, int z, EntityPlayer player) {
		// get block above
		Block blockAbove = Block.blocksList[world.getBlockId(x, y + 1, z)];

		// if block above is a trommel
		if (blockAbove instanceof BlockTrommel) {
			TileEntityTrommel tileEntityTrommel = (TileEntityTrommel)world.getBlockTileEntity(x, y + 1, z);
			
			// store burning state before adding burn time
			boolean isBurning = tileEntityTrommel.isBurning();

			// if trommel is not burning and there is something to burn
			if(findNextToSieve(tileEntityTrommel) && tileEntityTrommel.burnTime < 50) {
				tileEntityTrommel.burnTime = Math.min(tileEntityTrommel.burnTime + 25, 50);

				// if burning has changed, update block state and notify
				if(isBurning != tileEntityTrommel.isBurning()) {
					BlockTrommel.updateTrommelBlockState(tileEntityTrommel.burnTime > 0, world, x, y + 1, z);
					tileEntityTrommel.onInventoryChanged();
				}
				return 2;
			}
		}

		return 0;
	}
}
