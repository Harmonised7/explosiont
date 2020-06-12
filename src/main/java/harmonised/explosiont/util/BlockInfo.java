package harmonised.explosiont.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class BlockInfo
{
    public ResourceLocation dimResLoc;
    public BlockState state;
    public BlockPos pos;
    public CompoundNBT tileEntityNBT;
    public int ticksLeft;
    public int type;

    public BlockInfo( ResourceLocation dimResLoc, BlockState state, BlockPos blockPos, int ticksLeft, int type, @Nullable CompoundNBT tileEntityNBT )
    {
        this.dimResLoc = dimResLoc;
        this.state = state;
        this.pos = blockPos;
        this.ticksLeft = ticksLeft;
        this.type = type;
        this.tileEntityNBT = tileEntityNBT;
    }
}
