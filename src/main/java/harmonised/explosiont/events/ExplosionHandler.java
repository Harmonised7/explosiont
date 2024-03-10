package harmonised.explosiont.events;

import harmonised.explosiont.util.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.block.Block;
import harmonised.explosiont.config.Config;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExplosionHandler
{
    private static final boolean ExplosionHealingEnabled = Config.config.ExplosionHealingEnabled.get();
    private static final boolean OnlyHealCreepers = Config.config.OnlyHealCreepers.get();
    private static final int healDelayExplosion = Config.config.healDelayExplosion.get();
    private static final double ticksPerHealExplosion = Config.config.ticksPerHealExplosion.get();

    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        if( ExplosionHealingEnabled )
        {
            final ResourceLocation dimResLoc = Util.getId(event.getLevel());
            if(!BlackList.checkDimension(dimResLoc))
            {
                return;
            }
            if( OnlyHealCreepers && !( event.getExplosion().getDamageSource().getEntity() instanceof Creeper) )
                return;
            final List<BlockInfo> blocks = new ArrayList<>();
            final Level level = event.getLevel();

            final Map<Integer, List<BlockInfo>> dimMap = ChunkDataHandler.toHealDimMap
                                                            .computeIfAbsent(dimResLoc, key -> new ConcurrentHashMap<>());
            final List<BlockInfo> blocksToHeal = dimMap.containsKey(0) ?
                    new ArrayList<>(dimMap.get(0)):
                    new ArrayList<>();
            int i = 0;
            final List<BlockPos> affectedBlocks = event.getAffectedBlocks();
            affectedBlocks.sort(Util.blockPosComparator);

            for( BlockPos blockPos : affectedBlocks )
            {
                BlockState blockState = level.getBlockState( blockPos );
                final Block block = blockState.getBlock();

                if( BlackList.checkBlock( RegistryHelper.getBlockResLoc(block) ) && level.getBlockState( blockPos ).canDropFromExplosion( level, blockPos, event.getExplosion() ) )
                {
                    if( block.equals( Blocks.NETHER_PORTAL ) )
                        blockState = Blocks.FIRE.defaultBlockState();

                    BlockEntity BlockEntity = level.getBlockEntity( blockPos );
                    CompoundTag BlockEntityNBT = null;
                    if( BlockEntity != null )
                        BlockEntityNBT = BlockEntity.serializeNBT();

                    BlockInfo blockInfo = new BlockInfo( dimResLoc, blockState, blockPos, (int) (healDelayExplosion + ticksPerHealExplosion * i), 0, BlockEntityNBT );
                    blocks.add( blockInfo );
                    i++;
                }
            }

            blocks.forEach( info ->     //yes updates
            {
                if( !info.state.canOcclude() )
                {
                    level.removeBlockEntity( info.pos );
                    level.setBlock( info.pos, Blocks.AIR.defaultBlockState(), Reference.SET_BLOCK_TAGS );
                }
            });

            blocks.forEach( info ->     //yes updates
            {
                if( info.state.canOcclude() )
                {
                    level.removeBlockEntity( info.pos );
                    level.setBlock( info.pos, Blocks.AIR.defaultBlockState(), Reference.SET_BLOCK_TAGS );
                }
            });

            blocksToHeal.addAll( blocks );
            blocksToHeal.sort(Util.blockInfoComparator);
            dimMap.put(0, blocksToHeal);
        }
    }
}
