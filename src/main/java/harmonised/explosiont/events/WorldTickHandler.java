package harmonised.explosiont.events;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.RegistryHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class WorldTickHandler
{
    private static final Random rand = new Random();
    private static final Map<ResourceLocation, Map<Integer, Double>> dimLastHeal = new HashMap<>();
    private static final Map<ResourceLocation, Set<Integer>> dimForceHeal = new HashMap<>();
    private static final Map<ResourceLocation, Boolean> dimWasDay = new HashMap<>();
    private static final Double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();
    private static final Double ticksPerHealFire = Config.config.ticksPerHealFire.get();
    private static final Integer speedUpTresholdExplosion = Config.config.speedUpTresholdExplosion.get();
    private static final Integer speedUpTresholdFire = Config.config.speedUpTresholdFire.get();
    private static final boolean onlyHealPastMorning = Config.config.onlyHealPastMorning.get();

    public static void handleLevelTick(TickEvent.LevelTickEvent event )
    {
//        event.world.getServer().getPlayerList().getPlayers().forEach( a ->
//        {
//            System.out.println( a.getUniqueID() + " " + a.getName().getString() );
//        });
        Level level = event.level;
        ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc( level );
        if( !dimForceHeal.containsKey( dimResLoc ) )
            dimForceHeal.put( dimResLoc, new HashSet<>() );
        boolean forceHeal;
        if( !dimWasDay.containsKey( dimResLoc ) )
            dimWasDay.put( dimResLoc, isDayTime( level ) );
        if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
            ChunkDataHandler.toHealDimMap.put( dimResLoc, new HashMap<>() );
        for( Map.Entry<Integer, List<BlockInfo>> entry : ChunkDataHandler.toHealDimMap.get( dimResLoc ).entrySet() )
        {
            forceHeal = dimForceHeal.get( dimResLoc ).contains( entry.getKey() );
            List<BlockInfo> blocksToHeal = entry.getValue();

            if( onlyHealPastMorning || forceHeal )
            {
                if( ( !dimWasDay.get( dimResLoc ) && isDayTime( level ) ) || forceHeal )
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
                dimLastHeal.put( dimResLoc, new HashMap<>() );

            healBlocks( level, blocksToHeal, entry.getKey(), forceHeal );
            if( blocksToHeal.size() == 0 )
                dimForceHeal.get( dimResLoc ).remove( entry.getKey() );
        }

        dimWasDay.replace( dimResLoc, isDayTime( level ) );
    }

    private static boolean isDayTime( Level level )
    {
        return level.getServer().getLevel( Level.OVERWORLD ).isDay();
    }

    private static void healBlocks( Level level, List<BlockInfo> blocksToHeal, int type, boolean forceHeal )
    {
        ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc( level );

        if( blocksToHeal.size() > 0 )
        {
            double ticksPerHeal;
            int speedUpTreshold;

            if( type == 0 )
            {
                ticksPerHeal = ticksPerHealExplosion;
                speedUpTreshold = speedUpTresholdExplosion;
            }
            else
            {
                ticksPerHeal = ticksPerHealFire;
                speedUpTreshold = speedUpTresholdFire;
            }

            if( !dimLastHeal.get( dimResLoc ).containsKey( type ) )
                dimLastHeal.get( dimResLoc ).put( type, 0D );

            dimLastHeal.get( dimResLoc ).replace( type, dimLastHeal.get( dimResLoc ).get( type ) + 1 );     //add tick
            int toHeal;
            double cost;

            if( blocksToHeal.size() > speedUpTreshold && speedUpTreshold > 0 )      //get cost, scale if needed
                cost = ticksPerHeal * ( speedUpTreshold / (double) (blocksToHeal.size() ) );
            else
                cost = ticksPerHeal;

            toHeal = (int) ( dimLastHeal.get( dimResLoc ).get( type ) / cost );
            dimLastHeal.get( dimResLoc ).replace( type, dimLastHeal.get( dimResLoc ).get( type ) % cost );  //take away cost for each block

            int index = blocksToHeal.size() - 1;
            BlockInfo blockInfo;
            ChunkPos chunkPos;
            boolean chunkExists;
            int healed = 0;

            while( healed < toHeal || forceHeal )
            {
                if( index >= 0 )
                {
                    blockInfo = blocksToHeal.get( index );
                    chunkPos = new ChunkPos( blockInfo.pos );
                    chunkExists = checkChunkExists( level, chunkPos );

                    if( chunkExists || forceHeal )
                    {
                        if( blockInfo.ticksLeft < 0 || forceHeal )
                        {
                            healBlock( level, blockInfo );
                            blocksToHeal.remove( blockInfo );
                            healed++;
                        }
                    }
                    index--;
                }
                else
                    break;
            }
        }
        else
        {
            dimForceHeal.get( dimResLoc ).remove( type );
            dimLastHeal.get( dimResLoc ).replace( type, 0D );
        }
    }

    private static void healBlock( Level level, BlockInfo blockInfo )
    {
        BlockPos pos = blockInfo.pos;
        Block block = level.getBlockState(pos).getBlock();
        FluidState fluidInfo = level.getFluidState(pos);

        if ( block.equals( Blocks.AIR ) || block.equals( Blocks.CAVE_AIR ) || block.equals( Blocks.FIRE ) || ( !fluidInfo.isEmpty() && !fluidInfo.isSource() ) )
        {
            if( blockInfo.state.hasProperty( GrassBlock.SNOWY ) )
                blockInfo.state = blockInfo.state.setValue( GrassBlock.SNOWY, false );
            if( blockInfo.state.hasProperty( LeavesBlock.DISTANCE ) )
                blockInfo.state = blockInfo.state.setValue( LeavesBlock.DISTANCE, 1 );
            level.setBlock( pos, blockInfo.state, blockInfo.type == 0 ? 3 : 2 | 16 );
            if (blockInfo.BlockEntityNBT != null && blockInfo.BlockEntityNBT.size() > 0)
                level.setBlockEntity( BlockEntity.loadStatic( blockInfo.pos, blockInfo.state, blockInfo.BlockEntityNBT ) );

            level.getEntitiesOfClass( Entity.class, new AABB( pos, pos.above().south().east() ) ).forEach(a ->
            {
                BlockPos entityPos = new BlockPos( a.position() );
                int i = 1;
                while( level.getBlockState( entityPos.above( i ) ).canOcclude() || level.getBlockState( entityPos.above( i + 1 ) ).canOcclude() )
                {
                    i++;
                }
                a.setPos( a.position().x, a.position().y + i, a.position().z );
                level.playSound(null, a.position().x, a.position().y, a.position().z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.8F + rand.nextFloat() * 0.4F, 0.9F + rand.nextFloat() * 0.15F );
            });
        }
        else
        {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(blockInfo.state.getBlock().asItem())));
            if (blockInfo.BlockEntityNBT != null && blockInfo.BlockEntityNBT.contains("Items"))
            {
                ListTag items = (ListTag) blockInfo.BlockEntityNBT.get("Items");
                if (items != null)
                {
                    for (int i = 0; i < items.size(); i++)
                    {
                        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), ItemStack.of(items.getCompound(i))));
                    }
                }
            }
        }

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F);
    }

    private static boolean checkChunkExists( Level level, ChunkPos chunkPos )
    {
        return level.hasChunk( chunkPos.x, chunkPos.z );
    }

    public static void forceAllHeal()
    {
        //Map<ResourceLocation, Map<Integer, List<BlockInfo>>>
        ChunkDataHandler.toHealDimMap.forEach( (key, value) ->
        {
            if( !dimForceHeal.containsKey( key ) )
                dimForceHeal.put( key, new HashSet<>() );

            value.forEach( (key2, value2) ->
            {
                dimForceHeal.get( key ).add( key2 );
            });
        });
    }
}
