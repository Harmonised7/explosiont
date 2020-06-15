package harmonised.explosiont.events;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.util.BlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class WorldTickHandler
{
    private static final Random rand = new Random();
    private static final Map<ResourceLocation, Double> dimLastHeal = new HashMap<>();
    private static final Set<ResourceLocation> dimForceHeal = new HashSet<>();
    private static final Double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();
    private static final Double ticksPerHealFire = Config.config.ticksPerHealFire.get();
    private static final Integer speedUpTresholdExplosion = Config.config.speedUpTresholdExplosion.get();
    private static final Integer speedUpTresholdFire = Config.config.speedUpTresholdFire.get();
    private static final boolean onlyHealPastMorning = Config.config.onlyHealPastMorning.get();
    private static boolean wasDay = false;

    public static void handleWorldTick( TickEvent.WorldTickEvent event )
    {
        ResourceLocation dimResLoc = ( event.world.dimension.getType().getRegistryName() );
        World world = event.world;
        boolean forceHeal = dimForceHeal.contains( dimResLoc );

        if( !ChunkDataHandler.toHealDimMap.containsKey( world.dimension.getType().getRegistryName() ) )
            ChunkDataHandler.toHealDimMap.put( world.dimension.getType().getRegistryName(), new ArrayList<>() );
        List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( world.dimension.getType().getRegistryName() );

        if( onlyHealPastMorning || forceHeal )
        {
            if( !wasDay && world.isDaytime() || forceHeal )
            {
                blocksToHeal.forEach( blockToHeal ->
                {
                    blockToHeal.ticksLeft = -1;
                });
            }
        }
        else
        {
            blocksToHeal.forEach( blockToHeal ->
            {
                blockToHeal.ticksLeft--;
            });
        }

        if( !dimLastHeal.containsKey( dimResLoc ) )
            dimLastHeal.put( dimResLoc, 0D );

        if( blocksToHeal.size() > 0 )
        {
            dimLastHeal.replace( dimResLoc, dimLastHeal.get( dimResLoc ) + 1 );     //add tick
            int toHealExplosion, toHealFire;

            if( forceHeal )
            {
                toHealExplosion = blocksToHeal.size();
                toHealFire = blocksToHeal.size();
            }
            else
            {
                double costExplosion;
                double costFire;

                if( blocksToHeal.size() > speedUpTresholdExplosion && speedUpTresholdExplosion > 0 )      //get cost, scale if needed
                    costExplosion = ticksPerHealExplosion * ( speedUpTresholdExplosion / (double) (blocksToHeal.size() ) );
                else
                    costExplosion = ticksPerHealExplosion;

                toHealExplosion = (int) ( dimLastHeal.get( dimResLoc ) / costExplosion );
                dimLastHeal.replace( dimResLoc, dimLastHeal.get( dimResLoc ) % costExplosion );  //take away cost for each block

                if( blocksToHeal.size() > speedUpTresholdFire && speedUpTresholdFire > 0 )      //get cost, scale if needed
                    costFire = ticksPerHealFire * ( speedUpTresholdFire / (double) (blocksToHeal.size() ) );
                else
                    costFire = ticksPerHealFire;

                toHealFire = (int) ( dimLastHeal.get( dimResLoc ) / costFire );
                dimLastHeal.replace( dimResLoc, dimLastHeal.get( dimResLoc ) % costFire );  //take away cost for each block
            }

            int index = -1;
            BlockInfo blockInfo;
            ChunkPos chunkPos;
            boolean chunkExists;
            int healedExplosion = 0, healedFire = 0;

            while( healedExplosion < toHealExplosion || healedFire < toHealFire )
            {
                if( blocksToHeal.size() > ++index )
                {
                    blockInfo = blocksToHeal.get( index );
                    chunkPos = new ChunkPos( blockInfo.pos );
                    chunkExists = checkChunkExists( world, chunkPos ) || forceHeal;

                    if( chunkExists )
                    {
                        if( blockInfo.ticksLeft < 0 )
                        {
                            System.out.println( blocksToHeal.size() );
                            if( blockInfo.type == 0 )
                            {
                                if( healedExplosion < toHealExplosion )
                                {
                                    processBlock( world, blockInfo );
                                    blocksToHeal.remove( blockInfo );
                                    healedExplosion++;
                                }
                            }
                            else
                            {
                                if( healedFire < toHealFire )
                                {
                                    processBlock( world, blockInfo );
                                    blocksToHeal.remove( blockInfo );
                                    healedFire++;
                                }
                            }
                        }
                    }
                }
                else
                    break;
            }
        }
        else
        {
            dimLastHeal.replace( dimResLoc, 0D );
            dimForceHeal.remove( dimResLoc );
        }

        wasDay = world.isDaytime();
    }

    private static void processBlock( World world, BlockInfo blockInfo )
    {
        BlockPos pos = blockInfo.pos;
        Block block = world.getBlockState(pos).getBlock();
        IFluidState fluidInfo = world.getFluidState(pos);

        if ( block.equals( Blocks.AIR ) || block.equals( Blocks.CAVE_AIR ) || block.equals( Blocks.FIRE ) || ( !fluidInfo.isEmpty() && !fluidInfo.isSource() ) )
        {
            if( blockInfo.state.has( GrassBlock.SNOWY ) )
                blockInfo.state = blockInfo.state.with( GrassBlock.SNOWY, false );
            world.setBlockState( pos, blockInfo.state.getBlock().getDefaultState(), blockInfo.type == 0 ? 3 : 2 | 16 );
            if (blockInfo.tileEntityNBT != null && blockInfo.tileEntityNBT.size() > 0)
                world.setTileEntity(pos, TileEntity.create(blockInfo.tileEntityNBT));
//                    blockInfo.state.updateNeighbors( world, pos, 0 );

            world.getEntitiesWithinAABB( Entity.class, new AxisAlignedBB( pos, pos.up().south().east() ) ).forEach( a ->
            {
                BlockPos entityPos = a.getPosition();
                int i = 1;
                while( world.getBlockState( entityPos.up( i ) ).isSolid() || world.getBlockState( entityPos.up( i + 1 ) ).isSolid() )
                {
                    i++;
                }
                a.setPosition( a.getPositionVector().getX(), a.getPositionVector().y + i, a.getPositionVector().z );
                world.playSound(null, a.getPositionVector().getX(), a.getPositionVector().getY(), a.getPositionVector().getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.8F + rand.nextFloat() * 0.4F, 0.9F + rand.nextFloat() * 0.15F );
            });
        }
        else
        {
            Block.spawnAsEntity(world, pos, new ItemStack(blockInfo.state.getBlock().asItem()));
            if (blockInfo.tileEntityNBT != null && blockInfo.tileEntityNBT.contains("Items"))
            {
                ListNBT items = (ListNBT) blockInfo.tileEntityNBT.get("Items");
                if (items != null)
                {
                    for (int i = 0; i < items.size(); i++)
                    {
                        Block.spawnAsEntity(world, pos, ItemStack.read(items.getCompound(i)));
                    }
                }
            }
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F);
    }

    private static boolean checkChunkExists( World world, ChunkPos chunkPos )
    {
        return world.chunkExists( chunkPos.x, chunkPos.z );
    }

    public static void forceAllHeal()
    {
        ChunkDataHandler.toHealDimMap.forEach( (key, value) ->
        {
            dimForceHeal.add( key );
        });
    }
}
