package harmonised.explosiont;

import harmonised.explosiont.commands.ExplosiontCommand;
import harmonised.explosiont.config.Config;
import harmonised.explosiont.events.ChunkDataHandler;
import harmonised.explosiont.util.BlackList;
import harmonised.explosiont.util.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod( Reference.MOD_ID )
public class ExplosiontMod
{
    public ExplosiontMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::modsLoading );
        MinecraftForge.EVENT_BUS.addListener( this::serverAboutToStart );
        MinecraftForge.EVENT_BUS.addListener( this::serverStart );
        Config.init();
    }

    private void modsLoading( FMLCommonSetupEvent event )
    {
        MinecraftForge.EVENT_BUS.register( harmonised.explosiont.events.EventHandler.class );
    }

    private void serverAboutToStart( FMLServerAboutToStartEvent event )
    {
        ChunkDataHandler.init();
        BlackList.init();
    }

    private void serverStart( FMLServerStartingEvent event)
    {
        ExplosiontCommand.register( event.getCommandDispatcher() );
    }
}
