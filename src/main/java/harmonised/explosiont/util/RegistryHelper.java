package harmonised.explosiont.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class RegistryHelper
{
    public static ResourceLocation getBiomeResLoc( Biome biome )
    {
        return biome.getRegistryName();
    }

    public static ResourceLocation getDimensionResLoc( World world )
    {
        return world.getDimensionKey().getRegistryName();
    }
}
