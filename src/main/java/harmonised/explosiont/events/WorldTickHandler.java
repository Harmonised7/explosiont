package harmonised.explosiont.events;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.util.BlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class WorldTickHandler
{
    private static double healDelay = Config.config.healDelay.get();
    private static double eachHealDelay = Config.config.eachHealDelay.get();
    public static long lastHeal = System.currentTimeMillis();

    public static void handleWorldTick( TickEvent.WorldTickEvent event )
    {
        if( System.currentTimeMillis() - lastHeal > eachHealDelay )
        {
            World world = event.world;
            healDelay = 1000;

            if( !ChunkDataHandler.toHealDimMap.containsKey( world.dimension.getType().getRegistryName() ) )
                ChunkDataHandler.toHealDimMap.put( world.dimension.getType().getRegistryName(), new ArrayList<>() );

            List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( world.dimension.getType().getRegistryName() );

            if( blocksToHeal.size() > 0 )
            {
                int index = 0;
                BlockInfo blockInfo = blocksToHeal.get( index );
                ChunkPos chunkPos = new ChunkPos( blockInfo.pos );

                while( !world.chunkExists( chunkPos.x, chunkPos.z ) )
                {
                    if( blocksToHeal.size() > ++index )
                    {
                        blockInfo = blocksToHeal.get( index );
                        chunkPos = new ChunkPos( blockInfo.pos );
                    }
                }

                if( !world.chunkExists( chunkPos.x, chunkPos.z ) )
                    return;

                if( System.currentTimeMillis() - healDelay > blockInfo.time )
                {
                    Block block = world.getBlockState( blockInfo.pos ).getBlock();

                    IFluidState fluidInfo = world.getFluidState( blockInfo.pos );

                    if( block.equals( Blocks.AIR ) || ( fluidInfo.isEmpty() || !fluidInfo.isSource() ) )
                    {
                        world.setBlockState( blockInfo.pos, blockInfo.state );
                        if( blockInfo.tileEntityNBT != null && blockInfo.tileEntityNBT.size() > 0 )
                            world.setTileEntity( blockInfo.pos, TileEntity.create( blockInfo.tileEntityNBT ) );
//                        blockInfo.state.updateNeighbors( world, blockInfo.pos, 0 );

                        System.out.println( blockInfo.pos );

                        Random rand = new Random();
                        world.playSound( null, blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F );
                    }
                    else
                    {
                        Block.spawnAsEntity( world, blockInfo.pos, new ItemStack( blockInfo.state.getBlock().asItem() ) );
                        if( blockInfo.tileEntityNBT != null && blockInfo.tileEntityNBT.contains( "Items" ) )
                        {
                            ListNBT items = (ListNBT) blockInfo.tileEntityNBT.get( "Items" );
                            if( items != null )
                            {
                                for( int i = 0; i < items.size(); i++ )
                                {
                                    Block.spawnAsEntity( world, blockInfo.pos, ItemStack.read( items.getCompound( i ) ) );
                                }
                            }
                        }
                    }

                    blocksToHeal.remove( 0 );
                }
            }

            lastHeal = System.currentTimeMillis();
        }
    }
}
