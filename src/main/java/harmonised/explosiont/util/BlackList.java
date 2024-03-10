package harmonised.explosiont.util;

import harmonised.explosiont.config.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class BlackList
{
    public static Filter filterType = Config.config.filterType.get() ? Filter.WHITELIST : Filter.BLACKLIST;
    public static Filter dimensionFilterType = Config.config.dimensionFilterType.get() ? Filter.WHITELIST : Filter.BLACKLIST;
    public static final Set<ResourceLocation> blackList = new HashSet<>();
    public static Set<ResourceLocation> filter = new HashSet<>();
    public static Set<ResourceLocation> dimensionFilter = new HashSet<>();

    public static void init()
    {
        blackList.add(new ResourceLocation("minecraft", "air"));
        blackList.add(new ResourceLocation("minecraft", "cave_air"));
        blackList.add(new ResourceLocation("minecraft", "void_air"));
        blackList.add(new ResourceLocation("minecraft", "fire"));

        blackList.add(new ResourceLocation("gravestone", "gravestone"));
    }

    public static boolean check(ResourceLocation regKey, Set<ResourceLocation> filterSet, Filter filter)
    {
        if(filterType.equals(Filter.BLACKLIST) && filterSet.contains(regKey))
            return false;

        if(filterType.equals(Filter.WHITELIST) && !filterSet.contains(regKey))
            return false;

        return true;
    }

    public static boolean checkBlock(ResourceLocation regKey)
    {
        if(BlackList.blackList.contains(regKey))
            return false;

        return check(regKey, filter, filterType);
    }

    public static boolean checkDimension(ResourceLocation regKey)
    {
        return check(regKey, dimensionFilter, dimensionFilterType);
    }
}
