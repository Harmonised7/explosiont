package harmonised.explosiont.util;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockInfo
{
    public ResourceLocation dimResLoc;
    public BlockState state;
    public BlockPos pos;
    public CompoundNBT tileEntityNBT;
    public long time;

    public BlockInfo( ResourceLocation dimResLoc, BlockState state, BlockPos blockPos, long time, @Nullable CompoundNBT tileEntityNBT )
    {
        this.dimResLoc = dimResLoc;
        this.state = state;
        this.pos = blockPos;
        this.time = time;
        this.tileEntityNBT = tileEntityNBT;
    }
}
