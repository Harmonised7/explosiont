package harmonised.explosiont.util;

import java.util.List;

public class ExplosionInfo
{
    public List<BlockInfo> blocks;
    public int age;

    public ExplosionInfo( List<BlockInfo> blocks, int age )
    {
        this.blocks = blocks;
        this.age = age;
    }
}
