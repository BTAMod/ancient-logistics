package youngsditch.ancientlogistics.mixin;

import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = TileEntityTrommel.class, remap = false)
public interface TrommelAccessor {
  @Accessor("itemStacks")
  public ItemStack[] getItemStacks();

  @Accessor("nextToSieve")
  public int getNextToSieve();

  @Accessor("nextToSieve")
  public void setNextToSieve(int nextToSieve);

  @Invoker("canProduce")
  public boolean invokeCanProduce(int nextToSieve);
}
