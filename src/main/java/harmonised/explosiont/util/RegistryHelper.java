package harmonised.explosiont.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RegistryHelper
{
//    public static ResourceLocation getBiomeResLoc( Biome biome )
//    {
//        return biome.
//    }

    public static ResourceLocation getDimensionResLoc( Level level )
    {
        return level.dimension().location();
    }

    public static ResourceLocation getBlockResLoc(BlockState state)
    {
        return getBlockResLoc(state.getBlock());
    }

    public static ResourceLocation getBlockResLoc(Block block)
    {
        return block.builtInRegistryHolder().key().location();
    }
}
