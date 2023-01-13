package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ChunkDataEvent;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

import harmonised.explosiont.util.Util;

public class ChunkDataHandler
{
    public static Map<ResourceLocation, Map<Integer, List<BlockInfo>>> toHealDimMap = new HashMap<>();

    public static void init()
    {
        toHealDimMap = new HashMap<>();
    }
    
    public static void handleChunkDataLoad( ChunkDataEvent.Load event )
    {
        final CompoundTag chunkNBT = event.getData();
        if( chunkNBT != null )
        {
//            CompoundTag levelNBT = chunkNBT.getCompound( "Level" );
            final CompoundTag levelNBT = chunkNBT;
            if( levelNBT.contains( "blocksToHeal" ) )
            {
                final Level level = (Level) event.getLevel();
                final ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc( level );
                if( !toHealDimMap.containsKey( dimResLoc ) )
                    toHealDimMap.put( dimResLoc, new HashMap<>() );
                CompoundTag blocksToHealNBT = ( (CompoundTag) levelNBT.get( "blocksToHeal" ) );
                if( blocksToHealNBT == null )
                    return;
                final Map<Integer, List<BlockInfo>> blocksToAddTypes = new HashMap<>();
                final Set<String> keySet = blocksToHealNBT.getAllKeys();

                keySet.forEach( key ->
                {
                    CompoundTag entry = blocksToHealNBT.getCompound( key );

                    if( !blocksToAddTypes.containsKey( entry.getInt( "type" ) ) )
                        blocksToAddTypes.put( entry.getInt( "type" ), new ArrayList<>() );
                    blocksToAddTypes.get( entry.getInt( "type" ) ).add( new BlockInfo( dimResLoc, NbtUtils.readBlockState( level.holderLookup(Registries.BLOCK), entry.getCompound( "state" ) ), NbtUtils.readBlockPos( entry.getCompound( "pos" ) ), entry.getInt( "ticksLeft" ), entry.getInt( "type" ), entry.getCompound( "BlockEntity" ) ) );
                });

                List<BlockInfo> blocksToHeal;

                for( Map.Entry<Integer, List<BlockInfo>> entry : blocksToAddTypes.entrySet() )
                {
                    if( !toHealDimMap.get( dimResLoc ).containsKey( entry.getKey() ) )
                        toHealDimMap.get( dimResLoc ).put( entry.getKey(), new ArrayList<>() );
                    blocksToHeal = toHealDimMap.get( dimResLoc ).get( entry.getKey() );
                    blocksToHeal.removeAll( entry.getValue() );
                    blocksToHeal.addAll( entry.getValue() );
                    blocksToHeal.sort(Util.blockInfoComparator);
                }
            }
        }
    }

    public static void handleChunkDataSave( ChunkDataEvent.Save event )
    {
        final Level level = (Level) event.getLevel();
        final ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc( level );

        if( toHealDimMap.containsKey( dimResLoc ) )
        {
            final Map<Integer, List<BlockInfo>> toHealTypeMap = toHealDimMap.get( dimResLoc );

//            CompoundTag levelNBT = (CompoundTag) event.getData().get( "Level" );
//            if( levelNBT == null )
//                return;
            final CompoundTag levelNBT = event.getData();

            final List<BlockInfo> chunkBlocksToHeal = new ArrayList<>();
            final ChunkPos chunkPos = event.getChunk().getPos();

            for( Map.Entry<Integer, List<BlockInfo>> entry : toHealTypeMap.entrySet() )
            {
                List<BlockInfo> blocksToHeal = entry.getValue();

                for( BlockInfo blockInfo : blocksToHeal )
                {
                    if( new ChunkPos( blockInfo.pos ).equals( chunkPos ) )
                        chunkBlocksToHeal.add( blockInfo );
                }
            }

            if( chunkBlocksToHeal.size() <= 0 )
                return;

            final CompoundTag newBlocksToHealNBT = new CompoundTag();
            CompoundTag insidesNBT;

            int i = 0;

            for( BlockInfo blockInfo : chunkBlocksToHeal )
            {
                insidesNBT = new CompoundTag();
                insidesNBT.put( "pos", NbtUtils.writeBlockPos( blockInfo.pos ) );
                insidesNBT.put( "state", NbtUtils.writeBlockState( blockInfo.state ) );
                insidesNBT.putInt( "ticksLeft", blockInfo.ticksLeft );
                insidesNBT.putInt( "type", blockInfo.type );
                if( blockInfo.BlockEntityNBT != null )
                    insidesNBT.put( "BlockEntity", blockInfo.BlockEntityNBT );
                newBlocksToHealNBT.put( i++ + "", insidesNBT );
                if( !event.getLevel().hasChunk( chunkPos.x, chunkPos.z ) )
                    toHealTypeMap.get( blockInfo.type ).remove( blockInfo );
            }

            levelNBT.put( "blocksToHeal", newBlocksToHealNBT );
        }
    }
}
