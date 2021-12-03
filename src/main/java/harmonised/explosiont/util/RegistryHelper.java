package harmonised.explosiont.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class RegistryHelper
{
    public static ResourceLocation getBiomeResLoc(Biome biome)
    {
        return biome.getRegistryName();
    }

    public static ResourceLocation getDimensionResLoc(Level level)
    {
        return level.dimension().location();
    }
}
