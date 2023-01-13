package harmonised.explosiont.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class BlockInfo
{
    public ResourceLocation dimResLoc;
    public BlockState state;
    public BlockPos pos;
    public CompoundTag BlockEntityNBT;
    public int ticksLeft;
    public int type;
    public boolean forceHeal = false;

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
