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
    private static final int healDelayExplosion = Config.config.healDelayExplosion.get();
    private static final double ticksPerHeal = Config.config.ticksPerHeal.get();

    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        List<BlockInfo> blocks = new ArrayList<>();
        World world = event.getWorld();
        ResourceLocation dimResLoc = world.dimension.getType().getRegistryName();

        if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
            ChunkDataHandler.toHealDimMap.put( dimResLoc, new ArrayList<>() );

        List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( dimResLoc );
        int i = 0;
        List<BlockPos> affectedBlocks = event.getAffectedBlocks();
        affectedBlocks.sort( Comparator.comparingInt( BlockPos::getY ) );

//        for( BlockPos pos : affectedBlocks )
//        {
//            if( world.getBlockState( pos ).getBlock().equals( Blocks.SNOW ) )
//                System.out.println( "snow" );
//        }

        for( BlockPos blockPos : affectedBlocks )
        {
            BlockState blockState = world.getBlockState( blockPos );
            Block block = blockState.getBlock();

            if( !block.equals( Blocks.AIR ) && !block.equals( Blocks.CAVE_AIR ) && !block.equals( Blocks.VOID_AIR ) && !block.equals( Blocks.FIRE ) && ( world.getBlockState( blockPos ).canDropFromExplosion( world, blockPos, event.getExplosion() ) ) )
            {
                TileEntity tileEntity = world.getTileEntity( blockPos );
                CompoundNBT tileEntityNBT = null;
                if( tileEntity != null )
                    tileEntityNBT = tileEntity.serializeNBT();

                BlockInfo blockInfo = new BlockInfo( dimResLoc, blockState, blockPos, (int) (healDelayExplosion + ticksPerHeal * i), 0, tileEntityNBT );
                blocks.add( blockInfo );
                i++;
            }
        }

        blocks.forEach( info ->
        {
            if( !info.state.isSolid() )
            {
                world.removeTileEntity( info.pos );
                world.setBlockState( info.pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.IS_MOVING );
            }
        });

//        blocks.sort( Comparator.comparingInt( a -> ( (BlockInfo) a ).pos.getY() ).reversed() );
        blocks.forEach( info ->
        {
            world.removeTileEntity( info.pos );
            world.setBlockState( info.pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.IS_MOVING );
        });
//        blocks.sort( Comparator.comparingInt( a -> a.pos.getY() ) );

        
        blocksToHeal.removeAll( blocks );
        blocksToHeal.addAll( blocks );
        blocksToHeal.sort( Comparator.comparingInt( info -> info.pos.getY() ) );

//        WorldTickHandler.explosions.add( WorldTickHandler.explosions.size(), new ExplosionInfo( blocks, 0 ) );
//        System.out.println( event.getExplosion() );

//        event.getExplosion().getAffectedBlockPositions().clear();
    }
}
