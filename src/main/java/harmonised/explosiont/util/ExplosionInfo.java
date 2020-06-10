package harmonised.explosiont.util;

import java.util.List;

public class ExplosionInfo
{
    public List<BlockInfo> blocks;
    public double age;

    public ExplosionInfo( List<BlockInfo> blocks, int age )
    {
        this.blocks = blocks;
        this.age = age;
    }
}
