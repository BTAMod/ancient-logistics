package youngsditch.ancientlogistics.gears;
 
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;

public class GearBlock extends Block {

  protected static final int[][] NEIGHBORS = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

  public GearBlock(String key, int id) {
    super(key, id, Material.metal);
  }
}
