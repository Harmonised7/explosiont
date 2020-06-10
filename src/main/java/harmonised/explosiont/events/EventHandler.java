package harmonised.explosiont.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void detonation( ExplosionEvent.Detonate event )
    {
        ExplosionHandler.handleExplosion( event );
    }

    @SubscribeEvent
    public static void worldTick( TickEvent.WorldTickEvent event )
    {
        WorldTickHandler.handleWorldTick( event );
    }
}
