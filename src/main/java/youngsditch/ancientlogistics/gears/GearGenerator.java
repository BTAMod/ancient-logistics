package youngsditch.ancientlogistics.gears;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.util.Random;

public class GearGenerator extends GearBlock {
  private Random rand = new Random();
  protected boolean canRunMultiple = false;

  public GearGenerator(String key, int id) {
    super(key, id);
    this.rand = new Random();
  }

  public boolean isCovered(World world, int x, int y, int z) {
    return world.getBlockId(x, y + 1, z) != 0;
  }

  public boolean playerHasBone(EntityPlayer player) {
    return player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == Item.bone.id;
  }

  public boolean interactable(World world, int x, int y, int z, EntityPlayer player) {
    return !isCovered(world, x, y, z) && playerHasBone(player);
  }

  public GearInfo<GearUsable> nextToGearUsable(World world, int x, int y, int z) {
    // array of coordinates
    int[][] coordinates = {{x + 1, y, z}, {x - 1, y, z}, {x, y, z + 1}, {x, y, z - 1}};

    GearInfo<GearUsable> gearInfo = null;
    int count = 0;

    // check if any of the blocks are a GearUsable
    for (int[] coord : coordinates) {
        Block block = Block.blocksList[world.getBlockId(coord[0], coord[1], coord[2])];
        if (block instanceof GearUsable) {
            count++;
            if (count == 1) {
                gearInfo = new GearInfo<GearUsable>(coord, (GearUsable)block);
            }
        }
    }

    return count == 1 ? gearInfo : null;
}

  public boolean nextToGenerator(World world, int x, int y, int z) {
    // array of blocks
    Block[] blocks = {
      Block.blocksList[world.getBlockId(x + 1, y, z)],
      Block.blocksList[world.getBlockId(x - 1, y, z)],
      Block.blocksList[world.getBlockId(x, y, z + 1)],
      Block.blocksList[world.getBlockId(x, y, z - 1)]
    };

    int count = 0;
    // check if any of the blocks are a GearUsable
    for (Block block : blocks) {
      if (block instanceof GearGenerator) {
        count++;
      }
    }

    return count > 0;
  }

  public void showWorking(World world, int x, int y, int z, EntityPlayer player) {
    for (int i = 0; i < 2; i++) {
      // (particle, pos, motion)
      world.spawnParticle("flame",
        x + (this.rand.nextFloat() + 0.5)/2,
        y + 0.75 + (this.rand.nextFloat() + 0.5)/2,
        z + (this.rand.nextFloat() + 0.5)/2,
        (this.rand.nextFloat() - 0.5) / 100,
        (this.rand.nextFloat() - 0.5) / 100,
        (this.rand.nextFloat() - 0.5) / 100,
        0
        );
    }
    player.world.playSoundAtEntity(null, player, "mob.skeletonhurt", 0.25f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f)/2.0f);
  }

  public void showNotWorking(World world, int x, int y, int z, EntityPlayer player) {
    for (int i = 0; i < 5; i++) {
      // (particle, pos, motion)
      world.spawnParticle("smoke",
        x + (this.rand.nextFloat() + 0.5)/2,
        y + 0.75 + (this.rand.nextFloat() + 0.5)/2,
        z + (this.rand.nextFloat() + 0.5)/2,
        (this.rand.nextFloat() - 0.5) / 100,
        (this.rand.nextFloat() - 0.5) / 100,
        (this.rand.nextFloat() - 0.5) / 100,
        0);
    }
  }

  @Override
  public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xPlaced, double yPlaced) {
    if(world.isClientSide) {
      return true;
    }

    // do nothing if covered
    if(!interactable(world, x, y, z, player)) {
      return false;
    }

    // if next to a generator, spawn particles
    if (nextToGenerator(world, x, y, z)) {
      // spawn particles
      showNotWorking(world, x, y, z, player);
      return false;
    }

    GearInfo<GearUsable> gearUsable = nextToGearUsable(world, x, y, z);
    if (gearUsable != null) {

      int[] coords = gearUsable.getCoordinates();

      // check if the user has enough bones, roughly
      int bonesNeeded = gearUsable.getGear().costToUse(world, coords[0], coords[1], coords[2], player, this.canRunMultiple);

      if(!player.getGamemode().consumeBlocks()) {
        bonesNeeded = 1; // still needs one in hand
      }

      // bones ends up being somewhat random, but we're gonna say 1/10 of the value is the cost
      // if theres even a val of 1, which might be free, the cost ceil goes up to 1
      // i mean, you need a bone equipped to use it, so it's not like you can use it for free anyway
      if (Math.ceil(bonesNeeded / 10.0) > player.getCurrentEquippedItem().stackSize) {
        showNotWorking(world, x, y, z, player);
        return false;
      }

      // spawn particles
      int value = gearUsable.getGear().onGearUsed(world, coords[0], coords[1], coords[2], player, this.canRunMultiple);

      showWorking(world, x, y, z, player);
      // 1 in 10 chance of breaking a bone for every value
      for (int i = 0; i < value; i++) {
        if (this.rand.nextInt(10) == 0) {
          // log break
          player.world.playSoundAtEntity(null, player, "mob.skeletonhurt", 0.5f, 1.0f);
          if(playerHasBone(player) && player.getCurrentEquippedItem().stackSize > 0) {
            player.getCurrentEquippedItem().consumeItem(player);
          }
        }
      }
    } else {
      showNotWorking(world, x, y, z, player);
      return false;
    }

    return true;
  }
}
