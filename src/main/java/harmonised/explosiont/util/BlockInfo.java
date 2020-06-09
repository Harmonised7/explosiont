package harmonised.explosiont.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockInfo
{
    public BlockState state;
    public BlockPos pos;

    public BlockInfo( BlockState state, BlockPos blockPos )
    {
        this.state = state;
        this.pos = blockPos;
    }
}
