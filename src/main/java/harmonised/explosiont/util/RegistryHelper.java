package harmonised.explosiont.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class RegistryHelper
{
    public static ResourceLocation getBiomeResLoc(World world, Biome biome )
    {
        return world.getRegistryManager().get( Registry.BIOME_KEY ).getKey( biome );
    }

    public static ResourceLocation getDimensionResLoc( World world, DimensionType dimensionType )
    {
        return world.getRegistryManager().get( Registry.DIMENSION_TYPE_KEY ).getKey( dimensionType );
    }
}
