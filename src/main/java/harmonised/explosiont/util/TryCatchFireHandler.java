package harmonised.explosiont.util;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.events.ChunkDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

public class TryCatchFireHandler
{
    private static final boolean FireHealingEnabled = Config.config.FireHealingEnabled.get();
    private static final int healDelayFire = Config.config.healDelayFire.get();

    public static void handle(Level level, BlockPos pos, CallbackInfo info)
    {
        if(FireHealingEnabled)
        {
            BlockState state = level.getBlockState(pos);
            if(!BlackList.checkBlock(state.getBlock().getRegistryName().toString()))
            {
                ResourceLocation dimResLoc = RegistryHelper.getDimensionResLoc(level);
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (!ChunkDataHandler.toHealDimMap.containsKey(dimResLoc))
                    ChunkDataHandler.toHealDimMap.put(dimResLoc, new HashMap<>());
                if (!ChunkDataHandler.toHealDimMap.get(dimResLoc).containsKey(1))
                    ChunkDataHandler.toHealDimMap.get(dimResLoc).put(1, new ArrayList<>());

                List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get(dimResLoc).get(1);

                blocksToHeal.add(new BlockInfo(dimResLoc, state, pos, healDelayFire, 1, blockEntity == null ? null : blockEntity.serializeNBT()));
                blocksToHeal.sort(Comparator.comparingInt(a -> a.pos.getY()));

                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }
}