package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.*;

public class ChunkDataHandler
{
    public static Map<ResourceLocation, List<BlockInfo>> toHealDimMap = new HashMap<>();

    public static void init()
    {
        toHealDimMap = new HashMap<>();
    }
    
    public static void handleChunkDataLoad( ChunkDataEvent.Load event )
    {
        CompoundNBT levelNBT = event.getData();
        if( levelNBT != null )
        {
            if( levelNBT.contains( "blocksToHeal" ) )
            {
                ResourceLocation dimResLoc = event.getWorld().getDimension().getType().getRegistryName();
                if( !toHealDimMap.containsKey( dimResLoc ) )
                    toHealDimMap.put( dimResLoc, new ArrayList<>() );
                List<BlockInfo> blocksToHeal = toHealDimMap.get( dimResLoc );

                CompoundNBT blocksToHealNBT = ( (CompoundNBT) levelNBT.get( "blocksToHeal" ) );
                if( blocksToHealNBT == null )
                    return;
                Set<String> keySet = blocksToHealNBT.keySet();

                keySet.forEach( key ->
                {
                    CompoundNBT entry = blocksToHealNBT.getCompound( key );
                    blocksToHeal.add( new BlockInfo( dimResLoc, NBTUtil.readBlockState( entry.getCompound( "state" ) ), NBTUtil.readBlockPos( entry.getCompound( "pos" ) ), entry.getLong( "time" ), entry.getCompound( "tileEntity" ) ) );
                });
            }
        }
    }

    public static void handleChunkDataSave( ChunkDataEvent.Save event )
    {
        ResourceLocation dimResLoc = event.getWorld().getDimension().getType().getRegistryName();

        if( toHealDimMap.containsKey( dimResLoc ) )
        {
            List<BlockInfo> blocksToHeal = toHealDimMap.get( dimResLoc );
            List<BlockInfo> chunkBlocksToHeal = new ArrayList<>();
            ChunkPos chunkPos = event.getChunk().getPos();
            
            CompoundNBT levelNBT = (CompoundNBT) event.getData().get( "Level" );
            if( levelNBT == null )
                return;

            for( BlockInfo blockInfo : blocksToHeal )
            {
                if( new ChunkPos( blockInfo.pos ).equals( chunkPos ) )
                    chunkBlocksToHeal.add( blockInfo );
            }

            if( chunkBlocksToHeal.size() <= 0 )
                return;

            CompoundNBT newBlocksToHealNBT = new CompoundNBT();
            CompoundNBT insidesNBT;

            int i = 0;

            for( BlockInfo blockInfo : chunkBlocksToHeal )
            {
                insidesNBT = new CompoundNBT();
                insidesNBT.put( "pos", NBTUtil.writeBlockPos( blockInfo.pos ) );
                insidesNBT.put( "state", NBTUtil.writeBlockState( blockInfo.state ) );
                if( blockInfo.tileEntityNBT != null )
                    insidesNBT.put( "tileEntity", blockInfo.tileEntityNBT );
                newBlocksToHealNBT.put( i++ + "", insidesNBT );
                if( !event.getWorld().chunkExists( chunkPos.x, chunkPos.z ) )
                    blocksToHeal.remove( blockInfo );
            }

            levelNBT.put( "blocksToHeal", newBlocksToHealNBT );
        }
    }
}
