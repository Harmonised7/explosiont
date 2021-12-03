package harmonised.explosiont.events;

import harmonised.explosiont.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.*;

public class ChunkDataHandler
{
    public static Map<ResourceLocation, Map<Integer, List<BlockInfo>>> toHealDimMap = new HashMap<>();

    public static void init()
    {
        toHealDimMap = new HashMap<>();
    }
    
    public static void handleChunkDataLoad(ChunkDataEvent.Load event)
    {
        CompoundTag chunkNBT = event.getData();
        if(chunkNBT != null)
        {
            CompoundTag levelNBT = chunkNBT.getCompound("Level");
            if(levelNBT.contains("blocksToHeal"))
            {
                Level level = (Level) event.getWorld();
                ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc(level);
                if(!toHealDimMap.containsKey(dimResLoc))
                    toHealDimMap.put(dimResLoc, new HashMap<>());
                CompoundTag blocksToHealNBT = ((CompoundTag) levelNBT.get("blocksToHeal"));
                if(blocksToHealNBT == null)
                    return;
                Map<Integer, List<BlockInfo>> blocksToAddTypes = new HashMap<>();
                Set<String> keySet = blocksToHealNBT.getAllKeys();

                keySet.forEach(key ->
                {
                    CompoundTag entry = blocksToHealNBT.getCompound(key);

                    if(!blocksToAddTypes.containsKey(entry.getInt("type")))
                        blocksToAddTypes.put(entry.getInt("type"), new ArrayList<>());
                    blocksToAddTypes.get(entry.getInt("type")).add(new BlockInfo(dimResLoc, NbtUtils.readBlockState(entry.getCompound("state")), NbtUtils.readBlockPos(entry.getCompound("pos")), entry.getInt("ticksLeft"), entry.getInt("type"), entry.getCompound("blockEntity")));
                });

                List<BlockInfo> blocksToHeal;

                for(Map.Entry<Integer, List<BlockInfo>> entry : blocksToAddTypes.entrySet())
                {
                    if(!toHealDimMap.get(dimResLoc).containsKey(entry.getKey()))
                        toHealDimMap.get(dimResLoc).put(entry.getKey(), new ArrayList<>());
                    blocksToHeal = toHealDimMap.get(dimResLoc).get(entry.getKey());
                    blocksToHeal.removeAll(entry.getValue());
                    blocksToHeal.addAll(entry.getValue());
                    blocksToHeal.sort(Comparator.comparingInt(blockInfo -> blockInfo.pos.getY()));
                }
            }
        }
    }

    public static void handleChunkDataSave(ChunkDataEvent.Save event)
    {
        Level level = (Level) event.getWorld();
        ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc(level);

        if(toHealDimMap.containsKey(dimResLoc))
        {
            Map<Integer, List<BlockInfo>> toHealTypeMap = toHealDimMap.get(dimResLoc);

            CompoundTag levelNBT = (CompoundTag) event.getData().get("Level");
            if(levelNBT == null)
                return;

            List<BlockInfo> chunkBlocksToHeal = new ArrayList<>();
            ChunkPos chunkPos = event.getChunk().getPos();

            for(Map.Entry<Integer, List<BlockInfo>> entry : toHealTypeMap.entrySet())
            {
                List<BlockInfo> blocksToHeal = entry.getValue();

                for(BlockInfo blockInfo : blocksToHeal)
                {
                    if(new ChunkPos(blockInfo.pos).equals(chunkPos))
                        chunkBlocksToHeal.add(blockInfo);
                }
            }

            if(chunkBlocksToHeal.size() <= 0)
                return;

            CompoundTag newBlocksToHealNBT = new CompoundTag();
            CompoundTag insidesNBT;

            int i = 0;

            for(BlockInfo blockInfo : chunkBlocksToHeal)
            {
                insidesNBT = new CompoundTag();
                insidesNBT.put("pos", NbtUtils.writeBlockPos(blockInfo.pos));
                insidesNBT.put("state", NbtUtils.writeBlockState(blockInfo.state));
                insidesNBT.putInt("ticksLeft", blockInfo.ticksLeft);
                insidesNBT.putInt("type", blockInfo.type);
                if(blockInfo.tileEntityNBT != null)
                    insidesNBT.put("blockEntity", blockInfo.tileEntityNBT);
                newBlocksToHealNBT.put(i++ + "", insidesNBT);
                if(!event.getWorld().hasChunk(chunkPos.x, chunkPos.z))
                    toHealTypeMap.get(blockInfo.type).remove(blockInfo);
            }

            levelNBT.put("blocksToHeal", newBlocksToHealNBT);
        }
    }
}
