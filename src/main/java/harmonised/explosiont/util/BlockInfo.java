package harmonised.explosiont.util;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.world.level.block.state.*;

import javax.annotation.Nullable;

public class BlockInfo
{
    public ResourceLocation dimResLoc;
    public BlockState state;
    public BlockPos pos;
    public CompoundTag tileEntityNBT;
    public int ticksLeft;
    public int type;
    public boolean forceHeal = false;

    public BlockInfo(ResourceLocation dimResLoc, BlockState state, BlockPos blockPos, int ticksLeft, int type, @Nullable CompoundTag tileEntityNBT)
    {
        this.dimResLoc = dimResLoc;
        this.state = state;
        this.pos = blockPos;
        this.ticksLeft = ticksLeft;
        this.type = type;
        this.tileEntityNBT = tileEntityNBT;
    }
}
