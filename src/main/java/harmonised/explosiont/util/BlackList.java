package harmonised.explosiont.util;

import harmonised.explosiont.config.Config;

import java.util.HashSet;
import java.util.Set;

public class BlackList
{
    public static Filter filterType = Config.config.filterType.get() ? Filter.WHITELIST : Filter.BLACKLIST;
    public static final Set<String> blackList = new HashSet<>();
    public static Set<String> filter = new HashSet<>();

    public static void init()
    {
        blackList.add( "minecraft:air" );
        blackList.add( "minecraft:cave_air" );
        blackList.add( "minecraft:void_air" );
        blackList.add( "minecraft:fire" );

        blackList.add( "gravestone:gravestone" );
    }

    public static boolean checkBlock( String regName )
    {
        if( BlackList.blackList.contains( regName ) )
            return false;

        if( filterType.equals( Filter.BLACKLIST ) && filter.contains( regName ) )
            return false;

        if( filterType.equals( Filter.WHITELIST ) && !filter.contains( regName ) )
            return false;

        return true;
    }
}
