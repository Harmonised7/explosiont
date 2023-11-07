package harmonised.explosiont.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class BlockInfo
{
    public final ResourceLocation dimResLoc;
    public BlockState state;
    public final BlockPos pos;
    public final CompoundTag BlockEntityNBT;
    public int ticksLeft;
    public final int type;
    public final boolean forceHeal = false;

    public BlockInfo( ResourceLocation dimResLoc, BlockState state, BlockPos blockPos, int ticksLeft, int type, @Nullable CompoundTag BlockEntityNBT )
    {
        this.dimResLoc = dimResLoc;
        this.state = state;
        this.pos = blockPos;
        this.ticksLeft = ticksLeft;
        this.type = type;
        this.BlockEntityNBT = BlockEntityNBT;
    }
}
