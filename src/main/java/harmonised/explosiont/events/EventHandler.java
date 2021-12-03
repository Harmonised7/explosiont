package harmonised.explosiont.events;

import net.minecraftforge.event.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void detonation(ExplosionEvent.Detonate event)
    {
        ExplosionHandler.handleExplosion(event);
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        WorldTickHandler.handleWorldTick(event);
    }

    @SubscribeEvent
    public static void chunkDataLoad(ChunkDataEvent.Load event)
    {
        ChunkDataHandler.handleChunkDataLoad(event);
    }

    @SubscribeEvent
    public static void chunkDataSave(ChunkDataEvent.Save event)
    {
        ChunkDataHandler.handleChunkDataSave(event);
    }
}
