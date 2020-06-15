package harmonised.explosiont.util;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.events.ChunkDataHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TryCatchFireHandler
{
    private static final int healDelayFire = Config.config.healDelayFire.get();

    public static void handle(World world, BlockPos pos, CallbackInfo info )
    {
        ResourceLocation dimResLoc = world.dimension.getType().getRegistryName();
        TileEntity tileEntity = world.getTileEntity( pos );

        if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
            ChunkDataHandler.toHealDimMap.put( dimResLoc, new HashMap<>() );
        if( !ChunkDataHandler.toHealDimMap.get( dimResLoc ).containsKey( 1 ) )
            ChunkDataHandler.toHealDimMap.get( dimResLoc ).put( 1, new ArrayList<>() );

        List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( dimResLoc ).get( 1 );

        blocksToHeal.add( new BlockInfo( dimResLoc, world.getBlockState( pos ), pos, healDelayFire, 1, tileEntity == null ? null : tileEntity.serializeNBT() ) );
        blocksToHeal.sort( Comparator.comparingInt( a -> a.pos.getY() ) );
    }
}