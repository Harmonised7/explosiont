package harmonised.explosiont.events;

import harmonised.explosiont.util.BlackList;
import harmonised.explosiont.util.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import harmonised.explosiont.config.Config;
import harmonised.explosiont.util.BlockInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.*;

public class ExplosionHandler
{
    private static final boolean ExplosionHealingEnabled = Config.config.ExplosionHealingEnabled.get();
    private static final boolean OnlyHealCreepers = Config.config.OnlyHealCreepers.get();
    private static final int healDelayExplosion = Config.config.healDelayExplosion.get();
    private static final double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();

    public static void handleExplosion(ExplosionEvent.Detonate event)
    {
        if(ExplosionHealingEnabled)
        {
            if(OnlyHealCreepers && !(event.getExplosion().getDamageSource().getDirectEntity() instanceof Creeper))
                return;
            List<BlockInfo> blocks = new ArrayList<>();
            Level level = event.getWorld();
            ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc(level);

            if(!ChunkDataHandler.toHealDimMap.containsKey(dimResLoc))
                ChunkDataHandler.toHealDimMap.put(dimResLoc, new HashMap<>());
            if(!ChunkDataHandler.toHealDimMap.get(dimResLoc).containsKey(0))
                ChunkDataHandler.toHealDimMap.get(dimResLoc).put(0, new ArrayList<>());

            List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get(dimResLoc).get(0);
            int i = 0;
            List<BlockPos> affectedBlocks = event.getAffectedBlocks();
            affectedBlocks.sort(Comparator.comparingInt(BlockPos::getY));

            for(BlockPos blockPos : affectedBlocks)
            {
                BlockState blockState = level.getBlockState(blockPos);
                Block block = blockState.getBlock();

                if(BlackList.checkBlock(block.getRegistryName().toString()) && level.getBlockState(blockPos).canDropFromExplosion(level, blockPos, event.getExplosion()))
                {
                    if(block.equals(Blocks.NETHER_PORTAL))
                        blockState = Blocks.FIRE.defaultBlockState();

                    BlockEntity blockEntity = level.getBlockEntity(blockPos);
                    CompoundTag tileEntityNBT = null;
                    if(blockEntity != null)
                        tileEntityNBT = blockEntity.serializeNBT();

                    BlockInfo blockInfo = new BlockInfo(dimResLoc, blockState, blockPos, (int) (healDelayExplosion + ticksPerHealExplosion * i), 0, tileEntityNBT);
                    blocks.add(blockInfo);
                    i++;
                }
            }

            blocks.forEach(info ->     //yes updates
            {
                if(!info.state.canOcclude())
                {
                    level.removeBlockEntity(info.pos);
                    level.setBlockAndUpdate(info.pos, Blocks.AIR.defaultBlockState());
                }
            });

            blocks.forEach(info ->     //yes updates
            {
                if(info.state.canOcclude())
                {
                    level.removeBlockEntity(info.pos);
                    level.setBlockAndUpdate(info.pos, Blocks.AIR.defaultBlockState());
                }
            });

            blocksToHeal.removeAll(blocks);
            blocksToHeal.addAll(blocks);
            blocksToHeal.sort(Comparator.comparingInt(info -> info.pos.getY()));
        }
    }
}
