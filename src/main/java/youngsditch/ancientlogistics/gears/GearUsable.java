package youngsditch.ancientlogistics.gears;

import java.util.ArrayList;
import net.minecraft.src.*;

public class GearUsable extends GearBlock {

  public GearUsable(int id) {
    super(id);
  }
  
  public int onGearUsed(World world, int x, int y, int z, EntityPlayer player) {
    return 0;
  }

	public int costToUse(World world, int x, int y, int z, EntityPlayer player) {
    return 0;
  }

  @SuppressWarnings("unchecked")
  public <T extends GearBlock> GearInfo<T>[] getConnected(World world, int x, int y, int z, Class<T> blockType) {
    ArrayList<GearInfo<T>> connected = new ArrayList<>();
    int distance = 0;
    ArrayList<int[]> checked = new ArrayList<>();
    ArrayList<int[]> toCheck = new ArrayList<>();

    toCheck.add(new int[]{x, y, z});

    while (!toCheck.isEmpty()) {
      // first check self
      int[] checkPos = toCheck.remove(0);
      Block block = Block.blocksList[world.getBlockId(checkPos[0], checkPos[1], checkPos[2])];
      if (blockType.isInstance(block) && !isChecked(checked, checkPos[0], checkPos[1], checkPos[2])) {
        checked.add(checkPos);
        distance++;
        connected.add(new GearInfo<T>(checkPos, distance, blockType.cast(block)));
      }

      // then check neighbors
      for (int[] neighbor : NEIGHBORS) {
        int nx = checkPos[0] + neighbor[0];
        int ny = checkPos[1] + neighbor[1];
        int nz = checkPos[2] + neighbor[2];

        if (isGearUsable(world, nx, ny, nz) && !isChecked(checked, nx, ny, nz)) {
          toCheck.add(new int[]{nx, ny, nz});
        }
      }
    }

    return connected.toArray(new GearInfo[connected.size()]);
  }

  private boolean isChecked(ArrayList<int[]> checked, int x, int y, int z) {
      for (int[] pos : checked) {
          if (pos[0] == x && pos[1] == y && pos[2] == z) {
              return true;
          }
      }
      return false;
  }

  private boolean isGearUsable(World world, int x, int y, int z) {
      return Block.blocksList[world.getBlockId(x, y, z)] instanceof GearUsable;
  }
}