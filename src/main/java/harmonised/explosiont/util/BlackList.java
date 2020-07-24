package harmonised.explosiont.util;

import java.util.HashSet;
import java.util.Set;

public class BlackList
{
    public static final Set<String> blackList = new HashSet<>();

    public static void init()
    {
        blackList.add( "minecraft:air" );
        blackList.add( "minecraft:cave_air" );
        blackList.add( "minecraft:void_air" );
        blackList.add( "minecraft:fire" );

        blackList.add( "gravestone:gravestone" );
    }
}
