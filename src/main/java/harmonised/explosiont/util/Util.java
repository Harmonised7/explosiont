package harmonised.explosiont.util;

import net.minecraft.core.BlockPos;

import java.util.Comparator;

public class Util
{


    public static Comparator<BlockInfo> blockInfoComparator = new Comparator<BlockInfo>()
    {
        @Override
        public int compare(BlockInfo a, BlockInfo b)
        {
            return sortByY(a, b);
        }
    };

    public static Comparator<BlockPos> blockPosComparator = new Comparator<BlockPos>()
    {
        @Override
        public int compare(BlockPos a, BlockPos b)
        {
            return sortByY(a, b);
        }
    };

    public static int sortByY(BlockInfo a, BlockInfo b)
    {
        return sortByY(a.pos, b.pos);
    }

    public static int sortByY(BlockPos a, BlockPos b)
    {
        return a.getY() - b.getY();
    }
}
