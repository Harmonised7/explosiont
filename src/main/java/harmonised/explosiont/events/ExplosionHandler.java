package harmonised.explosiont.events;

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
    private static final int healDelayExplosion = Config.config.healDelayExplosion.get();
    private static final double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();

    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        List<BlockInfo> blocks = new ArrayList<>();
        World world = event.getWorld();
        ResourceLocation dimResLoc = world.dimension.getType().getRegistryName();

        if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
            ChunkDataHandler.toHealDimMap.put( dimResLoc, new ArrayList<>() );

        List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( dimResLoc );
        int i = 0;
        List<BlockPos> affectedBlocks = event.getExplosion().getAffectedBlockPositions();
        affectedBlocks.sort( Comparator.comparingInt( Vec3i::getY ) );

        for( BlockPos blockPos : affectedBlocks )
        {
            BlockState blockState = world.getBlockState( blockPos );
            Block block = world.getBlockState( blockPos ).getBlock();

            if( !block.equals( Blocks.AIR ) && !block.equals( Blocks.CAVE_AIR ) && !block.equals( Blocks.FIRE ) && ( world.getBlockState( blockPos ).canDropFromExplosion( world, blockPos, event.getExplosion() ) ) )
            {
                TileEntity tileEntity = world.getTileEntity( blockPos );
                CompoundNBT tileEntityNBT = null;
                if( tileEntity != null )
                    tileEntityNBT = tileEntity.serializeNBT();

                BlockInfo blockInfo = new BlockInfo( dimResLoc, blockState, blockPos, (int) (healDelayExplosion + ticksPerHealExplosion * i), 0, tileEntityNBT );
                blocks.add( blockInfo );
                world.removeTileEntity( blockPos );
                world.removeBlock( blockPos, false );
                i++;
            }
        };

        blocksToHeal.removeAll( blocks );
        blocksToHeal.addAll( blocks );
        blocksToHeal.sort( Comparator.comparingInt( info -> info.pos.getY() ) );

//        WorldTickHandler.explosions.add( WorldTickHandler.explosions.size(), new ExplosionInfo( blocks, 0 ) );
//        System.out.println( event.getExplosion() );

//        event.getExplosion().getAffectedBlockPositions().clear();
    }
}
