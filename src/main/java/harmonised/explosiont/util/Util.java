package harmonised.explosiont.util;

import net.minecraft.core.BlockPos;

import java.util.Comparator;

public class Util
{


    public static Comparator<BlockInfo> blockInfoComparator = Util::sortByY;

    public static Comparator<BlockPos> blockPosComparator = Util::sortByY;

    public static int sortByY(BlockInfo a, BlockInfo b)
    {
        return sortByY(a.pos, b.pos);
    }

    public static int sortByY(BlockPos a, BlockPos b)
    {
        return a.getY() - b.getY();
    }
}
