package harmonised.explosiont.util;

import harmonised.explosiont.config.Config;
import harmonised.explosiont.events.ChunkDataHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TryCatchFireHandler
{
    private static final boolean FireHealingEnabled = Config.config.FireHealingEnabled.get();
    private static final int healDelayFire = Config.config.healDelayFire.get();

    public static void handle(Level level, BlockPos pos, CallbackInfo info )
    {
        if( FireHealingEnabled )
        {
            final ResourceLocation dimResLoc = Util.getId(level);
            if(!BlackList.checkDimension(dimResLoc))
            {
                return;
            }
            final BlockState state = level.getBlockState( pos );
            if( !BlackList.checkBlock( RegistryHelper.getBlockResLoc(state) ) )
            {
                BlockEntity tileEntity = level.getBlockEntity( pos );

                if (!ChunkDataHandler.toHealDimMap.containsKey(dimResLoc))
                    ChunkDataHandler.toHealDimMap.put(dimResLoc, new ConcurrentHashMap<>());
                if (!ChunkDataHandler.toHealDimMap.get(dimResLoc).containsKey(1))
                    ChunkDataHandler.toHealDimMap.get(dimResLoc).put(1, new ArrayList<>());

                List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get(dimResLoc).get(1);

                blocksToHeal.add(new BlockInfo(dimResLoc, state, pos, healDelayFire, 1, tileEntity == null ? null : tileEntity.serializeNBT()));
                blocksToHeal.sort(Util.blockInfoComparator);

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Reference.SET_BLOCK_TAGS );
            }
        }
    }
}