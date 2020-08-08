package harmonised.explosiont.config;

import harmonised.explosiont.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

public class TagWrappers
{
    public static final BlockTags.Wrapper filter = getBlockTagWrapper("filter" );

    public static BlockTags.Wrapper getBlockTagWrapper( String path )
    {
        return new BlockTags.Wrapper( new ResourceLocation( Reference.MOD_ID, path ) );
    }

    public static boolean isBlockInFilter( Block block )
    {
        return TagWrappers.filter.contains( block );
    }
}