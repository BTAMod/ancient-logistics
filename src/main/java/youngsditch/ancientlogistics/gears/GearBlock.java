package youngsditch.ancientlogistics.gears;

import net.minecraft.core.world.World;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;

public class GearBlock extends Block {
  protected static final int[][] NEIGHBORS = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

  public GearBlock(String key, int id) {
    super(key, id, Material.wood);
  }

  @Override
  public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
    return new ItemStack[] { new ItemStack(this) };
  }
}
