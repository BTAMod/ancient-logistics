package youngsditch.ancientlogistics.gears;
 
import net.minecraft.src.*;

public class GearBlock extends Block {

  protected static final int[][] NEIGHBORS = {{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

  public GearBlock(int id) {
    super(id, Material.iron);
  }
}
