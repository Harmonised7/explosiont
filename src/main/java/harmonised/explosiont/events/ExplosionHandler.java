package harmonised.explosiont.events;

import net.minecraftforge.common.util.Constants;
import harmonised.explosiont.config.Config;
import harmonised.explosiont.util.BlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.*;

public class ExplosionHandler
{
    private static final boolean ExplosionHealingEnabled = Config.config.ExplosionHealingEnabled.get();
    private static final int healDelayExplosion = Config.config.healDelayExplosion.get();
    private static final double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();

    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        if( ExplosionHealingEnabled )
        {
            List<BlockInfo> blocks = new ArrayList<>();
            World world = event.getWorld();
            ResourceLocation dimResLoc = world.dimension.getType().getRegistryName();

            if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
                ChunkDataHandler.toHealDimMap.put( dimResLoc, new HashMap<>() );
            if( !ChunkDataHandler.toHealDimMap.get( dimResLoc ).containsKey( 0 ) )
                ChunkDataHandler.toHealDimMap.get( dimResLoc ).put( 0, new ArrayList<>() );

            List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( dimResLoc ).get( 0 );
            int i = 0;
            List<BlockPos> affectedBlocks = event.getAffectedBlocks();
            affectedBlocks.sort( Comparator.comparingInt( BlockPos::getY ) );

            for( BlockPos blockPos : affectedBlocks )
            {
                BlockState blockState = world.getBlockState( blockPos );
                Block block = blockState.getBlock();

                if( !block.equals( Blocks.AIR ) && !block.equals( Blocks.CAVE_AIR ) && !block.equals( Blocks.VOID_AIR ) && !block.equals( Blocks.FIRE ) && ( world.getBlockState( blockPos ).canDropFromExplosion( world, blockPos, event.getExplosion() ) ) )
                {
                    if( block.equals( Blocks.NETHER_PORTAL ) )
                        blockState = Blocks.FIRE.getDefaultState();

                    TileEntity tileEntity = world.getTileEntity( blockPos );
                    CompoundNBT tileEntityNBT = null;
                    if( tileEntity != null )
                        tileEntityNBT = tileEntity.serializeNBT();

                    BlockInfo blockInfo = new BlockInfo( dimResLoc, blockState, blockPos, (int) (healDelayExplosion + ticksPerHealExplosion * i), 0, tileEntityNBT );
                    blocks.add( blockInfo );
                    i++;
                }
            }

            blocks.forEach( info ->     //yes updates
            {
                if( !info.state.isSolid() )
                {
                    world.removeTileEntity( info.pos );
                    world.setBlockState( info.pos, Blocks.AIR.getDefaultState() );
                }
            });

            blocks.forEach( info ->     //yes updates
            {
                if( info.state.isSolid() )
                {
                    world.removeTileEntity( info.pos );
                    world.setBlockState( info.pos, Blocks.AIR.getDefaultState() );
                }
            });

            blocksToHeal.removeAll( blocks );
            blocksToHeal.addAll( blocks );
            blocksToHeal.sort( Comparator.comparingInt( info -> info.pos.getY() ) );
        }
    }
}
